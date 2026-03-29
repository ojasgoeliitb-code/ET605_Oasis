package com.robotegers.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Payload the JS frontend sends to POST /api/session/{id}/sync.
 * Contains learner model signals for a single concept, plus the PED action
 * the frontend already computed — Java validates, persists, and may override.
 */
public class SyncRequest {

    @JsonProperty("concept_id")
    private String conceptId;

    // Learner model signals (computed by JS LearnerModel.getSignals())
    @JsonProperty("mastery_score")
    private double masteryScore;

    @JsonProperty("struggle_index")
    private double struggleIndex;

    @JsonProperty("engagement_level")
    private double engagementLevel;

    @JsonProperty("hint_dependency")
    private double hintDependency;

    @JsonProperty("consecutive_wrong")
    private int consecutiveWrong;

    @JsonProperty("hints_used")
    private int hintsUsed;

    @JsonProperty("retries_on_current_question")
    private int retriesOnCurrentQuestion;

    @JsonProperty("robot_fix_rate")
    private double robotFixRate;

    // PED action the JS engine decided
    @JsonProperty("ped_action")
    private String pedAction;

    @JsonProperty("ped_reason")
    private String pedReason;

    // Rolling Merge-schema counters (from LearnerModel.session)
    @JsonProperty("correct_answers")
    private int correctAnswers;

    @JsonProperty("wrong_answers")
    private int wrongAnswers;

    @JsonProperty("questions_attempted")
    private int questionsAttempted;

    @JsonProperty("retry_count")
    private int retryCount;

    @JsonProperty("hints_used_total")
    private int hintsUsedTotal;

    @JsonProperty("topic_units_completed")
    private int topicUnitsCompleted;

    // Getters
    public String getConceptId()               { return conceptId; }
    public double getMasteryScore()            { return masteryScore; }
    public double getStruggleIndex()           { return struggleIndex; }
    public double getEngagementLevel()         { return engagementLevel; }
    public double getHintDependency()          { return hintDependency; }
    public int getConsecutiveWrong()           { return consecutiveWrong; }
    public int getHintsUsed()                  { return hintsUsed; }
    public int getRetriesOnCurrentQuestion()   { return retriesOnCurrentQuestion; }
    public double getRobotFixRate()            { return robotFixRate; }
    public String getPedAction()               { return pedAction; }
    public String getPedReason()               { return pedReason; }
    public int getCorrectAnswers()             { return correctAnswers; }
    public int getWrongAnswers()               { return wrongAnswers; }
    public int getQuestionsAttempted()         { return questionsAttempted; }
    public int getRetryCount()                 { return retryCount; }
    public int getHintsUsedTotal()             { return hintsUsedTotal; }
    public int getTopicUnitsCompleted()        { return topicUnitsCompleted; }

    // Setters
    public void setConceptId(String v)               { this.conceptId = v; }
    public void setMasteryScore(double v)            { this.masteryScore = v; }
    public void setStruggleIndex(double v)           { this.struggleIndex = v; }
    public void setEngagementLevel(double v)         { this.engagementLevel = v; }
    public void setHintDependency(double v)          { this.hintDependency = v; }
    public void setConsecutiveWrong(int v)           { this.consecutiveWrong = v; }
    public void setHintsUsed(int v)                  { this.hintsUsed = v; }
    public void setRetriesOnCurrentQuestion(int v)   { this.retriesOnCurrentQuestion = v; }
    public void setRobotFixRate(double v)            { this.robotFixRate = v; }
    public void setPedAction(String v)               { this.pedAction = v; }
    public void setPedReason(String v)               { this.pedReason = v; }
    public void setCorrectAnswers(int v)             { this.correctAnswers = v; }
    public void setWrongAnswers(int v)               { this.wrongAnswers = v; }
    public void setQuestionsAttempted(int v)         { this.questionsAttempted = v; }
    public void setRetryCount(int v)                 { this.retryCount = v; }
    public void setHintsUsedTotal(int v)             { this.hintsUsedTotal = v; }
    public void setTopicUnitsCompleted(int v)        { this.topicUnitsCompleted = v; }
}
