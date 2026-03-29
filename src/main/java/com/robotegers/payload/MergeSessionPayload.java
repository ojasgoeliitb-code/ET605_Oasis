package com.robotegers.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/** Exact Merge Team session interaction payload — field names per spec. */
public class MergeSessionPayload {

    @JsonProperty("student_id")            private String studentId;
    @JsonProperty("session_id")            private String sessionId;
    @JsonProperty("chapter_id")            private String chapterId;
    @JsonProperty("timestamp")             private String timestamp;
    @JsonProperty("session_status")        private String sessionStatus;
    @JsonProperty("correct_answers")       private Integer correctAnswers;
    @JsonProperty("wrong_answers")         private Integer wrongAnswers;
    @JsonProperty("questions_attempted")   private Integer questionsAttempted;
    @JsonProperty("total_questions")       private Integer totalQuestions;
    @JsonProperty("retry_count")           private Integer retryCount;
    @JsonProperty("hints_used")            private Integer hintsUsed;
    @JsonProperty("total_hints_embedded")  private Integer totalHintsEmbedded;
    @JsonProperty("time_spent_seconds")    private Long timeSpentSeconds;
    @JsonProperty("topic_completion_ratio")private Double topicCompletionRatio;

    // Merge spec: use null (not 0) for missing values
    public boolean isValid() { return validationErrors().isEmpty(); }

    public List<String> validationErrors() {
        List<String> errs = new ArrayList<>();
        if (studentId  == null || studentId.isBlank())  errs.add("student_id missing");
        if (sessionId  == null || sessionId.isBlank())  errs.add("session_id missing");
        if (chapterId  == null || chapterId.isBlank())  errs.add("chapter_id missing");
        if (timestamp  == null)                          errs.add("timestamp missing");
        if (sessionStatus == null ||
            (!sessionStatus.equals("completed") && !sessionStatus.equals("exited_midway")))
            errs.add("session_status must be 'completed' or 'exited_midway'");
        if (correctAnswers   == null) errs.add("correct_answers missing");
        if (wrongAnswers     == null) errs.add("wrong_answers missing");
        if (questionsAttempted==null) errs.add("questions_attempted missing");
        if (totalQuestions   == null) errs.add("total_questions missing");
        if (retryCount       == null) errs.add("retry_count missing");
        if (hintsUsed        == null) errs.add("hints_used missing");
        if (totalHintsEmbedded==null) errs.add("total_hints_embedded missing");
        if (timeSpentSeconds == null) errs.add("time_spent_seconds missing");
        if (topicCompletionRatio==null) errs.add("topic_completion_ratio missing");

        // Sanity checks per Merge spec
        if (correctAnswers != null && wrongAnswers != null && questionsAttempted != null) {
            if (correctAnswers + wrongAnswers > questionsAttempted)
                errs.add("correct+wrong > questions_attempted (violates sanity check)");
        }
        if (questionsAttempted != null && totalQuestions != null) {
            if (questionsAttempted > totalQuestions)
                errs.add("questions_attempted > total_questions");
        }
        if (hintsUsed != null && totalHintsEmbedded != null) {
            if (hintsUsed > totalHintsEmbedded)
                errs.add("hints_used > total_hints_embedded");
        }
        if (topicCompletionRatio != null &&
            (topicCompletionRatio < 0.0 || topicCompletionRatio > 1.0))
            errs.add("topic_completion_ratio must be 0–1");
        return errs;
    }

    // Getters
    public String  getStudentId()            { return studentId; }
    public String  getSessionId()            { return sessionId; }
    public String  getChapterId()            { return chapterId; }
    public String  getTimestamp()            { return timestamp; }
    public String  getSessionStatus()        { return sessionStatus; }
    public Integer getCorrectAnswers()       { return correctAnswers; }
    public Integer getWrongAnswers()         { return wrongAnswers; }
    public Integer getQuestionsAttempted()   { return questionsAttempted; }
    public Integer getTotalQuestions()       { return totalQuestions; }
    public Integer getRetryCount()           { return retryCount; }
    public Integer getHintsUsed()            { return hintsUsed; }
    public Integer getTotalHintsEmbedded()   { return totalHintsEmbedded; }
    public Long    getTimeSpentSeconds()     { return timeSpentSeconds; }
    public Double  getTopicCompletionRatio() { return topicCompletionRatio; }

    // Setters
    public void setStudentId(String v)             { studentId = v; }
    public void setSessionId(String v)             { sessionId = v; }
    public void setChapterId(String v)             { chapterId = v; }
    public void setTimestamp(String v)             { timestamp = v; }
    public void setSessionStatus(String v)         { sessionStatus = v; }
    public void setCorrectAnswers(Integer v)       { correctAnswers = v; }
    public void setWrongAnswers(Integer v)         { wrongAnswers = v; }
    public void setQuestionsAttempted(Integer v)   { questionsAttempted = v; }
    public void setTotalQuestions(Integer v)       { totalQuestions = v; }
    public void setRetryCount(Integer v)           { retryCount = v; }
    public void setHintsUsed(Integer v)            { hintsUsed = v; }
    public void setTotalHintsEmbedded(Integer v)   { totalHintsEmbedded = v; }
    public void setTimeSpentSeconds(Long v)        { timeSpentSeconds = v; }
    public void setTopicCompletionRatio(Double v)  { topicCompletionRatio = v; }
}
