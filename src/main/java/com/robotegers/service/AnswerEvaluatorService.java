package com.robotegers.service;

import com.robotegers.model.Question;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Local semantic answer evaluator — no external API required.
 *
 * Evaluation pipeline (in order):
 *  1. Exact match (after normalisation)
 *  2. Numeric equality  (handles −6 / -6 / "negative 6" / "minus 6")
 *  3. Accepted variants list (per question)
 *  4. Synonym expansion + keyword coverage
 *  5. Structural rule checks (sign, operation, classification)
 */
@Service
public class AnswerEvaluatorService {

    // ── Synonym map ────────────────────────────────────────────────────────────
    // Each entry: canonical form → set of synonymous phrases
    private static final Map<String, Set<String>> SYNONYMS = new LinkedHashMap<>();
    static {
        put("integer",         "whole number", "counting number", "whole", "int");
        put("positive",        "greater than zero", "above zero", "plus", "gains", "profit");
        put("negative",        "less than zero", "below zero", "minus", "loss", "debt", "deficit");
        put("zero",            "nothing", "neutral", "neither positive nor negative", "0");
        put("add",             "addition", "adding", "sum", "plus", "combine", "total");
        put("subtract",        "subtraction", "subtracting", "minus", "take away", "difference");
        put("multiply",        "multiplication", "multiplying", "times", "product");
        put("divide",          "division", "dividing", "split", "quotient");
        put("opposite",        "flip the sign", "negate", "inverse", "additive inverse");
        put("absolute value",  "distance from zero", "magnitude", "size", "how far from zero");
        put("number line",     "line of numbers", "integer line");
        put("left",            "to the left", "leftward", "smaller direction");
        put("right",           "to the right", "rightward", "larger direction");
        put("same sign",       "both positive", "both negative", "like signs", "same signs");
        put("different sign",  "unlike signs", "opposite signs", "one positive one negative");
        put("undefined",       "no answer", "impossible", "cannot be done", "does not exist",
                               "no solution", "not possible");
        put("commutative",     "order does not matter", "order doesn't matter", "swap");
        put("associative",     "grouping does not matter", "grouping doesn't matter", "regroup");
        put("identity",        "does not change", "stays the same", "unchanged");
        put("decimal",         "decimal number", "has a decimal point", "point");
        put("fraction",        "fractional", "has a fraction", "over", "slash");
        put("mastered",        "correct", "right", "yes", "true");
    }

    private static void put(String canonical, String... synonyms) {
        Set<String> set = new HashSet<>(Arrays.asList(synonyms));
        set.add(canonical);
        SYNONYMS.put(canonical, set);
    }

    // ── Stop words ─────────────────────────────────────────────────────────────
    private static final Set<String> STOP = Set.of(
        "the","and","are","is","a","an","in","on","of","to","it","be","as",
        "at","or","but","not","with","for","from","by","was","were","been",
        "have","has","had","this","that","these","those","they","them","their",
        "what","which","when","where","who","will","would","can","could","should",
        "just","also","both","even","then","than","into","because","since","so"
    );

    // ── Numeric patterns ───────────────────────────────────────────────────────
    private static final Pattern NUM_PATTERN =
        Pattern.compile("(?:negative|minus|−|-)?\\s*\\d+(?:\\.\\d+)?");

    // ── Main evaluate method ───────────────────────────────────────────────────

    public record EvalResult(boolean correct, boolean partialCredit, String feedback) {}

    public EvalResult evaluate(String studentRaw, Question question) {
        if (studentRaw == null || studentRaw.isBlank())
            return new EvalResult(false, false, "No answer provided.");

        String student = normalise(studentRaw);
        String correct = normalise(question.getCorrectAnswer());

        // 1. Exact match after normalisation
        if (student.equals(correct))
            return new EvalResult(true, false, "Correct!");

        // For structured input types (MCQ, DROPDOWN, TRUE_FALSE), the answer must
        // be an exact option match — skip fuzzy/keyword/numeric matching to avoid
        // false positives from similar option text.
        var inputType = question.getInputType();
        if (inputType == Question.InputType.MCQ ||
            inputType == Question.InputType.DROPDOWN ||
            inputType == Question.InputType.TRUE_FALSE) {
            return new EvalResult(false, false,
                "That's not the right answer. Think about what makes each option different.");
        }

        // 2. Numeric equality (TEXT input only from here)
        EvalResult numResult = numericCheck(student, correct);
        if (numResult != null) return numResult;

        // 3. Accepted variants
        if (question.getAcceptedVariants() != null) {
            for (String variant : question.getAcceptedVariants()) {
                if (student.equals(normalise(variant)))
                    return new EvalResult(true, false, "Correct!");
                // fuzzy variant match
                if (coverageScore(student, normalise(variant)) >= 0.75)
                    return new EvalResult(true, false, "Correct!");
            }
        }

        // 4. Yes/No handling
        EvalResult ynResult = yesNoCheck(student, correct);
        if (ynResult != null) return ynResult;

        // 5. Structural rule checks (sign, classification, operation)
        EvalResult structResult = structuralCheck(student, correct, question);
        if (structResult != null) return structResult;

        // 6. Synonym-expanded keyword coverage
        double score = coverageScore(expand(student), expand(correct));
        if (score >= 0.70) return new EvalResult(true, false, "Correct — good explanation!");
        if (score >= 0.50) return new EvalResult(false, true,
            "You're on the right track, but your answer is missing some key details. "
            + hintAtMissing(student, correct));

        return new EvalResult(false, false,
            "That's not quite right. " + hintAtMissing(student, correct));
    }

    // ── Step 2: Numeric ────────────────────────────────────────────────────────

    private EvalResult numericCheck(String student, String correct) {
        Double sNum = extractNumber(student);
        Double cNum = extractNumber(correct);
        if (sNum == null || cNum == null) return null;
        if (Math.abs(sNum - cNum) < 0.0001)
            return new EvalResult(true, false, "Correct!");
        return new EvalResult(false, false,
            "The number isn't right — check your calculation.");
    }

    private Double extractNumber(String s) {
        // "negative 6", "minus 6", "−6", "-6", "6"
        s = s.replaceAll("negative\\s+", "-").replaceAll("minus\\s+", "-")
             .replaceAll("−", "-").replaceAll("\\+", "").trim();
        Matcher m = NUM_PATTERN.matcher(s);
        // find the last standalone number (handles "answer is -6")
        Double last = null;
        while (m.find()) {
            try { last = Double.parseDouble(m.group().replaceAll("\\s+", "")); }
            catch (NumberFormatException ignored) {}
        }
        return last;
    }

    // ── Step 4: Yes/No ─────────────────────────────────────────────────────────

    private EvalResult yesNoCheck(String student, String correct) {
        Set<String> YES = Set.of("yes","y","yep","yeah","correct","true","right","indeed");
        Set<String> NO  = Set.of("no","n","nope","nah","wrong","false","incorrect","not");
        if (correct.equals("yes") || correct.startsWith("yes")) {
            if (YES.stream().anyMatch(student::startsWith))
                return new EvalResult(true, false, "Correct!");
            if (NO.stream().anyMatch(student::startsWith))
                return new EvalResult(false, false, "Actually the answer is yes — check the definition.");
        }
        if (correct.equals("no") || correct.startsWith("no")) {
            if (NO.stream().anyMatch(student::startsWith))
                return new EvalResult(true, false, "Correct!");
            if (YES.stream().anyMatch(student::startsWith))
                return new EvalResult(false, false, "Actually the answer is no — think again.");
        }
        return null;
    }

    // ── Step 5: Structural checks ──────────────────────────────────────────────

    private EvalResult structuralCheck(String student, String correct, Question question) {
        // Sign rule check: does student mention the right sign outcome?
        boolean correctMentionsPositive = correct.contains("positive") || correct.contains("+");
        boolean correctMentionsNegative = correct.contains("negative") || correct.contains("-");
        boolean studentMentionsPositive = student.contains("positive") || student.contains("+");
        boolean studentMentionsNegative = student.contains("negative") || student.contains("-");

        // If answer is purely a sign statement (e.g. "positive", "negative")
        if ((correct.equals("positive") || correct.equals("negative")) ) {
            if (correctMentionsPositive && studentMentionsPositive && !studentMentionsNegative)
                return new EvalResult(true, false, "Correct sign!");
            if (correctMentionsNegative && studentMentionsNegative && !studentMentionsPositive)
                return new EvalResult(true, false, "Correct sign!");
            if (correctMentionsPositive && studentMentionsNegative)
                return new EvalResult(false, false,
                    "The sign is wrong — same signs give a positive result.");
            if (correctMentionsNegative && studentMentionsPositive)
                return new EvalResult(false, false,
                    "The sign is wrong — different signs give a negative result.");
        }

        // Classification check: correct answer lists specific numbers
        if (correct.matches(".*\\d.*")) {
            Set<Integer> correctNums = extractAllIntegers(correct);
            Set<Integer> studentNums = extractAllIntegers(student);
            if (!correctNums.isEmpty() && !studentNums.isEmpty()) {
                if (studentNums.containsAll(correctNums) && correctNums.containsAll(studentNums))
                    return new EvalResult(true, false, "Correct!");
                if (!Collections.disjoint(studentNums, correctNums)) {
                    long missing = correctNums.stream().filter(n -> !studentNums.contains(n)).count();
                    long extra   = studentNums.stream().filter(n -> !correctNums.contains(n)).count();
                    if (missing == 0 && extra > 0)
                        return new EvalResult(false, true,
                            "You included extra numbers that aren't correct. " + hintAtMissing(student, correct));
                    if (missing > 0)
                        return new EvalResult(false, true,
                            "You're missing " + missing + " number(s) from the correct answer.");
                }
            }
        }

        // Operation identity: "add the opposite", "flip the sign" patterns
        if (correct.contains("opposite") || correct.contains("flip")) {
            if (student.contains("opposite") || student.contains("flip") ||
                student.contains("negate") || student.contains("change the sign") ||
                student.contains("switch the sign") || student.contains("convert"))
                return new EvalResult(true, false, "Correct — that's exactly the key rule!");
        }

        // Undefined / division by zero
        if (correct.contains("undefined")) {
            if (student.contains("undefined") || student.contains("no answer") ||
                student.contains("impossible") || student.contains("cannot") ||
                student.contains("doesn't exist") || student.contains("does not exist"))
                return new EvalResult(true, false, "Correct — division by zero is undefined!");
        }

        return null;
    }

    private Set<Integer> extractAllIntegers(String s) {
        Set<Integer> nums = new HashSet<>();
        Matcher m = Pattern.compile("-?\\d+").matcher(s);
        while (m.find()) {
            try { nums.add(Integer.parseInt(m.group())); }
            catch (NumberFormatException ignored) {}
        }
        return nums;
    }

    // ── Step 6: Keyword coverage ───────────────────────────────────────────────

    private double coverageScore(String student, String correct) {
        Set<String> correctTokens = significantTokens(correct);
        if (correctTokens.isEmpty()) return 0;
        long matched = correctTokens.stream()
            .filter(t -> student.contains(t)).count();
        return (double) matched / correctTokens.size();
    }

    private Set<String> significantTokens(String text) {
        Set<String> tokens = new HashSet<>();
        for (String word : text.split("[\\s,;.!?()]+")) {
            String w = word.toLowerCase().replaceAll("[^a-z0-9\\-]", "");
            if (w.length() >= 3 && !STOP.contains(w)) tokens.add(w);
        }
        return tokens;
    }

    // ── Synonym expansion ──────────────────────────────────────────────────────

    private String expand(String text) {
        String result = text;
        for (Map.Entry<String, Set<String>> entry : SYNONYMS.entrySet()) {
            String canonical = entry.getKey();
            for (String syn : entry.getValue()) {
                if (result.contains(syn) && !result.contains(canonical)) {
                    result = result.replace(syn, canonical + " " + syn);
                }
            }
        }
        return result;
    }

    // ── Hint at missing keywords ───────────────────────────────────────────────

    private String hintAtMissing(String student, String correct) {
        Set<String> correctTokens = significantTokens(correct);
        List<String> missing = correctTokens.stream()
            .filter(t -> !student.contains(t))
            .limit(3)
            .toList();
        if (missing.isEmpty()) return "";
        return "Think about: " + String.join(", ", missing) + ".";
    }

    // ── Normaliser ─────────────────────────────────────────────────────────────

    private String normalise(String s) {
        return s.toLowerCase()
            .replaceAll("−", "-")           // Unicode minus → ASCII
            .replaceAll("×", "*")
            .replaceAll("÷", "/")
            .replaceAll("[₹$£€]", "")       // strip currency
            .replaceAll("°[cC]", "")        // strip degree markers
            .replaceAll("\\brs\\.?\\s*", "")
            .replaceAll("[^a-z0-9\\-\\.\\+\\*/\\s]", " ")
            .replaceAll("\\s{2,}", " ")
            .trim();
    }
}
