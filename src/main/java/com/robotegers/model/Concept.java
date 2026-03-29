package com.robotegers.model;

import java.util.List;

/**
 * A single concept within a subtopic (e.g. "1.1 — What is an integer?").
 * Each Module contains 4 Concepts; each Concept has 1–3 Questions.
 * The JS LearnerModel tracks signals per conceptId.
 */
public class Concept {

    private String conceptId;       // e.g. "m1_c1", "m3_c2"
    private String title;           // e.g. "What is an integer?"
    private String explanation;     // full concept explanation
    private String storyExample;    // real-world story
    private List<Question> questions;

    public Concept() {}

    public Concept(String conceptId, String title, String explanation,
                   String storyExample, List<Question> questions) {
        this.conceptId   = conceptId;
        this.title       = title;
        this.explanation = explanation;
        this.storyExample = storyExample;
        this.questions   = questions;
    }

    public String getConceptId()    { return conceptId; }
    public String getTitle()        { return title; }
    public String getExplanation()  { return explanation; }
    public String getStoryExample() { return storyExample; }
    public List<Question> getQuestions() { return questions; }

    public void setConceptId(String conceptId)       { this.conceptId = conceptId; }
    public void setTitle(String title)               { this.title = title; }
    public void setExplanation(String explanation)   { this.explanation = explanation; }
    public void setStoryExample(String storyExample) { this.storyExample = storyExample; }
    public void setQuestions(List<Question> questions){ this.questions = questions; }
}
