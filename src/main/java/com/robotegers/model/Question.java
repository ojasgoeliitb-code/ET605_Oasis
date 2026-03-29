package com.robotegers.model;

import java.util.List;

public class Question {
    public enum QuestionType { RULE_TEACHING, PREDICTION, DEBUGGING }

    // Input types for structured questions
    public enum InputType { TEXT, MCQ, DROPDOWN, TRUE_FALSE, FILL_BLANK }

    private String questionId;
    private QuestionType type;
    private InputType inputType;       // how the student answers
    private String prompt;             // teacher's setup / robot's doubt
    private String robotAttempt;       // what the robot says (may be wrong)
    private String correctAnswer;      // correct answer key
    private boolean robotIsWrong;
    private List<String> options;      // for MCQ / DROPDOWN / TRUE_FALSE
    private String hint;               // L1
    private String hintL2;             // L2 label (number line shown automatically)
    private String guidedExample;      // L3 visual example description
    private List<String> acceptedVariants;

    public Question() {}

    // Full constructor
    public Question(String questionId, QuestionType type, InputType inputType,
                    String prompt, String robotAttempt, String correctAnswer,
                    boolean robotIsWrong, List<String> options,
                    String hint, String hintL2, String guidedExample,
                    List<String> acceptedVariants) {
        this.questionId = questionId; this.type = type; this.inputType = inputType;
        this.prompt = prompt; this.robotAttempt = robotAttempt;
        this.correctAnswer = correctAnswer; this.robotIsWrong = robotIsWrong;
        this.options = options; this.hint = hint; this.hintL2 = hintL2;
        this.guidedExample = guidedExample; this.acceptedVariants = acceptedVariants;
    }

    // Convenience: TEXT input, no options
    public Question(String questionId, QuestionType type, String prompt,
                    String robotAttempt, String correctAnswer, boolean robotIsWrong,
                    String hint, String hintL2, String conceptClarification, String guidedExample) {
        this(questionId, type, InputType.TEXT, prompt, robotAttempt, correctAnswer,
             robotIsWrong, List.of(), hint, hintL2, guidedExample, List.of());
    }

    // Convenience: MCQ
    public static Question mcq(String id, QuestionType type, String prompt,
                                String robotAttempt, String correctAnswer, boolean robotIsWrong,
                                List<String> options, String hint, String hintL2, String guided) {
        return new Question(id, type, InputType.MCQ, prompt, robotAttempt, correctAnswer,
                            robotIsWrong, options, hint, hintL2, guided, List.of());
    }

    // Convenience: TRUE_FALSE
    public static Question trueFalse(String id, QuestionType type, String prompt,
                                     String robotAttempt, String correctAnswer, boolean robotIsWrong,
                                     String hint, String hintL2, String guided) {
        return new Question(id, type, InputType.TRUE_FALSE, prompt, robotAttempt, correctAnswer,
                            robotIsWrong, List.of("True", "False"), hint, hintL2, guided, List.of());
    }

    // Convenience: DROPDOWN
    public static Question dropdown(String id, QuestionType type, String prompt,
                                    String robotAttempt, String correctAnswer, boolean robotIsWrong,
                                    List<String> options, String hint, String hintL2, String guided) {
        return new Question(id, type, InputType.DROPDOWN, prompt, robotAttempt, correctAnswer,
                            robotIsWrong, options, hint, hintL2, guided, List.of());
    }

    public String getQuestionId()             { return questionId; }
    public QuestionType getType()             { return type; }
    public InputType getInputType()           { return inputType != null ? inputType : InputType.TEXT; }
    public String getPrompt()                 { return prompt; }
    public String getRobotAttempt()           { return robotAttempt; }
    public String getCorrectAnswer()          { return correctAnswer; }
    public boolean isRobotIsWrong()           { return robotIsWrong; }
    public List<String> getOptions()          { return options != null ? options : List.of(); }
    public String getHint()                   { return hint; }
    public String getHintL2()                 { return hintL2; }
    public String getGuidedExample()          { return guidedExample; }
    public String getConceptClarification()   { return guidedExample; } // alias
    public List<String> getAcceptedVariants() { return acceptedVariants != null ? acceptedVariants : List.of(); }

    public void setQuestionId(String v)             { questionId = v; }
    public void setType(QuestionType v)             { type = v; }
    public void setInputType(InputType v)           { inputType = v; }
    public void setPrompt(String v)                 { prompt = v; }
    public void setRobotAttempt(String v)           { robotAttempt = v; }
    public void setCorrectAnswer(String v)          { correctAnswer = v; }
    public void setRobotIsWrong(boolean v)          { robotIsWrong = v; }
    public void setOptions(List<String> v)          { options = v; }
    public void setHint(String v)                   { hint = v; }
    public void setHintL2(String v)                 { hintL2 = v; }
    public void setGuidedExample(String v)          { guidedExample = v; }
    public void setAcceptedVariants(List<String> v) { acceptedVariants = v; }
}
