package com.robotegers.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Java's response to a /sync call.
 * Confirms or overrides the JS PED decision and carries any
 * server-side content needed (story text, remedial explanation, etc.).
 */
public class PedResponse {

    @JsonProperty("action")
    private String action;          // same PED_ACTIONS constants

    @JsonProperty("confirmed")
    private boolean confirmed;      // did Java agree with JS?

    @JsonProperty("message")
    private String message;         // UI message to show student

    @JsonProperty("reason")
    private String reason;          // logging / debug

    @JsonProperty("remedial_content")
    private String remedialContent; // L4 concept re-explanation text (nullable)

    @JsonProperty("story_content")
    private String storyContent;    // story text for SHOW_STORY (nullable)

    @JsonProperty("concept_id")
    private String conceptId;

    public PedResponse() {}

    public static PedResponse confirm(String action, String message, String reason) {
        PedResponse r = new PedResponse();
        r.action    = action;
        r.confirmed = true;
        r.message   = message;
        r.reason    = reason;
        return r;
    }

    public static PedResponse override(String newAction, String message, String reason) {
        PedResponse r = new PedResponse();
        r.action    = newAction;
        r.confirmed = false;
        r.message   = message;
        r.reason    = reason;
        return r;
    }

    // Getters & setters
    public String  getAction()          { return action; }
    public boolean isConfirmed()        { return confirmed; }
    public String  getMessage()         { return message; }
    public String  getReason()          { return reason; }
    public String  getRemedialContent() { return remedialContent; }
    public String  getStoryContent()    { return storyContent; }
    public String  getConceptId()       { return conceptId; }

    public void setAction(String v)          { this.action = v; }
    public void setConfirmed(boolean v)      { this.confirmed = v; }
    public void setMessage(String v)         { this.message = v; }
    public void setReason(String v)          { this.reason = v; }
    public void setRemedialContent(String v) { this.remedialContent = v; }
    public void setStoryContent(String v)    { this.storyContent = v; }
    public void setConceptId(String v)       { this.conceptId = v; }
}
