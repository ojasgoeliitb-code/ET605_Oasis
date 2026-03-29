package com.robotegers.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ChapterMetadataPayload {

    @JsonProperty("grade")
    private int grade = 7;

    @JsonProperty("chapter_name")
    private String chapterName = "Integers";

    @JsonProperty("chapter_id")
    private String chapterId = "grade7_integers";

    @JsonProperty("chapter_url")
    private String chapterUrl = "/chapter/grade7_integers";

    @JsonProperty("chapter_difficulty")
    private double chapterDifficulty = 0.55;

    @JsonProperty("expected_completion_time_seconds")
    private int expectedCompletionTimeSeconds = 2400;

    @JsonProperty("subtopics")
    private List<Subtopic> subtopics;

    @JsonProperty("prerequisites")
    private List<String> prerequisites = List.of("grade6_basic_numbers");

    public ChapterMetadataPayload() {
        this.subtopics = List.of(
            new Subtopic("grade7_integers_understanding", "Understanding Integers", 0.30),
            new Subtopic("grade7_integers_number_line", "Integers on the Number Line", 0.35),
            new Subtopic("grade7_integers_addition", "Addition of Integers", 0.50),
            new Subtopic("grade7_integers_subtraction", "Subtraction of Integers", 0.55),
            new Subtopic("grade7_integers_mul_div", "Multiplication and Division of Integers", 0.65)
        );
    }

    public static class Subtopic {
        @JsonProperty("subtopic_id")
        private String subtopicId;
        @JsonProperty("name")
        private String name;
        @JsonProperty("difficulty")
        private double difficulty;

        public Subtopic(String subtopicId, String name, double difficulty) {
            this.subtopicId = subtopicId;
            this.name = name;
            this.difficulty = difficulty;
        }
        public String getSubtopicId() { return subtopicId; }
        public String getName() { return name; }
        public double getDifficulty() { return difficulty; }
    }

    public int getGrade() { return grade; }
    public String getChapterName() { return chapterName; }
    public String getChapterId() { return chapterId; }
    public String getChapterUrl() { return chapterUrl; }
    public double getChapterDifficulty() { return chapterDifficulty; }
    public int getExpectedCompletionTimeSeconds() { return expectedCompletionTimeSeconds; }
    public List<Subtopic> getSubtopics() { return subtopics; }
    public List<String> getPrerequisites() { return prerequisites; }
}
