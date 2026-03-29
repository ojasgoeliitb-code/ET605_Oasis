// pedagogyEngine.js v2 — Deep Adaptive Pedagogy Engine
// Goes beyond right/wrong: uses learner profile, pacing, style, misconceptions, streaks

const PED_ACTIONS = {
  ADVANCE:              'advance',
  ADVANCE_CHALLENGE:    'advance_challenge',   // NEW: hot-streak → harder variant
  CONTINUE:             'continue',
  SLOW_DOWN:            'slow_down',           // NEW: impulsive → ask to reflect
  OFFER_HINT_L1:        'hint_l1',
  SHOW_VISUAL_L2:       'visual_l2',
  GUIDED_EXAMPLE_L3:    'guided_l3',
  FULL_REMEDIAL:        'remedial',
  TARGETED_REMEDIAL:    'targeted_remedial',   // NEW: misconception-specific
  SHOW_STORY:           'story',
  METACOGNITIVE_NUDGE:  'metacognitive',       // NEW: hint-dependent → push independence
  ENCOURAGE:            'encourage',           // NEW: cold-streak → emotional support
  STYLE_ADAPTED_HINT:   'style_hint',          // NEW: hint tailored to learning style
};

function getPedagogicalAction(signals, conceptConfig = {}) {
  const {
    masteryScore, struggleIndex, engagementLevel,
    consecutiveWrong, hintsUsed, retriesOnCurrentQuestion, hintDependency,
    learningStyle, pacingProfile, isImpulsive, isDeliberate,
    firstAttemptRate, onHotStreak, onColdStreak,
    gotRightAfterHint, gotRightWithoutHint,
    hintSequence, streakCorrect, streakWrong,
    wrongAnswerPatterns, misconceptions,
  } = signals;

  // ══════════════════════════════════════════════════════════════════════
  // TIER 1 — ADVANCED / MASTERY PATH
  // ══════════════════════════════════════════════════════════════════════

  // Hot streak + zero hints + high mastery → challenge mode
  if (onHotStreak && hintsUsed === 0 && masteryScore >= 0.85 && consecutiveWrong === 0) {
    return {
      action: PED_ACTIONS.ADVANCE_CHALLENGE,
      reason: 'Hot streak with no hint usage — ready for challenge extension',
      message: "🔥 You're on a roll! Here's a trickier twist to test your understanding.",
      badge: '⚡ Challenge Mode',
    };
  }

  // High mastery, clean progress → advance normally
  if (masteryScore >= 0.8 && consecutiveWrong === 0 && retriesOnCurrentQuestion === 0) {
    return {
      action: PED_ACTIONS.ADVANCE,
      reason: 'Clean mastery achieved',
      message: "Great work! Let's move on.",
    };
  }

  // ══════════════════════════════════════════════════════════════════════
  // TIER 2 — EMOTIONAL / MOTIVATIONAL INTERVENTIONS
  // ══════════════════════════════════════════════════════════════════════

  // Cold streak across concepts → emotional support before content help
  if (onColdStreak && streakWrong >= 4) {
    return {
      action: PED_ACTIONS.ENCOURAGE,
      reason: 'Extended cold streak — student may be disengaging emotionally',
      message: "💙 This is a tricky topic — even mathematicians found it confusing at first! Let's look at it from a completely fresh angle together.",
      remedialLevel: 1,
    };
  }

  // ══════════════════════════════════════════════════════════════════════
  // TIER 3 — METACOGNITIVE / PACING INTERVENTIONS
  // ══════════════════════════════════════════════════════════════════════

  // Impulsive answerer: getting wrong too fast → slow-down nudge
  if (isImpulsive && consecutiveWrong >= 2) {
    return {
      action: PED_ACTIONS.SLOW_DOWN,
      reason: 'Fast answering + wrong → likely not reading carefully',
      message: "⏸️ Try reading the question again slowly before answering — there's an important detail that easy to miss!",
    };
  }

  // Hint-dependent: always uses hints even when getting correct → metacognitive push
  if (hintDependency > 0.6 && gotRightAfterHint && !gotRightWithoutHint && retriesOnCurrentQuestion === 0) {
    return {
      action: PED_ACTIONS.METACOGNITIVE_NUDGE,
      reason: 'Student succeeds only after hints — needs confidence-building for independent work',
      message: "💪 Before looking at a hint, give it one try on your own! I believe you can work it out. Trust the rules you've learned.",
    };
  }

  // ══════════════════════════════════════════════════════════════════════
  // TIER 4 — MISCONCEPTION-TARGETED REMEDIAL
  // ══════════════════════════════════════════════════════════════════════

  // Same wrong answer chosen 2+ times → targeted misconception correction
  if (wrongAnswerPatterns && Object.keys(wrongAnswerPatterns).length > 0) {
    const repeatedMisconception = Object.entries(wrongAnswerPatterns)
      .find(([, count]) => count >= 2);
    if (repeatedMisconception) {
      return {
        action: PED_ACTIONS.TARGETED_REMEDIAL,
        reason: `Repeated misconception: chose "${repeatedMisconception[0]}" ${repeatedMisconception[1]} times`,
        message: `🎯 I see you keep choosing "${repeatedMisconception[0]}" — let me explain exactly why that's a common trap and how to avoid it!`,
        misconceptionAnswer: repeatedMisconception[0],
        remedialLevel: 2,
      };
    }
  }

  // Persistent failure (3+ wrong) → full remedial with style-awareness
  if (consecutiveWrong >= 3 || struggleIndex > 0.7) {
    const styleMsg = learningStyle === 'visual'
      ? "Let me draw this out step by step on the number line."
      : learningStyle === 'narrative'
      ? "Let's go back to the story context — sometimes seeing it in a real situation makes it click."
      : "Let's break this right down to the basics with a worked example.";
    return {
      action: PED_ACTIONS.FULL_REMEDIAL,
      reason: 'Persistent failure — full remedial with style adaptation',
      message: `🔄 Let's take a step back. ${styleMsg}`,
      remedialLevel: 3,
      learningStyle,
    };
  }

  // ══════════════════════════════════════════════════════════════════════
  // TIER 5 — HINT ESCALATION WITH STYLE PERSONALISATION
  // ══════════════════════════════════════════════════════════════════════

  // Visual learner struggling → go straight to number line (skip text hint)
  if (learningStyle === 'visual' && retriesOnCurrentQuestion >= 1 && hintsUsed === 0) {
    return {
      action: PED_ACTIONS.STYLE_ADAPTED_HINT,
      reason: 'Visual learner — skip L1 text hint, go straight to number line',
      message: '📏 I can see this is tricky. As a visual learner, let me show you this on the number line right away!',
      hintLevel: 2,
    };
  }

  // Narrative learner struggling → offer story/guided example
  if (learningStyle === 'narrative' && retriesOnCurrentQuestion >= 1 && hintsUsed <= 1) {
    return {
      action: PED_ACTIONS.STYLE_ADAPTED_HINT,
      reason: 'Narrative learner — serve worked example with real-world story',
      message: '📖 Let me connect this to a real-world example that might make it click!',
      hintLevel: 3,
    };
  }

  // High hint usage → escalate to visual
  if (hintsUsed > 2) {
    return {
      action: PED_ACTIONS.SHOW_VISUAL_L2,
      reason: 'High hint usage — reinforce with visual',
      message: 'Let me show you this on the number line.',
    };
  }

  // Retry escalation
  if (retriesOnCurrentQuestion >= 1) {
    const hintLevel = Math.min(3, retriesOnCurrentQuestion);
    return {
      action: hintLevel === 1 ? PED_ACTIONS.OFFER_HINT_L1
             : hintLevel === 2 ? PED_ACTIONS.SHOW_VISUAL_L2
             : PED_ACTIONS.GUIDED_EXAMPLE_L3,
      reason: `Retry #${retriesOnCurrentQuestion} — escalating support`,
      message: 'Need a hint? Here\'s a clue.',
      hintLevel,
    };
  }

  // Low engagement → story re-engagement
  if (engagementLevel < 0.4) {
    return {
      action: PED_ACTIONS.SHOW_STORY,
      reason: 'Low engagement',
      message: "Here's a story that might help!",
    };
  }

  // Default
  return {
    action: PED_ACTIONS.CONTINUE,
    reason: 'Normal progress',
    message: null,
  };
}

// ── Generate personalised feedback message from ped action ──────────────
function getPersonalisedFeedbackMessage(pedAction, signals) {
  const { learningStyle, pacingProfile, onHotStreak, masteryScore } = signals;

  switch (pedAction.action) {
    case PED_ACTIONS.ADVANCE_CHALLENGE:
      return `🔥 Excellent! You've mastered this concept — try this extension question!`;
    case PED_ACTIONS.SLOW_DOWN:
      return `⏸️ Take your time — re-read the question carefully. There's a subtle detail!`;
    case PED_ACTIONS.METACOGNITIVE_NUDGE:
      return `💡 Try without a hint first — you know more than you think!`;
    case PED_ACTIONS.ENCOURAGE:
      return `💙 This is genuinely hard. Every mistake teaches your brain something. You're getting there!`;
    case PED_ACTIONS.TARGETED_REMEDIAL:
      return pedAction.message;
    case PED_ACTIONS.STYLE_ADAPTED_HINT:
      return pedAction.message;
    default:
      return pedAction.message;
  }
}
