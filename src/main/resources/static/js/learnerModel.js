// learnerModel.js — Deep Adaptive Learner Model v2
// Tracks: mastery, misconception patterns, hint dependency, engagement, learning style, pacing

class LearnerModel {
  constructor(studentId, sessionId, chapterId) {
    this.studentId    = studentId;
    this.sessionId    = sessionId;
    this.chapterId    = chapterId;
    this.sessionStart = Date.now();

    this.session = {
      correctAnswers: 0,
      wrongAnswers:   0,
      questionsAttempted: new Set(),
      totalQuestions:     0,
      retryCount:         0,
      hintsUsed:          0,
      totalHintsEmbedded: 0,
      topicUnitsCompleted: 0,
      topicUnitsTotal:    25,   // 5 subtopics × 5 concepts
      streakCorrect:      0,
      streakWrong:        0,
      speedAnswers:       [],
      hintPatternByType:  {},
      modulePerformance:  {},
      misconceptions:     [],
    };

    this.learningStyle = 'unknown';
    this.styleEvidence = { visual: 0, procedural: 0, narrative: 0 };

    this.pacing = {
      avgResponseMs:    0,
      fastThresholdMs:  8000,
      slowThresholdMs:  40000,
      profile:          'normal',
    };

    this.concepts = {};
  }

  initConcept(conceptId) {
    if (!this.concepts[conceptId]) {
      this.concepts[conceptId] = {
        conceptId,
        startTime:              Date.now(),
        questionStartTime:      Date.now(),
        correct:                0,
        wrong:                  0,
        consecutiveWrong:       0,
        retriesOnCurrentQuestion: 0,
        hintsUsed:              0,
        hintSequence:           [],
        robotFixCorrect:        0,
        robotFixWrong:          0,
        debugSuccess:           0,
        activeTime:             0,
        lastActiveAt:           Date.now(),
        idleThresholdMs:        15000,
        remedialTriggered:      false,
        remedialLevel:          0,
        status:                 'in_progress',
        questionsAttempted:     new Set(),
        responseTimes:          [],
        wrongAnswerTypes:       {},
        gotRightAfterHint:      false,
        gotRightWithoutHint:    false,
        firstAttemptCorrect:    false,
        totalAttempts:          0,
        advancedUnlocked:       false,
      };
    }
    return this.concepts[conceptId];
  }

  markQuestionStart(conceptId) {
    const c = this.initConcept(conceptId);
    c.questionStartTime = Date.now();
  }

  recordActivity(conceptId) {
    const c = this.concepts[conceptId];
    if (!c) return;
    const now = Date.now();
    const gap = now - c.lastActiveAt;
    if (gap < c.idleThresholdMs) c.activeTime += gap;
    c.lastActiveAt = now;
  }

  recordAnswer(conceptId, questionId, isCorrect, isRetry = false, chosenAnswer = null) {
    const c = this.initConcept(conceptId);
    this.recordActivity(conceptId);

    const responseMs = Date.now() - c.questionStartTime;
    c.responseTimes.push(responseMs);
    this.session.speedAnswers.push(responseMs);
    this._updatePacingProfile();

    if (!c.questionsAttempted.has(questionId)) {
      c.questionsAttempted.add(questionId);
      this.session.questionsAttempted.add(questionId);
      c.totalAttempts = 0;
    }
    c.totalAttempts++;

    if (isRetry) { c.retriesOnCurrentQuestion++; this.session.retryCount++; }
    else           { c.retriesOnCurrentQuestion = 0; }

    if (isCorrect) {
      c.correct++; c.consecutiveWrong = 0;
      this.session.correctAnswers++;
      this.session.streakCorrect++;
      this.session.streakWrong = 0;
      if (!isRetry) c.firstAttemptCorrect = true;
      if (c.hintsUsed > 0) c.gotRightAfterHint = true;
      else c.gotRightWithoutHint = true;
      if (c.hintsUsed === 0) this.styleEvidence.procedural++;
    } else {
      c.wrong++; c.consecutiveWrong++;
      this.session.wrongAnswers++;
      this.session.streakWrong++;
      this.session.streakCorrect = 0;
      if (chosenAnswer) {
        c.wrongAnswerTypes[chosenAnswer] = (c.wrongAnswerTypes[chosenAnswer] || 0) + 1;
        this._recordMisconception(conceptId, chosenAnswer, c.consecutiveWrong);
      }
    }

    const modId = conceptId.split('_')[0];
    if (!this.session.modulePerformance[modId]) {
      this.session.modulePerformance[modId] = { correct: 0, wrong: 0, time: 0 };
    }
    if (isCorrect) this.session.modulePerformance[modId].correct++;
    else this.session.modulePerformance[modId].wrong++;
    this.session.modulePerformance[modId].time += responseMs;
  }

  recordHintUsed(conceptId, level) {
    const c = this.initConcept(conceptId);
    c.hintsUsed++;
    c.hintSequence.push('l' + level);
    this.session.hintsUsed++;
    const key = 'l' + level;
    this.session.hintPatternByType[key] = (this.session.hintPatternByType[key] || 0) + 1;
    if (level === 2) this.styleEvidence.visual++;
    if (level === 3) this.styleEvidence.narrative++;
    this._updateLearningStyle();
  }

  recordRobotInteraction(conceptId, correctionWasCorrect) {
    const c = this.initConcept(conceptId);
    if (correctionWasCorrect) { c.robotFixCorrect++; c.debugSuccess++; c.consecutiveWrong = 0; }
    else                       { c.robotFixWrong++; }
    this.recordActivity(conceptId);
  }

  getSignals(conceptId) {
    const c = this.concepts[conceptId];
    if (!c) return null;
    const attempted = c.questionsAttempted.size;
    const total     = attempted + 0.001;
    const hintPenalty  = Math.min(0.2, c.hintsUsed * 0.04);
    const retryPenalty = Math.min(0.15, c.retriesOnCurrentQuestion * 0.05);
    const masteryScore = attempted > 0
      ? Math.max(0, (c.correct / total) - hintPenalty - retryPenalty) : 0;
    const struggleIndex = Math.min(1,
      (c.consecutiveWrong * 0.25) +
      (c.hintsUsed > 2 ? 0.3 : c.hintsUsed * 0.1) +
      (c.retriesOnCurrentQuestion > 2 ? 0.2 : 0));
    const totalConceptTime = Date.now() - c.startTime;
    const engagementLevel  = totalConceptTime > 0 ? Math.min(1, c.activeTime / totalConceptTime) : 1;
    const hintDependency   = attempted > 0 ? Math.min(1, c.hintsUsed / (attempted * 3)) : 0;
    const avgRespMs        = c.responseTimes.length > 0
      ? c.responseTimes.reduce((a, b) => a + b, 0) / c.responseTimes.length : 0;
    const firstAttemptRate = c.totalAttempts > 0 ? c.correct / c.totalAttempts : 0;

    return {
      masteryScore:              parseFloat(masteryScore.toFixed(2)),
      struggleIndex:             parseFloat(struggleIndex.toFixed(2)),
      engagementLevel:           parseFloat(engagementLevel.toFixed(2)),
      hintDependency:            parseFloat(hintDependency.toFixed(2)),
      consecutiveWrong:          c.consecutiveWrong,
      hintsUsed:                 c.hintsUsed,
      retriesOnCurrentQuestion:  c.retriesOnCurrentQuestion,
      robotFixRate:              c.robotFixCorrect / Math.max(1, c.robotFixCorrect + c.robotFixWrong),
      learningStyle:             this.learningStyle,
      pacingProfile:             this.pacing.profile,
      isImpulsive:               avgRespMs > 0 && avgRespMs < this.pacing.fastThresholdMs,
      isDeliberate:              avgRespMs > this.pacing.slowThresholdMs,
      firstAttemptRate:          parseFloat(firstAttemptRate.toFixed(2)),
      onHotStreak:               this.session.streakCorrect >= 3,
      onColdStreak:              this.session.streakWrong >= 3,
      gotRightAfterHint:         c.gotRightAfterHint,
      gotRightWithoutHint:       c.gotRightWithoutHint,
      hintSequence:              [...c.hintSequence],
      streakCorrect:             this.session.streakCorrect,
      streakWrong:               this.session.streakWrong,
      wrongAnswerPatterns:       { ...c.wrongAnswerTypes },
      misconceptions:            this.session.misconceptions.filter(m => m.conceptId === conceptId),
      advancedUnlocked:          c.advancedUnlocked,
    };
  }

  markConceptComplete(conceptId) {
    const c = this.concepts[conceptId];
    if (c) {
      const sig = this.getSignals(conceptId) || { masteryScore: 0 };
      c.status = sig.masteryScore >= 0.7 ? 'mastered' : 'needs_remedial';
      if (sig.masteryScore >= 0.9 && sig.hintsUsed === 0) c.advancedUnlocked = true;
      this.session.topicUnitsCompleted++;
    }
  }

  _updatePacingProfile() {
    const times = this.session.speedAnswers;
    if (times.length < 2) return;
    const avg = times.reduce((a, b) => a + b, 0) / times.length;
    this.pacing.avgResponseMs = avg;
    if (avg < this.pacing.fastThresholdMs) this.pacing.profile = 'fast';
    else if (avg > this.pacing.slowThresholdMs) this.pacing.profile = 'slow';
    else this.pacing.profile = 'normal';
  }

  _updateLearningStyle() {
    const ev = this.styleEvidence;
    const max = Math.max(ev.visual, ev.procedural, ev.narrative);
    if (max === 0) { this.learningStyle = 'unknown'; return; }
    if (ev.visual >= max) this.learningStyle = 'visual';
    else if (ev.procedural >= max) this.learningStyle = 'procedural';
    else this.learningStyle = 'narrative';
  }

  _recordMisconception(conceptId, chosenAnswer, count) {
    const existing = this.session.misconceptions.find(
      m => m.conceptId === conceptId && m.answer === chosenAnswer
    );
    if (existing) existing.count++;
    else this.session.misconceptions.push({ conceptId, answer: chosenAnswer, count });
  }

  buildSyncPayload(conceptId, pedAction, pedReason) {
    const signals = this.getSignals(conceptId) || {};
    return {
      concept_id: conceptId,
      mastery_score: signals.masteryScore ?? 0,
      struggle_index: signals.struggleIndex ?? 0,
      engagement_level: signals.engagementLevel ?? 1,
      hint_dependency: signals.hintDependency ?? 0,
      consecutive_wrong: signals.consecutiveWrong ?? 0,
      hints_used: signals.hintsUsed ?? 0,
      retries_on_current_question: signals.retriesOnCurrentQuestion ?? 0,
      robot_fix_rate: signals.robotFixRate ?? 1,
      learning_style: signals.learningStyle ?? 'unknown',
      pacing_profile: signals.pacingProfile ?? 'normal',
      first_attempt_rate: signals.firstAttemptRate ?? 0,
      on_hot_streak: signals.onHotStreak ?? false,
      on_cold_streak: signals.onColdStreak ?? false,
      ped_action: pedAction,
      ped_reason: pedReason,
      correct_answers: this.session.correctAnswers,
      wrong_answers: this.session.wrongAnswers,
      questions_attempted: this.session.questionsAttempted.size,
      retry_count: this.session.retryCount,
      hints_used_total: this.session.hintsUsed,
      topic_units_completed: this.session.topicUnitsCompleted,
    };
  }

  buildMergePayload(sessionStatus = 'completed') {
    return {
      student_id: this.studentId,
      session_id: this.sessionId,
      chapter_id: this.chapterId,
      timestamp: new Date().toISOString(),
      session_status: sessionStatus,
      correct_answers: this.session.correctAnswers,
      wrong_answers: this.session.wrongAnswers,
      questions_attempted: this.session.questionsAttempted.size,
      total_questions: this.session.totalQuestions,
      retry_count: this.session.retryCount,
      hints_used: this.session.hintsUsed,
      total_hints_embedded: this.session.totalHintsEmbedded,
      time_spent_seconds: Math.round((Date.now() - this.sessionStart) / 1000),
      topic_completion_ratio: parseFloat((this.session.topicUnitsCompleted / this.session.topicUnitsTotal).toFixed(2)),
      learning_style: this.learningStyle,
      pacing_profile: this.pacing.profile,
      module_performance: this.session.modulePerformance,
    };
  }
}
