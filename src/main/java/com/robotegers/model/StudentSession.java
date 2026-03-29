package com.robotegers.model;

import com.robotegers.payload.SyncRequest;

import java.time.Instant;
import java.util.*;

/**
 * Server-side session state.
 * The JS LearnerModel is the source of truth for per-concept signals;
 * this class caches the latest sync snapshot from each concept and
 * accumulates the Merge-schema counters.
 */
public class StudentSession {

    private String sessionId;
    private String studentId;
    private String chapterId;

    // Navigation
    private String currentModuleId;
    private String currentConceptId;
    private int    currentQuestionIndex;

    // Merge Team counters — kept in sync via /sync calls
    private int    correctAnswers       = 0;
    private int    wrongAnswers         = 0;
    private int    retryCount           = 0;
    private int    hintsUsed            = 0;
    private int    totalHintsEmbedded   = 0;
    private int    totalQuestions       = 0;
    private int    questionsAttempted   = 0;
    private long   timeSpentSeconds     = 0;
    private double topicCompletionRatio = 0.0;
    private Instant sessionStart;
    private String sessionStatus = "in_progress";

    // Per-concept signal snapshots (conceptId → last SyncRequest)
    private final Map<String, SyncRequest> conceptSnapshots = new LinkedHashMap<>();

    // Per-module mastery computed from concept snapshots
    private final Map<String, Double> moduleMastery = new LinkedHashMap<>();

    public StudentSession() {}

    public StudentSession(String sessionId, String studentId, String chapterId) {
        this.sessionId   = sessionId;
        this.studentId   = studentId;
        this.chapterId   = chapterId;
        this.sessionStart = Instant.now();
    }

    // ── Sync from JS learner model ────────────────────────────────────────────

    public void applySync(SyncRequest req) {
        conceptSnapshots.put(req.getConceptId(), req);
        // Overwrite rolling counters with the authoritative JS values
        this.correctAnswers     = req.getCorrectAnswers();
        this.wrongAnswers       = req.getWrongAnswers();
        this.questionsAttempted = req.getQuestionsAttempted();
        this.retryCount         = req.getRetryCount();
        this.hintsUsed          = req.getHintsUsedTotal();
        recalcTopicCompletion(req.getTopicUnitsCompleted());
    }

    private void recalcTopicCompletion(int unitsCompleted) {
        // 18 concepts total across 5 modules
        this.topicCompletionRatio = Math.min(1.0,
            Math.round((unitsCompleted / 18.0) * 100.0) / 100.0);
    }

    public void markModuleMastery(String moduleId) {
        // Average mastery across concepts belonging to this module
        double sum = 0; int count = 0;
        for (Map.Entry<String, SyncRequest> e : conceptSnapshots.entrySet()) {
            if (e.getKey().startsWith(moduleId + "_")) {
                sum += e.getValue().getMasteryScore();
                count++;
            }
        }
        moduleMastery.put(moduleId, count > 0
            ? Math.round((sum / count) * 100.0) / 100.0 : 0.0);
    }

    public void finalise(String status) {
        this.sessionStatus = status;
        if (sessionStart != null)
            this.timeSpentSeconds = Instant.now().getEpochSecond() - sessionStart.getEpochSecond();
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────
    public String getSessionId()             { return sessionId; }
    public void   setSessionId(String v)     { this.sessionId = v; }
    public String getStudentId()             { return studentId; }
    public void   setStudentId(String v)     { this.studentId = v; }
    public String getChapterId()             { return chapterId; }
    public void   setChapterId(String v)     { this.chapterId = v; }
    public String getCurrentModuleId()       { return currentModuleId; }
    public void   setCurrentModuleId(String v){ this.currentModuleId = v; }
    public String getCurrentConceptId()      { return currentConceptId; }
    public void   setCurrentConceptId(String v){ this.currentConceptId = v; }
    public int    getCurrentQuestionIndex()  { return currentQuestionIndex; }
    public void   setCurrentQuestionIndex(int v){ this.currentQuestionIndex = v; }
    public int    getCorrectAnswers()        { return correctAnswers; }
    public int    getWrongAnswers()          { return wrongAnswers; }
    public int    getRetryCount()            { return retryCount; }
    public int    getHintsUsed()             { return hintsUsed; }
    public int    getTotalHintsEmbedded()    { return totalHintsEmbedded; }
    public void   setTotalHintsEmbedded(int v){ this.totalHintsEmbedded = v; }
    public int    getTotalQuestions()        { return totalQuestions; }
    public void   setTotalQuestions(int v)   { this.totalQuestions = v; }
    public int    getQuestionsAttempted()    { return questionsAttempted; }
    public long   getTimeSpentSeconds()      { return timeSpentSeconds; }
    public double getTopicCompletionRatio()  { return topicCompletionRatio; }
    public String getSessionStatus()         { return sessionStatus; }
    public Map<String, SyncRequest> getConceptSnapshots() { return conceptSnapshots; }
    public Map<String, Double> getModuleMastery()         { return moduleMastery; }
}
