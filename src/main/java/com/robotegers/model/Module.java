package com.robotegers.model;

import java.util.List;

public class Module {
    private String moduleId;
    private String title;
    private double difficulty;
    private List<Concept> concepts;

    public Module() {}

    public Module(String moduleId, String title, double difficulty, List<Concept> concepts) {
        this.moduleId   = moduleId;
        this.title      = title;
        this.difficulty = difficulty;
        this.concepts   = concepts;
    }

    public String getModuleId()          { return moduleId; }
    public String getTitle()             { return title; }
    public double getDifficulty()        { return difficulty; }
    public List<Concept> getConcepts()   { return concepts; }

    public void setModuleId(String v)        { this.moduleId = v; }
    public void setTitle(String v)           { this.title = v; }
    public void setDifficulty(double v)      { this.difficulty = v; }
    public void setConcepts(List<Concept> v) { this.concepts = v; }

    /** Total questions across all concepts in this module */
    public int getTotalQuestions() {
        return concepts == null ? 0
            : concepts.stream().mapToInt(c -> c.getQuestions().size()).sum();
    }
}
