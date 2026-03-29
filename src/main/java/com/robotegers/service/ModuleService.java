package com.robotegers.service;

import com.robotegers.model.Concept;
import com.robotegers.model.Module;
import com.robotegers.model.Question;
import com.robotegers.model.Question.QuestionType;
import com.robotegers.model.Question.InputType;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 5 Subtopics × 5 Concepts × 2 Questions = 50 questions total (10 per subtopic).
 * Question types: MCQ, TRUE_FALSE, DROPDOWN, TEXT — mixed across concepts.
 * Each question has L1 hint (text), L2 (number line image path), L3 (guided pie example).
 */
@Service
public class ModuleService {

    private final Map<String, Module>  modules      = new LinkedHashMap<>();
    private final Map<String, Concept> conceptIndex = new LinkedHashMap<>();

    public ModuleService() { build(); }

    private void reg(Module m) {
        modules.put(m.getModuleId(), m);
        m.getConcepts().forEach(c -> conceptIndex.put(c.getConceptId(), c));
    }

    private void build() {

        // ══════════════════════════════════════════════════════════════════════
        // SUBTOPIC 1: Understanding Integers  (5 concepts × 2 questions = 10)
        // ══════════════════════════════════════════════════════════════════════
        reg(new Module("m1", "Understanding Integers", 0.28, List.of(

            new Concept("m1_c1", "What is an Integer?",
                "An integer is any WHOLE number — it can be positive, negative, or zero.\n\n" +
                "✅ Integers: … −4, −3, −2, −1, 0, 1, 2, 3, 4 …\n" +
                "❌ NOT integers: 2.5, ½, −3.7 (these have decimals or fractions)\n\n" +
                "Easy test: if you can write the number without a dot or fraction bar → it's an integer!",
                "🎮 Rohan plays a video game. He scores +3 for a coin, −2 for a trap, and 0 when nothing happens. " +
                "Every score is always a whole number — you can NEVER score 2.5 points in this game. " +
                "Those whole-number scores are integers!",
                List.of(
                    Question.mcq("m1_c1_q1", QuestionType.DEBUGGING,
                        "The robot looked at these numbers and said: \"I think 3.5, −2, and 0 are ALL integers because they are on a number line!\"\n\nWhich part of the robot's answer is WRONG?",
                        "I think 3.5, −2, and 0 are all integers!",
                        "3.5 is not an integer",
                        true,
                        List.of("3.5 is not an integer", "−2 is not an integer", "0 is not an integer", "All three are integers"),
                        "Integers must be WHOLE numbers — no decimal points allowed.",
                        "s1_integers_intro",
                        "Can you have 3.5 whole pies? No — you'd need to cut one. So 3.5 is NOT an integer. But −2 and 0 are whole amounts!"),

                    Question.trueFalse("m1_c1_q2", QuestionType.RULE_TEACHING,
                        "The robot says: \"−15 is an integer, but −15.0 is NOT an integer because it has a decimal point.\"\n\nIs the robot CORRECT?",
                        "−15 is an integer but −15.0 is not — it has a decimal!",
                        "False",
                        true,
                        "−15 and −15.0 represent the same value. The '.0' just shows there are no fractional parts — it IS still a whole number!",
                        "s1_integers_intro",
                        "Think: 6 whole pies is the same whether you write '6' or '6.0'. Writing '.0' just means no fraction — no cutting! −15.0 = −15 — same whole number, still an integer!")
                )),

            new Concept("m1_c2", "Positive and Negative Integers",
                "Positive integers (1, 2, 3…) mean MORE — gains, above ground, forward, warmer.\n" +
                "Negative integers (−1, −2, −3…) mean LESS — losses, below ground, backward, colder.\n" +
                "Zero is NEITHER positive nor negative — it's the middle ground, the dividing line.\n\n" +
                "Think of a thermometer: above 0°C = positive (warm), below 0°C = negative (cold), exactly 0°C = zero.",
                "🏢 Priya's apartment building: the basement parking floors are B1 (−1), B2 (−2), B3 (−3). " +
                "The ground floor is 0. The apartments above are floors 1, 2, 3, 4, 5. " +
                "When Priya presses −2 in the lift, she goes to second basement. The lift buttons ARE integers!",
                List.of(
                    Question.trueFalse("m1_c2_q1", QuestionType.RULE_TEACHING,
                        "The robot is confused! It says:\n\"Zero is a positive integer because it is not negative.\"\n\nIs the robot CORRECT?",
                        "Zero is a positive integer because it is not negative.",
                        "False",
                        true,
                        "Remember: there are THREE categories — positive, negative, and zero is its OWN special category.",
                        "s1_pos_neg",
                        "In Priya's building, floor 0 (ground floor) is neither a basement nor an upper floor. Zero stands alone — exactly in the middle!"),

                    Question.mcq("m1_c2_q2", QuestionType.PREDICTION,
                        "A diver is 12 metres below sea level. The surface is 0. Which integer correctly represents the diver's position?\n\nRobby says: \"+12 because the diver exists above zero in the water!\"",
                        "The diver is at +12 because they are above zero in the water!",
                        "−12",
                        true,
                        List.of("−12", "+12", "0", "+12 or −12, both work"),
                        "Below sea level means BELOW zero — that's always a negative integer!",
                        "s1_pos_neg",
                        "Sea level = 0. Think of pies: owning pies = positive (above zero), owing pies = negative (below zero). The diver 12m underwater is like owing 12 pies — that's −12, not +12!")
                )),

            new Concept("m1_c3", "Comparing Integers",
                "On a number line, any number to the RIGHT is always GREATER.\n\n" +
                "This surprises many students with negatives:\n" +
                "−1 > −5  (because −1 is to the RIGHT of −5)\n" +
                "−3 < 2   (because −3 is to the LEFT of 2)\n\n" +
                "Among negative numbers: the one CLOSER to zero is GREATER.\n" +
                "−1 is greater than −100 because −1 is closer to zero.",
                "🌡️ Weather report: Delhi is 38°C, Shimla is −3°C, Ooty is 15°C. " +
                "To order from coldest to warmest: −3, 15, 38. " +
                "The temperature further LEFT on the thermometer is always colder (smaller)!",
                List.of(
                    Question.dropdown("m1_c3_q1", QuestionType.PREDICTION,
                        "The robot wants to compare −7 and −2.\nIt says: \"−7 is GREATER because 7 is bigger than 2.\"\n\nWhat is the CORRECT comparison?",
                        "−7 is greater than −2 because 7 is bigger than 2.",
                        "−2 > −7",
                        true,
                        List.of("−7 > −2", "−2 > −7", "−7 = −2", "Cannot compare negatives"),
                        "On the number line, which is further to the RIGHT — −7 or −2?",
                        "s1_compare",
                        "Imagine owing 7 pies (−7) vs owing only 2 pies (−2). Less debt is BETTER — you're closer to having pies! So −2 > −7, just as owing 2 pies is better than owing 7."),

                    Question.mcq("m1_c3_q2", QuestionType.DEBUGGING,
                        "Robby must choose the greatest from: −100, −1, 0, −50\n\nRobby says: \"−100 is greatest because 100 is the biggest number here!\"",
                        "−100 is the greatest because 100 is the biggest!",
                        "0",
                        true,
                        List.of("0", "−1", "−50", "−100"),
                        "Bigger absolute value in a negative number means it's actually SMALLER. Which is closest to or above zero?",
                        "s1_compare",
                        "On the pie number line: owing 100 pies (−100) is the worst, owing 50 is less bad, owing 1 is almost OK, and owing 0 pies means you're at zero — FREE! Zero is the greatest of −100, −50, −1, and 0.")
                )),

            new Concept("m1_c4", "The Number Zero — Special Integer",
                "Zero is one of the most important integers:\n\n" +
                "🔹 Additive Identity: any number + 0 = that same number (45 + 0 = 45)\n" +
                "🔹 Zero is neither positive nor negative\n" +
                "🔹 Zero separates positives from negatives on the number line\n" +
                "🔹 Integers go infinitely in BOTH directions — there's no largest or smallest!\n\n" +
                "The set of integers: … −4, −3, −2, −1, 0, 1, 2, 3, 4 …",
                "🌡️ A thermometer showing 0°C is not showing 'no temperature' — it's showing the freezing point of water! " +
                "Zero is the anchor that separates positive and negative integers.",
                List.of(
                    Question.mcq("m1_c4_q1", QuestionType.DEBUGGING,
                        "The robot says: \"0 is the smallest integer because there's nothing below it!\"\n\nWhich answer CORRECTLY fixes the robot?",
                        "0 is the smallest integer because there's nothing below it!",
                        "Integers go infinitely negative — there is no smallest integer",
                        true,
                        List.of("The robot is correct, 0 is the smallest", "Integers go infinitely negative — there is no smallest integer", "−1 is the smallest integer", "−100 is the smallest integer"),
                        "Think: what comes after −1? −2. After −2? −3. Does it ever stop?",
                        "s1_integers_intro",
                        "Imagine you can always owe ONE MORE pie than you already owe: owe 2, then 3, then 4... it never ends! Integers go infinitely in both directions — there is no smallest integer."),

                    Question.trueFalse("m1_c4_q2", QuestionType.RULE_TEACHING,
                        "Robby says: \"−999 + 0 = 0, because adding zero always gives zero.\"\n\nIs Robby CORRECT?",
                        "−999 + 0 = 0, because zero wipes everything out!",
                        "False",
                        true,
                        "Zero is the ADDITIVE IDENTITY: any number + 0 = that SAME number, not zero!",
                        "s1_integers_intro",
                        "You owe 999 pies. Someone adds 0 pies — nothing changes, you still owe 999! Adding zero never changes the amount. −999 + 0 = −999, not 0. Zero is identity, not an eraser!")
                )),

            new Concept("m1_c5", "Integers in Real Life",
                "Integers describe real situations everywhere:\n\n" +
                "📈 Stock market: +₹200 (gain) or −₹150 (loss)\n" +
                "🌡️ Temperature: +35°C (hot day) or −10°C (freezing)\n" +
                "🏦 Bank: +₹500 (deposit) or −₹300 (withdrawal)\n" +
                "🏔️ Altitude: +8849m (Everest summit) or −400m (Dead Sea)\n" +
                "🎮 Games: +5 (bonus) or −3 (penalty)\n\n" +
                "The key: positive = gaining/above/forward. Negative = losing/below/backward.",
                "💳 Anika's pocket money: she starts with ₹0. She earns ₹200 from a chore (+200). " +
                "She spends ₹350 on a book (−350). Her balance = 200 − 350 = −₹150. " +
                "Negative means she OWEs ₹150 — she's in debt!",
                List.of(
                    Question.mcq("m1_c5_q1", QuestionType.PREDICTION,
                        "A submarine is 80 metres below sea level. It rises 30 metres.\n\nRobby says: \"The new position is −80 + 30. I don't know if this is positive or negative!\"\n\nWhat IS the new position?",
                        "New position = −80 + 30. I'm not sure of the answer!",
                        "−50 (still below sea level)",
                        true,
                        List.of("−50 (still below sea level)", "+50 (above sea level)", "−110 (went deeper)", "0 (reached surface)"),
                        "−80 + 30: the negative is larger, so the result stays negative. 80 − 30 = 50, and the sign stays negative.",
                        "s1_integers_intro",
                        "Imagine owing 80 pies (−80). You get 30 pies back. You still owe 50! |−80| > |30|, so the negative wins: 80 − 30 = 50 still owed → −50. Still 'in debt', still below sea level."),

                    Question.dropdown("m1_c5_q2", QuestionType.RULE_TEACHING,
                        "A bank account shows: Deposit +₹500, Withdrawal −₹500.\n\nRobby says: \"The balance is −500 because the withdrawal was last!\"\n\nWhat is the ACTUAL balance?",
                        "Balance = −500 because withdrawal was done last!",
                        "₹0 — the amounts cancel exactly",
                        true,
                        List.of("₹0 — the amounts cancel exactly", "−₹500", "+₹500", "+₹1000"),
                        "What is +500 + (−500)? Equal and opposite amounts cancel to zero!",
                        "s1_integers_intro",
                        "Bake 500 pies (+500) and give away exactly 500 pies (−500) — you end up with 0 pies! Equal positives and negatives always cancel to zero. Balance = ₹0, not −₹500.")
                ))
        )));

        // ══════════════════════════════════════════════════════════════════════
        // SUBTOPIC 2: Integers on the Number Line  (5 concepts × 2 questions = 10)
        // ══════════════════════════════════════════════════════════════════════
        reg(new Module("m2", "Integers on the Number Line", 0.35, List.of(

            new Concept("m2_c1", "Building the Number Line",
                "A number line is a straight line where every integer has a fixed home:\n\n" +
                "← more negative | −4 −3 −2 −1 | 0 | 1 2 3 4 | more positive →\n\n" +
                "Rules:\n" +
                "• Zero sits in the MIDDLE\n" +
                "• Moving RIGHT → numbers get bigger\n" +
                "• Moving LEFT → numbers get smaller\n" +
                "• Every step is the SAME size (equal spacing)",
                "🏘️ Imagine a long road in your colony. Your house is at position 0. " +
                "Your friend's house is 3 plots to the RIGHT = position +3. " +
                "The park is 4 plots to the LEFT = position −4. This road IS your number line!",
                List.of(
                    Question.mcq("m2_c1_q1", QuestionType.DEBUGGING,
                        "The robot drew a number line and placed −3 to the RIGHT of 0.\n\"Negative 3 goes here!\" it said, pointing right.\n\nWhat mistake did the robot make?",
                        "I placed −3 to the right of zero — negatives go right!",
                        "Negative numbers go to the LEFT of zero",
                        true,
                        List.of("Negative numbers go to the LEFT of zero", "−3 should be further right", "The robot is correct", "Zero should be at the far left"),
                        "Which side of zero holds the negative numbers?",
                        "s2_numberline",
                        "In the colony road: going RIGHT = positive. Going LEFT = negative. −3 is 3 plots to the LEFT of your house at 0!"),

                    Question.trueFalse("m2_c1_q2", QuestionType.RULE_TEACHING,
                        "Robby says: \"On a number line, the gap between −3 and −4 is LARGER than the gap between 3 and 4, because negative numbers are more spread out.\"\n\nIs Robby CORRECT?",
                        "Gaps between negatives are larger because they are more spread out!",
                        "False",
                        true,
                        "The gap between ANY two consecutive integers is ALWAYS equal — number lines have uniform spacing everywhere!",
                        "s2_numberline",
                        "Whether pies are positive or negative, each step between neighbouring integers is the same size — exactly 1 unit. The gap from −3 to −4 is the same as from 3 to 4. Number lines have uniform spacing!")
                )),

            new Concept("m2_c2", "Ordering Integers on the Number Line",
                "The number line shows us the ORDER of all integers at a glance.\n\n" +
                "Key rules for ordering:\n" +
                "• All positive integers are greater than zero\n" +
                "• All negative integers are less than zero\n" +
                "• Any positive integer is greater than any negative integer\n" +
                "• Among negatives: closer to zero = GREATER (−1 > −5)\n\n" +
                "To order a list: place them on the number line, then read LEFT to RIGHT = smallest to largest.",
                "🎭 A drama competition scores: Team A got +8, Team B got −3, Team C got −1, Team D got 0. " +
                "Ordering from worst to best: −3, −1, 0, +8. Team B is last (furthest left), Team A wins (furthest right)!",
                List.of(
                    Question.dropdown("m2_c2_q1", QuestionType.PREDICTION,
                        "The robot tries to order these numbers from smallest to largest:\n−5, 2, −1, 0, −9, 4\n\nThe robot answers: \"−1, −5, −9, 0, 2, 4\"\n\nWhat is the CORRECT ordering?",
                        "My ordering is: −1, −5, −9, 0, 2, 4",
                        "−9, −5, −1, 0, 2, 4",
                        true,
                        List.of("−9, −5, −1, 0, 2, 4", "−1, −5, −9, 0, 2, 4", "4, 2, 0, −1, −5, −9", "−5, −9, −1, 0, 2, 4"),
                        "On the number line: left = smallest. Which negative is furthest left?",
                        "s2_order",
                        "Think of pies: owing 9 pies (−9) is worst, then owing 5 (−5), then owing 1 (−1), then having 0, then having 2, then having 4. Smallest to largest: −9, −5, −1, 0, 2, 4."),

                    Question.mcq("m2_c2_q2", QuestionType.DEBUGGING,
                        "Five students' test scores: Priya = −5, Arjun = −12, Meena = 0, Ravi = −3, Sana = +7.\n\nRobby ranks them best to worst: \"7, 0, −3, −5, −12\"\n\nIs Robby's ranking correct?",
                        "Best to worst: 7, 0, −3, −5, −12",
                        "Yes, Robby's ranking is correct",
                        false,
                        List.of("Yes, Robby's ranking is correct", "No — correct order is 7, 0, −3, −12, −5", "No — correct order is −12, −5, −3, 0, 7", "No — correct order is 0, 7, −3, −5, −12"),
                        "Best to worst = greatest to smallest = RIGHT to LEFT on the number line. Check Robby's sequence carefully!",
                        "s2_order",
                        "7 pies owned is best (rightmost), then 0 pies, then owe 3 (−3), then owe 5 (−5), then owe 12 (−12) is worst. Best to worst: 7, 0, −3, −5, −12. Robby's ranking IS correct! Among negatives, closer to zero = greater.")
                )),

            new Concept("m2_c3", "Absolute Value — Distance from Zero",
                "The absolute value of an integer is its DISTANCE from zero on the number line.\n\n" +
                "Written as: |−5| = 5  and  |5| = 5  and  |0| = 0\n\n" +
                "Important: absolute value is ALWAYS positive (or zero) — distance can never be negative!\n" +
                "Whether you walk 5 steps left OR 5 steps right from home, you've still walked 5 steps.",
                "🏃 Two students start at the school gate (position 0). Meera walks 6 steps LEFT to the water tap (−6). " +
                "Ravi walks 6 steps RIGHT to the bicycle stand (+6). Both walked the SAME distance — 6 steps! |−6| = |+6| = 6.",
                List.of(
                    Question.mcq("m2_c3_q1", QuestionType.RULE_TEACHING,
                        "The robot calculated |−8| and said:\n\"The absolute value of −8 is −8, because the number is negative!\"\n\nWhat is the CORRECT answer?",
                        "The absolute value of −8 is −8, because the number is negative!",
                        "|−8| = 8",
                        true,
                        List.of("|−8| = 8", "|−8| = −8", "|−8| = 0", "|−8| = 64"),
                        "Absolute value = distance from zero. Distance is never negative!",
                        "s2_absolute",
                        "Meera walked 8 steps away from home to collect 8 pies. The DISTANCE she walked is 8 — not negative 8! Absolute value = distance from zero, always positive. |−8| = 8."),

                    Question.dropdown("m2_c3_q2", QuestionType.PREDICTION,
                        "Robby must find which has the GREATEST absolute value:\n|−15|, |+9|, |−9|, |+15|\n\nRobby says: \"+15 has the greatest absolute value because it's positive!\"",
                        "|+15| is the greatest because +15 is a positive number!",
                        "|−15| and |+15| are both greatest and equal to 15",
                        true,
                        List.of("|−15| and |+15| are both greatest and equal to 15", "|+15| only, because positive is greater", "|−15| only, because negatives have larger absolute values", "|+9| and |−9|, value 9"),
                        "Absolute value ignores the sign — it ONLY measures distance from zero!",
                        "s2_absolute",
                        "Owning 15 pies (+15) or owing 15 pies (−15) — either way you're exactly 15 pies away from zero! |−15| = 15 and |+15| = 15. Both are equal. The sign vanishes when measuring distance from zero.")
                )),

            new Concept("m2_c4", "Using the Number Line for Addition Preview",
                "The number line isn't just for locating numbers — it's a CALCULATION TOOL!\n\n" +
                "Adding a positive integer → move RIGHT\n" +
                "Adding a negative integer → move LEFT\n\n" +
                "This makes addition VISUAL before you memorize rules:\n" +
                "−3 + 5: start at −3, move 5 steps RIGHT → land on +2",
                "🎲 Rohan's board game: starts at −3. Rolls a 5 (move right). Counts: −3 → −2 → −1 → 0 → 1 → 2. Lands on +2! " +
                "Then draws a −4 penalty card: +2 → +1 → 0 → −1 → −2. The board game IS the number line!",
                List.of(
                    Question.mcq("m2_c4_q1", QuestionType.DEBUGGING,
                        "The robot starts at −2 on a number line and moves 3 steps to the RIGHT.\nIt says: \"I land on −5!\"\n\nWhere does the robot ACTUALLY land?",
                        "I started at −2 and moved 3 steps right, landing on −5!",
                        "+1",
                        true,
                        List.of("+1", "−5", "+5", "−1"),
                        "Moving RIGHT means ADDING. What is −2 + 3?",
                        "s2_numberline",
                        "Owing 2 pies (−2), then receiving 3 pies (move right 3): cancel 2 debt pies and keep 1 extra. Land on +1, not −5! Moving right always means adding pies, not taking more away."),

                    Question.mcq("m2_c4_q2", QuestionType.PREDICTION,
                        "Starting at +4 on a number line, Robby moves 6 steps to the LEFT.\n\nRobby predicts: \"I'll land on +10 because I'm moving 4 and 6 together!\"\n\nWhere will Robby ACTUALLY land?",
                        "I'll land on +10 because 4 + 6 = 10!",
                        "−2",
                        true,
                        List.of("−2", "+10", "+2", "−10"),
                        "Moving LEFT = SUBTRACTING. What is 4 − 6?",
                        "s2_numberline",
                        "Own 4 pies (+4), then give away 6 pies (move left 6): you run out at 0 and still owe 2! 4 − 6 = 4 + (−6) = −2. Moving left means losing pies, not adding them. Land on −2, not +10.")
                )),

            new Concept("m2_c5", "Number Line and Inequalities",
                "The number line makes inequalities (>, <, ≥, ≤) easy to visualise:\n\n" +
                "• The number FURTHER RIGHT is always GREATER (use >)\n" +
                "• The number FURTHER LEFT is always LESS (use <)\n\n" +
                "Between any two integers on a number line:\n" +
                "All integers to the right of −3 and left of 2: that's −2, −1, 0, 1\n\n" +
                "This is called finding integers in a RANGE.",
                "📊 In a class test marked out of 10, any score below 0 means a penalty was applied. " +
                "Scores between −5 and +5 (not including ends) are: −4, −3, −2, −1, 0, 1, 2, 3, 4. " +
                "We read this range off the number line!",
                List.of(
                    Question.mcq("m2_c5_q1", QuestionType.PREDICTION,
                        "Robby must list ALL integers strictly between −3 and +2 (not including −3 or +2).\n\nRobby says: \"The integers are: −3, −2, −1, 0, 1, 2\"",
                        "Integers strictly between −3 and +2: −3, −2, −1, 0, 1, 2",
                        "−2, −1, 0, 1",
                        true,
                        List.of("−2, −1, 0, 1", "−3, −2, −1, 0, 1, 2", "−3, −2, −1, 0, 1", "−2, −1, 0, 1, 2"),
                        "STRICTLY between means NOT including the endpoints −3 and +2!",
                        "s2_order",
                        "Strictly between means NOT including the endpoints. Between −3 and +2 (exclusive): the pies you'd own are −2, −1, 0, +1. −3 itself and +2 itself are NOT included — the robot forgot to exclude the endpoints!"),

                    Question.trueFalse("m2_c5_q2", QuestionType.RULE_TEACHING,
                        "Robby says: \"Any negative integer is always less than any positive integer.\"\n\nIs Robby CORRECT?",
                        "Any negative integer < any positive integer — always!",
                        "True",
                        true,
                        "On the number line, ALL negatives are to the LEFT of zero, and ALL positives are to the RIGHT. Left always means less!",
                        "s1_compare",
                        "Every negative-pie debt (−3, −50, −1000...) is to the LEFT of zero. Every owned pie (+1, +5, +100...) is to the RIGHT of zero. Left is always less than right — so every negative < every positive. Robby is correct!")
                ))
        )));

        // ══════════════════════════════════════════════════════════════════════
        // SUBTOPIC 3: Addition of Integers  (5 concepts × 2 questions = 10)
        // ══════════════════════════════════════════════════════════════════════
        reg(new Module("m3", "Addition of Integers", 0.50, List.of(

            new Concept("m3_c1", "Adding Two Positive Integers",
                "When you add two positive integers, the answer is always a LARGER positive integer.\n" +
                "This works exactly like the addition you've always known!\n\n" +
                "4 + 7 = 11\n" +
                "On the number line: start at 4, move 7 steps RIGHT → land on 11\n\n" +
                "Think of it as collecting things: more + more = even more!",
                "🍕 Anaya earns ₹50 helping at home on Monday and ₹30 on Tuesday. " +
                "Her total savings = 50 + 30 = ₹80. Both amounts are positive earnings, so the total is simply bigger!",
                List.of(
                    Question.mcq("m3_c1_q1", QuestionType.PREDICTION,
                        "The teacher asks the robot: \"What is 56 + 34?\"\n\nPredict: what SHOULD the robot answer?",
                        "Hmm, I think 56 + 34 = 80?",
                        "90",
                        true,
                        List.of("80", "90", "100", "910"),
                        "Add the tens: 50+30=80. Add the ones: 6+4=10. Put them together: 80+10=90!",
                        "s3_add_pos_pos",
                        "56 pies + 34 pies. Count 56 pies, then add 34 more. 56 + 34 = 90. Both positive so total MUST be bigger than either one!"),

                    Question.trueFalse("m3_c1_q2", QuestionType.RULE_TEACHING,
                        "Robby claims: \"The sum of two positive integers can sometimes be smaller than one of the original numbers.\"\n\nIs Robby CORRECT?",
                        "Two positives added might sometimes give a smaller number!",
                        "False",
                        true,
                        "Adding any positive integer always INCREASES the total. You can never make a positive number smaller by adding another positive!",
                        "s3_add_pos_pos",
                        "If you have 5 freshly baked pies and someone gives you 3 more, you ALWAYS end up with 8 — never fewer than 5. Adding pies always increases the total. Two positives added can NEVER give a smaller result. Robby is wrong!")
                )),

            new Concept("m3_c2", "Adding Two Negative Integers",
                "When you add two negative integers, the answer is always a MORE NEGATIVE number.\n\n" +
                "Rule: Add the absolute values and keep the negative sign.\n" +
                "−4 + (−3) = −7\n\n" +
                "Think of it as: two LOSSES added together make an even BIGGER loss!\n" +
                "On the number line: start at −4, move 3 more steps LEFT → land on −7",
                "🎮 Arjun's quiz team: they lose 5 points in Round 1 (−5) and lose 3 more points in Round 2 (−3). " +
                "Total = −5 + (−3) = −8 points. Two losses = bigger loss!",
                List.of(
                    Question.mcq("m3_c2_q1", QuestionType.DEBUGGING,
                        "The robot calculated −5 + (−3) and said:\n\"The answer is −2, because I subtracted 3 from 5!\"\n\nWhat is the CORRECT answer?",
                        "−5 + (−3) = −2 because I subtracted 3 from 5!",
                        "−8",
                        true,
                        List.of("−2", "−8", "+8", "+2"),
                        "Both numbers are negative — two losses make a BIGGER loss, not a smaller one!",
                        "s3_add_neg_neg",
                        "5 pies you OWE + 3 more pies you OWE = 8 pies you OWE. More debt, not less! −5 + (−3) = −8."),

                    Question.dropdown("m3_c2_q2", QuestionType.PREDICTION,
                        "Robby must calculate: (−11) + (−9)\n\nRobby says: \"The answer is −2 because 11 − 9 = 2 and both numbers are negative!\"",
                        "(−11) + (−9) = −2 because 11 − 9 = 2!",
                        "−20",
                        true,
                        List.of("−20", "−2", "+20", "+2"),
                        "Two negatives being ADDED means their absolute values ADD together — not subtract!",
                        "s3_add_neg_neg",
                        "You owe 11 pies AND owe 9 more pies — that's 20 pies of debt total! Both are negative, so their absolute values ADD together: 11 + 9 = 20, keep the negative sign → −20. The robot incorrectly SUBTRACTED instead of adding debts!")
                )),

            new Concept("m3_c3", "Adding Integers with Different Signs",
                "This is the trickiest case! When one number is positive and one is negative:\n\n" +
                "Step 1: Find the absolute values of both numbers\n" +
                "Step 2: Subtract the SMALLER absolute value from the LARGER one\n" +
                "Step 3: Keep the sign of whichever number had the LARGER absolute value\n\n" +
                "Example: −5 + 8\n" +
                "→ |−5|=5, |8|=8 → 8 is larger (positive) → 8−5=3 → Answer = +3\n\n" +
                "Example: −9 + 4\n" +
                "→ |−9|=9, |4|=4 → 9 is larger (negative) → 9−4=5 → Answer = −5",
                "🏏 Cricket: Kavya scores 8 runs (+8) but loses 5 penalty runs (−5). Net = 8 + (−5) = +3. " +
                "The gain of 8 was BIGGER than the loss of 5, so she ends up positive!",
                List.of(
                    Question.mcq("m3_c3_q1", QuestionType.PREDICTION,
                        "The teacher shows the robot: −5 + 8\n\nThe robot guesses: \"The answer must be −13 because I added 5 and 8!\"\n\nWhat is the CORRECT answer?",
                        "I think −5 + 8 = −13 because I added 5 and 8 together and kept the negative!",
                        "+3",
                        true,
                        List.of("+13", "−13", "+3", "−3"),
                        "Different signs: SUBTRACT the smaller absolute value from the larger, keep the sign of the larger.",
                        "s3_q1_neg5_pos8",
                        "−4 + 6: 6 positive pies, cancel 4 negative → 2 remain! Same idea for −5+8: 8 positive, cancel 5 negative → 3 positive → +3!"),

                    Question.mcq("m3_c3_q2", QuestionType.DEBUGGING,
                        "Robby calculates −12 + 7 and says: \"Since the negative number is bigger, I subtract: 12 − 7 = 5, and the answer is +5!\"\n\nWhat is the CORRECT answer?",
                        "−12 + 7: negative is bigger, so 12 − 7 = 5, answer = +5!",
                        "−5",
                        true,
                        List.of("−5", "+5", "−19", "+19"),
                        "Robby got the calculation right (12−7=5) but the SIGN wrong! Which number had the larger absolute value?",
                        "s3_add_diff_neg5_pos8",
                        "You owe 12 pies (|−12|=12) but receive 7 pies (|7|=7). The bigger group is the debt (12 > 7), so the negative sign WINS. Subtract: 12 − 7 = 5 pies still owed → −5, not +5. Robby got the subtraction right but kept the wrong sign!")
                )),

            new Concept("m3_c4", "Properties of Integer Addition",
                "Integer addition follows three beautiful properties:\n\n" +
                "1. COMMUTATIVE: a + b = b + a (order doesn't change the answer)\n" +
                "   −3 + 7 = 7 + (−3) = 4 ✓\n\n" +
                "2. ASSOCIATIVE: (a + b) + c = a + (b + c) (grouping doesn't matter)\n" +
                "   (−2 + 5) + (−1) = −2 + (5 + (−1)) = 2 ✓\n\n" +
                "3. ADDITIVE IDENTITY: a + 0 = a (adding zero changes nothing!)\n" +
                "   −45 + 0 = −45 ✓",
                "🔀 Shuffling a deck: whether you add 3 red cards first then 5 blue, or 5 blue first then 3 red, " +
                "you still have 8 new cards. ORDER doesn't change the total — that's the commutative property!",
                List.of(
                    Question.trueFalse("m3_c4_q1", QuestionType.DEBUGGING,
                        "The robot says: \"−7 + 4 gives a DIFFERENT answer from 4 + (−7) because the order is different!\"\n\nIs the robot CORRECT?",
                        "−7 + 4 is different from 4 + (−7) because the order changed!",
                        "False",
                        true,
                        "Does the ORDER of addition change the result for integers?",
                        "s3_add_diff_neg5_pos8",
                        "Whether you eat 4 pies then owe 7, or owe 7 then eat 4 — you still end up 3 pies in debt! −7+4 = 4+(−7) = −3. Order doesn't matter!"),

                    Question.mcq("m3_c4_q2", QuestionType.PREDICTION,
                        "Robby sees: (−6 + 10) + (−4) and tries to simplify by regrouping: −6 + (10 + (−4))\n\nRobby asks: \"Will regrouping change my answer?\"\n\nWhat should Robby expect?",
                        "Will (−6 + 10) + (−4) give a different answer from −6 + (10 + (−4))?",
                        "No — both equal 0 due to the associative property",
                        true,
                        List.of("No — both equal 0 due to the associative property", "Yes — grouping changes integer addition", "No — both equal −12", "Yes — the second grouping gives +12"),
                        "The ASSOCIATIVE property says grouping doesn't matter in addition. Calculate both sides and verify!",
                        "s3_add_diff_neg5_pos8",
                        "Left side: (−6+10)+(−4) = 4+(−4) = 0. Right side: −6+(10+(−4)) = −6+6 = 0. Grouping pies differently — whether you cancel the debt-pies first or last — always gives the same result. Associative property always holds!")
                )),

            new Concept("m3_c5", "Multi-Step Integer Addition",
                "When adding several integers, use these strategies:\n\n" +
                "Strategy 1: GROUP positives together, GROUP negatives together\n" +
                "Then subtract the smaller group from the larger group.\n\n" +
                "Strategy 2: Work LEFT to RIGHT, adding one number at a time.\n\n" +
                "Example: −3 + 7 + (−2) + 4\n" +
                "Group: (7 + 4) + (−3 + −2) = 11 + (−5) = +6\n" +
                "Or step by step: −3 + 7 = 4 → 4 + (−2) = 2 → 2 + 4 = 6",
                "💰 Arun's week: Monday +₹50, Tuesday −₹30, Wednesday +₹20, Thursday −₹80, Friday +₹100. " +
                "Positives: 50+20+100 = ₹170. Negatives: 30+80 = ₹110. Net = 170 − 110 = +₹60 gain!",
                List.of(
                    Question.mcq("m3_c5_q1", QuestionType.PREDICTION,
                        "Robby must add: −4 + 9 + (−6) + 3 + (−2)\n\nRobby groups positives and negatives: \"Positives = 9+3=12. Negatives = 4+6+2=12. So the answer is 0!\"\n\nIs Robby correct?",
                        "Positives = 12, Negatives = 12, so answer = 0!",
                        "Yes, Robby is correct — answer is 0",
                        false,
                        List.of("Yes, Robby is correct — answer is 0", "No — answer is +12", "No — answer is −12", "No — answer is +24"),
                        "Check the grouping: positives are 9 and 3. Negatives are 4, 6, and 2. Then subtract: 12 − 12 = ?",
                        "s3_add_pos_pos",
                        "Collect all the pies you HAVE: 9 + 3 = 12 positive pies. Count all pies you OWE: 4 + 6 + 2 = 12 negative pies. 12 pies owned vs 12 pies owed — they cancel perfectly! 12 − 12 = 0. Robby is correct!"),

                    Question.mcq("m3_c5_q2", QuestionType.DEBUGGING,
                        "Robby calculates −8 + 3 + (−1) + 6 step by step:\n\"−8 + 3 = −5 → −5 + (−1) = −6 → −6 + 6 = 0\"\n\nIs the step-by-step correct, and what is the final answer?",
                        "−8+3=−5, −5+(−1)=−6, −6+6=0. Final answer: 0",
                        "Yes, every step is correct — answer is 0",
                        false,
                        List.of("Yes, every step is correct — answer is 0", "No — first step is wrong, answer is −11", "No — last step is wrong, answer is −12", "No — second step is wrong, answer is −4"),
                        "Check each step: −8+3=? Then add −1. Then add 6.",
                        "s3_add_neg_neg",
                        "Step 1: Owe 8 pies, get 3 back → owe 5 (−8+3=−5 ✓). Step 2: Owe 1 more pie → owe 6 (−5+(−1)=−6 ✓). Step 3: Get 6 pies, cancel all debt → 0 (−6+6=0 ✓). Every step checks out — Robby's answer of 0 is right!")
                ))
        )));

        // ══════════════════════════════════════════════════════════════════════
        // SUBTOPIC 4: Subtraction of Integers  (5 concepts × 2 questions = 10)
        // ══════════════════════════════════════════════════════════════════════
        reg(new Module("m4", "Subtraction of Integers", 0.55, List.of(

            new Concept("m4_c1", "Subtraction = Adding the Opposite",
                "The golden rule of integer subtraction:\n\n" +
                "a − b = a + (−b)\n\n" +
                "FLIP the sign of the second number and ADD!\n\n" +
                "Examples:\n" +
                "5 − 3 = 5 + (−3) = 2\n" +
                "5 − (−3) = 5 + 3 = 8  ← subtracting a negative = ADDING!\n" +
                "−4 − 2 = −4 + (−2) = −6\n\n" +
                "Once you rewrite as addition, use the addition rules you already know!",
                "💰 Rohan has ₹200. His uncle cancels an ₹80 debt. " +
                "Cancelling a debt (subtracting a negative) = ADDING money! 200 − (−80) = 200 + 80 = ₹280.",
                List.of(
                    Question.mcq("m4_c1_q1", QuestionType.DEBUGGING,
                        "The robot calculates 5 − (−3) and says:\n\"The answer is 2, because 5 minus 3 equals 2!\"\n\nWhat is the CORRECT answer?",
                        "5 − (−3) = 2, because 5 minus 3 equals 2!",
                        "8",
                        true,
                        List.of("2", "8", "−2", "−8"),
                        "Subtracting a NEGATIVE is the same as ADDING a positive. Flip the sign!",
                        "s4_sub_pos_neg",
                        "Riya has 5 baked pies. Someone cancels her 3-pie debt (removes −3). Cancelling a debt is like receiving pies! 5 − (−3) = 5 + 3 = 8 pies. The robot forgot: removing a negative means adding a positive!"),

                    Question.dropdown("m4_c1_q2", QuestionType.PREDICTION,
                        "Robby must rewrite −3 − (−7) as an addition problem, then solve it.\n\nRobby writes: \"−3 − (−7) = −3 − 7 = −10\"",
                        "−3 − (−7) = −3 − 7 = −10",
                        "+4",
                        true,
                        List.of("+4", "−10", "−4", "+10"),
                        "Subtracting (−7) means ADDING +7. Rewrite: −3 + 7 = ?",
                        "s4_sub_pos_neg",
                        "Riya owes 3 pies (−3). Her friend cancels 7 of her debt pies — removing a 7-pie debt = gaining 7 pies! −3 + 7: she receives 7 pies, cancels her 3-pie debt, and has 4 pies left → +4. Robby forgot to flip −(−7) to +7!")
                )),

            new Concept("m4_c2", "Subtracting and Getting a Negative Result",
                "When you subtract a LARGER number from a SMALLER one, the answer is negative.\n\n" +
                "3 − 8 = 3 + (−8) = −5\n\n" +
                "Real-life check: if you have only ₹3 and spend ₹8, you're ₹5 in DEBT!\n\n" +
                "On the number line: start at 3, move 8 steps LEFT → land on −5\n\n" +
                "The negative result means you've 'gone past zero' into debt territory.",
                "💧 Meena's water tank has 200 litres. She uses 350 litres. " +
                "Balance = 200 − 350 = −150 litres. The tank is 150 litres SHORT — water debt!",
                List.of(
                    Question.mcq("m4_c2_q1", QuestionType.PREDICTION,
                        "The robot is asked: what is −4 − 2?\n\nThe robot says: \"The answer is −2 because I subtracted the minus signs!\"\n\nWhat is the CORRECT answer?",
                        "−4 − 2 = −2 because the minus signs cancel!",
                        "−6",
                        true,
                        List.of("−6", "−2", "+6", "+2"),
                        "Rewrite as addition: −4 − 2 = −4 + (−2). Both negative — add them!",
                        "s4_q1_neg4_neg2",
                        "Meena owes 2 pies (−2). Now she borrows 3 more pies — that's additional debt! Subtract 2 = add 2 negative pies. 2 + 3 = 5 pies of debt total → −5. Same rule: −4 − 2 = −4 + (−2) = −6. Debts add up, not cancel!"),

                    Question.trueFalse("m4_c2_q2", QuestionType.RULE_TEACHING,
                        "Robby says: \"−15 − 3 and −3 − 15 give the same answer because subtraction is commutative for integers.\"\n\nIs Robby CORRECT?",
                        "−15 − 3 and −3 − 15 give the same answer because subtraction is commutative!",
                        "False",
                        true,
                        "Unlike addition, subtraction is NOT commutative — the ORDER matters! Calculate both to check.",
                        "s4_q1_neg4_neg2",
                        "Try with pies: you OWE 15 pies, then borrow 3 more → owe 18 (−15−3=−18). OR owe 3 pies, then borrow 15 more → also owe 18 (−3−15=−18). Same here, but try 5 pies OWNED minus 3 owed ≠ 3 owed minus 5 owned. Subtraction is NOT always commutative!")
                )),

            new Concept("m4_c3", "Real-World Subtraction Problems",
                "Subtraction of integers appears everywhere in real life:\n\n" +
                "🌡️ Temperature changes: if it was −5°C and dropped 8° more: −5 − 8 = −13°C\n" +
                "💰 Bank balance: ₹500 in account, ₹620 bill → 500 − 620 = −120 (overdrawn!)\n" +
                "🏔️ Altitude: a submarine at −40m dives 15m deeper → −40 − 15 = −55m\n\n" +
                "Key: identify the starting value and what is being removed, then apply a − b = a + (−b)",
                "❄️ In Shimla, the temperature at night is −5°C. By early morning it drops another 8°C. " +
                "New temperature = −5 − 8 = −5 + (−8) = −13°C. Both numbers pull colder!",
                List.of(
                    Question.mcq("m4_c3_q1", QuestionType.DEBUGGING,
                        "The robot hears: \"Temperature was −3°C and fell by 5 degrees.\"\n\nThe robot says: \"New temperature = −3 + 5 = +2°C because falling means adding!\"\n\nWhat did the robot get WRONG?",
                        "Temperature was −3°C and fell 5°, so −3 + 5 = +2°C!",
                        "Falling means subtracting — the answer is −8°C",
                        true,
                        List.of("Falling means subtracting — the answer is −8°C", "The robot is correct, the answer is +2°C", "The answer is −2°C", "The answer is +8°C"),
                        "'Fell by 5' means subtract 5 — temperature drops more negative!",
                        "s4_sub_neg_result",
                        "Think of it as pie debt: start owing 3 pies (−3°C). Temperature FELL 5° = add 5 more pie debts. 3 + 5 = 8 pies owed → −8°C. Falling always means MORE negative. The robot added when it should have subtracted further into negative!"),

                    Question.mcq("m4_c3_q2", QuestionType.PREDICTION,
                        "A deep-sea explorer is at −120m. She ascends (rises) 45m.\n\nRobby calculates: \"−120 − 45 = −165m because going up means the depth increases!\"",
                        "New depth = −120 − 45 = −165m because ascending increases depth!",
                        "−75m (still below surface)",
                        true,
                        List.of("−75m (still below surface)", "−165m", "+75m", "0m (reached surface)"),
                        "Ascending (going UP) means ADDING a positive number, not subtracting! −120 + 45 = ?",
                        "s4_sub_neg_result",
                        "Owing 120 pies (−120m deep). Ascending 45m = getting 45 pies back! −120 + 45: the debt (120) is larger, so still in debt: 120 − 45 = 75 pies still owed → −75m. Rising ADDS a positive, it doesn't subtract more. Robby subtracted instead of adding!")
                )),

            new Concept("m4_c4", "Subtraction on the Number Line",
                "Subtraction on the number line follows a simple visual rule:\n\n" +
                "a − b: start at 'a', then move 'b' units to the LEFT\n\n" +
                "BUT: a − (−b): start at 'a', then move 'b' units to the RIGHT!\n" +
                "(Because subtracting a negative = adding a positive = moving right)\n\n" +
                "Examples:\n" +
                "7 − 4: start at 7, move LEFT 4 → land on 3\n" +
                "2 − (−3): start at 2, move RIGHT 3 → land on 5",
                "🎯 Game token at position 6. Subtract 9 points: 6 − 9. Start at 6, move LEFT 9 steps: " +
                "6→5→4→3→2→1→0→−1→−2→−3. Land on −3!",
                List.of(
                    Question.mcq("m4_c4_q1", QuestionType.PREDICTION,
                        "On a number line, Robby starts at −1 and performs the operation −1 − (−5).\n\nRobby says: \"I move LEFT 5 steps and land on −6!\"",
                        "Start at −1, move LEFT 5 for −1 − (−5), landing on −6!",
                        "+4",
                        true,
                        List.of("+4", "−6", "+6", "−4"),
                        "Subtracting a NEGATIVE means moving RIGHT, not left! −1 − (−5) = −1 + 5 = ?",
                        "s4_sub_pos_neg",
                        "Owing 1 pie (−1). Cancel a 5-pie debt: −1 − (−5) = −1 + 5. Cancelling debt is GAINING pies → move RIGHT on the number line. −1 + 5 = +4. Robby moved left (more debt) when he should have moved right (cancelling debt)!"),

                    Question.dropdown("m4_c4_q2", QuestionType.DEBUGGING,
                        "Robby performs 4 − 10 on a number line, starting at 4 and moving LEFT 10 steps.\n\nRobby says: \"I land on +14 because 4 and 10 are both positive, so they add!\"",
                        "4 − 10: both positive so answer is +14!",
                        "−6",
                        true,
                        List.of("−6", "+14", "+6", "−14"),
                        "Subtraction = moving LEFT. Start at 4, move LEFT 10 steps. Count: 4→3→2→1→0→−1→−2→−3→−4→−5→−6",
                        "s4_sub_neg_result",
                        "Own 4 pies, give away 10 pies — you run out at 0 and still owe 6 more! 4 − 10 = 4 + (−10): the debt (10) is larger than pies owned (4), so you end up owing 6 → −6. You pass through zero going left, not right to +14!")
                )),

            new Concept("m4_c5", "Mixed Addition and Subtraction",
                "When expressions mix + and − with integers, convert EVERYTHING to addition first:\n\n" +
                "Step 1: Replace every '−' with '+(−)'\n" +
                "Step 2: Group all positives and all negatives\n" +
                "Step 3: Add each group, then combine\n\n" +
                "Example: 6 − (−3) + (−5) − 2\n" +
                "= 6 + 3 + (−5) + (−2)\n" +
                "= (6 + 3) + (−5 + −2)\n" +
                "= 9 + (−7) = +2",
                "📊 Kabaddi game: +6 (raid) − (−3) (tackle reversal) + (−5) (penalty) − 2 (foul). " +
                "Convert: 6 + 3 + (−5) + (−2) = 9 − 7 = +2 points net.",
                List.of(
                    Question.mcq("m4_c5_q1", QuestionType.PREDICTION,
                        "Robby must solve: −2 − (−8) + (−3) − 4\n\nStep 1, Robby converts to addition: \"−2 + 8 + (−3) + (−4)\"\n\nRobby then gets: \"Positives: 8. Negatives: 2+3+4=9. Answer = −1\"\n\nIs Robby correct?",
                        "−2 + 8 + (−3) + (−4) → positives=8, negatives=9, answer=−1",
                        "Yes, Robby is correct — answer is −1",
                        false,
                        List.of("Yes, Robby is correct — answer is −1", "No — conversion is wrong, answer = −17", "No — answer is +1", "No — answer is −9"),
                        "Verify the conversion: −(−8) = +8 ✓. Then check: positives = 8, negatives = 2+3+4 = 9. 9−8 = 1, negative wins → −1.",
                        "s4_sub_pos_neg",
                        "Convert all: −2 + 8 + (−3) + (−4). Own pies: 8. Owe pies: 2+3+4=9. 9 pies owed > 8 pies owned → negative wins. 9−8=1 still owed → −1. Robby's conversion AND grouping are both correct — answer is −1!"),

                    Question.mcq("m4_c5_q2", QuestionType.DEBUGGING,
                        "Robby solves 5 − (−2) − (−7) + (−3):\n\nRobby says: \"= 5 − 2 − 7 + (−3) = 5 − 2 − 7 − 3 = −7\"\n\nWhat is the CORRECT answer?",
                        "5 − (−2) − (−7) + (−3) = 5 − 2 − 7 − 3 = −7",
                        "+11",
                        true,
                        List.of("+11", "−7", "+7", "−11"),
                        "Robby forgot to flip the signs! −(−2) = +2 and −(−7) = +7. Redo from scratch.",
                        "s4_sub_pos_neg",
                        "Cancelling a debt = gaining pies! −(−2) = +2 and −(−7) = +7. So: 5 + 2 + 7 + (−3). Own: 5+2+7=14 pies. Owe: 3 pies. 14−3=11 → +11. Robby forgot that removing a debt ADDS pies — he subtracted when he should have added!")
                ))
        )));

        // ══════════════════════════════════════════════════════════════════════
        // SUBTOPIC 5: Multiplication & Division of Integers  (5 concepts × 2 = 10)
        // ══════════════════════════════════════════════════════════════════════
        reg(new Module("m5", "Multiplication and Division of Integers", 0.65, List.of(

            new Concept("m5_c1", "Multiplying Integers — The Sign Rules",
                "Multiplication follows TWO simple sign rules:\n\n" +
                "✅ SAME SIGNS → POSITIVE result\n" +
                "  (+) × (+) = (+)   e.g. 3 × 4 = +12\n" +
                "  (−) × (−) = (+)   e.g. −3 × −4 = +12\n\n" +
                "❌ DIFFERENT SIGNS → NEGATIVE result\n" +
                "  (+) × (−) = (−)   e.g. 3 × (−4) = −12\n" +
                "  (−) × (+) = (−)   e.g. −3 × 4 = −12\n\n" +
                "Memory trick: 'Same signs, smiley face (+). Different signs, sad face (−)!'",
                "🚢 A submarine descends 5 metres every minute. After 4 minutes: 4 × (−5) = −20 metres. " +
                "The submarine is 20 metres BELOW the surface.",
                List.of(
                    Question.mcq("m5_c1_q1", QuestionType.DEBUGGING,
                        "The robot is taught the sign rule. It then calculates:\n\"−3 × (−4) = −12 because there are negative numbers!\"\n\nWhat is the CORRECT answer?",
                        "−3 × (−4) = −12 because there are negative numbers!",
                        "+12",
                        true,
                        List.of("+12", "−12", "+7", "−7"),
                        "Both numbers are NEGATIVE — same signs always give a POSITIVE result!",
                        "s5_mul_neg_neg",
                        "Picture 2 groups of 3 pizza-slice debts (−2 × −3). Removing (cancelling) those debt groups = gaining pizza slices! 2×3=6, and removing negatives → positive. Same: −3×(−4)=+12. Two negatives always multiply to a POSITIVE!"),

                    Question.dropdown("m5_c1_q2", QuestionType.PREDICTION,
                        "Robby must figure out the SIGN (not the value) of: (−5) × (+8)\n\nRobby says: \"I think the sign will be POSITIVE because I see a big number 8!\"",
                        "The sign of (−5) × (+8) is positive because 8 is big!",
                        "Negative — different signs give a negative product",
                        true,
                        List.of("Negative — different signs give a negative product", "Positive — same signs give a positive product", "Positive — because 8 > 5", "Zero — positives and negatives cancel"),
                        "Sign rule: are the signs the SAME or DIFFERENT? −5 is negative, +8 is positive.",
                        "s5_mul_pos_neg",
                        "−5 is one sign, +8 is another — DIFFERENT signs. Think: 8 people each receive a pizza debt (negative outcome) = 8 pizza debts. Different signs always → NEGATIVE result. (−5)×(+8)=−40. The number 8 being large doesn't make it positive!")
                )),

            new Concept("m5_c2", "Dividing Integers — Same Sign Rules",
                "Division follows the EXACT SAME sign rules as multiplication!\n\n" +
                "✅ SAME SIGNS → POSITIVE result\n" +
                "  (+) ÷ (+) = (+)   e.g. 12 ÷ 4 = +3\n" +
                "  (−) ÷ (−) = (+)   e.g. −12 ÷ (−4) = +3\n\n" +
                "❌ DIFFERENT SIGNS → NEGATIVE result\n" +
                "  (+) ÷ (−) = (−)   e.g. 12 ÷ (−4) = −3\n" +
                "  (−) ÷ (+) = (−)   e.g. −12 ÷ 4 = −3\n\n" +
                "Shortcut: Just check — are the signs the SAME or DIFFERENT?",
                "💸 Three friends share a debt equally. Together they owe ₹600 (= −600). " +
                "Each person's share = −600 ÷ 3 = −200. Verify: 3 × (−200) = −600 ✓",
                List.of(
                    Question.dropdown("m5_c2_q1", QuestionType.PREDICTION,
                        "The robot is asked to solve −12 ÷ (−4).\n\nThe robot says: \"The answer is −3 because there are negatives!\"\n\nWhat is the CORRECT answer?",
                        "−12 ÷ (−4) = −3 because there are negatives!",
                        "+3",
                        true,
                        List.of("+3", "−3", "+48", "−48"),
                        "Check the signs first: −12 and −4 are BOTH negative. Same signs = ?",
                        "s5_div_neg_pos",
                        "12 pizza-slice debts (−12) split into groups of 4 pizza debts (÷−4). How many groups? 3 equal groups. Both signs are negative (same!) → POSITIVE result. −12 ÷ (−4) = +3. Same signs in division always give a positive answer!"),

                    Question.mcq("m5_c2_q2", QuestionType.DEBUGGING,
                        "Robby calculates +36 ÷ (−9) and says:\n\"Both 36 and 9 are big numbers, so the answer must be positive: +4!\"\n\nWhat is the CORRECT answer?",
                        "36 and 9 are big, so 36 ÷ (−9) = +4!",
                        "−4",
                        true,
                        List.of("−4", "+4", "−36", "+36"),
                        "Signs: +36 is positive, −9 is negative. DIFFERENT signs → NEGATIVE result!",
                        "s5_div_neg_pos",
                        "36 real pizza slices (+36) shared into groups of 9 debt-slices (÷−9). One positive, one negative = DIFFERENT signs → NEGATIVE result. 36÷9=4, apply negative sign → −4. The size of 36 and 9 is irrelevant — only the signs matter for determining the sign!")
                )),

            new Concept("m5_c3", "Division by Zero and Properties",
                "Two special rules you must NEVER forget:\n\n" +
                "🚫 DIVISION BY ZERO = UNDEFINED\n" +
                "  7 ÷ 0 = undefined (impossible — no answer exists!)\n" +
                "  Ask: 'How many groups of 0 make 7?' — impossible!\n\n" +
                "✅ ZERO DIVIDED BY ANYTHING = 0\n" +
                "  0 ÷ 7 = 0 (share 0 pies among 7 people — everyone gets nothing)\n\n" +
                "Properties of multiplication:\n" +
                "• Any number × 0 = 0  (multiplicative zero)\n" +
                "• Any number × 1 = itself  (multiplicative identity)\n" +
                "• Order doesn't matter: a × b = b × a  (commutative)",
                "🍕 Ravi has 0 pizzas to share with 7 friends. Each gets 0 ÷ 7 = 0 slices — nothing for everyone! " +
                "But 'divide 7 pizzas into groups of 0' is IMPOSSIBLE — that's why division by zero is undefined.",
                List.of(
                    Question.mcq("m5_c3_q1", QuestionType.DEBUGGING,
                        "The robot tries to calculate 7 ÷ 0 on its calculator and says:\n\"The answer is 0 because dividing by zero gives zero!\"\n\nWhat is the robot's mistake?",
                        "7 ÷ 0 = 0 because dividing by zero gives zero!",
                        "Division by zero is undefined — no answer exists",
                        true,
                        List.of("Division by zero is undefined — no answer exists", "The answer is actually 7", "The answer is actually 1", "The robot is correct, 7 ÷ 0 = 0"),
                        "Is there ANY number that, when multiplied by 0, gives 7?",
                        "s5_div_neg_pos",
                        "7 pizzas to share. If we share into groups of 0 slices each — how many groups? You can never finish, it's impossible! If 7÷0=x, then x×0 must equal 7. But any number × 0 = 0, never 7. Division by zero is UNDEFINED — no answer exists!"),

                    Question.trueFalse("m5_c3_q2", QuestionType.RULE_TEACHING,
                        "Robby says: \"0 ÷ (−50) = 0, because zero divided by anything is always zero.\"\n\nIs Robby CORRECT?",
                        "0 ÷ (−50) = 0 because zero divided by anything is zero!",
                        "True",
                        true,
                        "Zero divided by any NON-ZERO integer is always 0. (This is different from dividing BY zero!)",
                        "s5_div_neg_pos",
                        "0 pizzas shared among 50 people (or 50 pizza-debt groups) — everyone gets 0 slices, no matter the sign of the denominator! 0 ÷ anything(≠0) = 0 always. Robby is correct! Remember: 0÷x=0 but x÷0=undefined — very different!")
                )),

            new Concept("m5_c4", "Multiplying Multiple Integers — Counting Negatives",
                "When multiplying more than two integers, the sign of the result depends only on how many NEGATIVE numbers are in the product:\n\n" +
                "• EVEN number of negatives → POSITIVE result\n" +
                "• ODD number of negatives → NEGATIVE result\n\n" +
                "Examples:\n" +
                "(−2) × (−3) × (−4) = 3 negatives (ODD) → −24\n" +
                "(−2) × (−3) × (−4) × (−1) = 4 negatives (EVEN) → +24\n\n" +
                "Calculate the absolute value separately, then apply the sign!",
                "🔋 Each negative battery flip reverses the current. One flip → negative. Two flips → positive again. " +
                "Three flips → negative. Even flips always return to positive!",
                List.of(
                    Question.mcq("m5_c4_q1", QuestionType.PREDICTION,
                        "Robby must find the SIGN of: (−1) × (−1) × (−1) × (−1) × (−1)\n\nRobby counts: \"Five −1s. Five is odd so the answer is NEGATIVE!\"\n\nIs Robby's reasoning correct?",
                        "Five −1s. Odd number of negatives → negative result!",
                        "Yes — Robby's reasoning is correct, answer is −1",
                        false,
                        List.of("Yes — Robby's reasoning is correct, answer is −1", "No — five −1s multiply to +1", "No — five negatives always cancel to zero", "No — the sign rule only works for two numbers"),
                        "Count the negatives: 5 (odd) → negative. Then calculate the value: 1×1×1×1×1 = 1. So the answer is?",
                        "s5_mul_neg_neg",
                        "Each pizza debt (−1) flips the sign. 1 debt = negative. 2 debts cancelled = positive. 3 debts = negative again. 4 = positive. 5 = negative! Five −1s: odd count of negatives → NEGATIVE result. Value: 1×1×1×1×1=1 → answer = −1. Robby is correct!"),

                    Question.dropdown("m5_c4_q2", QuestionType.DEBUGGING,
                        "Robby calculates (−2) × (−3) × (+4) and says:\n\"Three numbers, so I need to check 3 signs! I see two negatives: even → positive! 2×3×4 = 24 so answer is +24.\"",
                        "Two negatives (even), so (−2)×(−3)×(+4) = +24!",
                        "+24 — Robby is completely correct",
                        false,
                        List.of("+24 — Robby is completely correct", "−24 — Robby miscounted the negatives", "+6 — Robby calculated the value wrong", "−6 — both the sign and value are wrong"),
                        "Count only the NEGATIVE numbers: how many are negative in (−2), (−3), (+4)?",
                        "s5_mul_neg_neg",
                        "Only count the NEGATIVE pizza pies: −2 and −3 are negative (2 total = EVEN). EVEN negatives → POSITIVE sign (two debts cancelled = gain!). Value: 2×3×4=24. EVEN negatives + value 24 → answer = +24. Robby counted and computed correctly — he IS right!")
                )),

            new Concept("m5_c5", "Mixed Multiplication and Division",
                "When an expression has both × and ÷ with integers:\n\n" +
                "Step 1: Count total negative numbers in the expression\n" +
                "Step 2: Even negatives → positive result; Odd negatives → negative result\n" +
                "Step 3: Calculate the numerical value ignoring signs\n" +
                "Step 4: Apply the sign\n\n" +
                "Example: (−6) × (−2) ÷ (−4) × (−1)\n" +
                "= 4 negatives (EVEN) → positive\n" +
                "= 6×2÷4×1 = 3 → answer = +3",
                "🏭 A factory loses ₹5 per unit (−5). It makes 8 units. The loss is split across 4 departments. " +
                "Each department's share: (−5) × 8 ÷ 4 = −40 ÷ 4 = −10. Each department absorbs ₹10 loss.",
                List.of(
                    Question.mcq("m5_c5_q1", QuestionType.PREDICTION,
                        "Robby must evaluate: (−4) × (−3) ÷ 6\n\nRobby says: \"Two negatives = positive! 4×3÷6 = 2. Answer = +2.\"",
                        "(−4)×(−3)÷6: two negatives (positive), 4×3÷6=2, answer = +2!",
                        "Yes, Robby is correct — answer is +2",
                        false,
                        List.of("Yes, Robby is correct — answer is +2", "No — answer is −2", "No — answer is +12", "No — answer is −12"),
                        "Count negatives: −4 and −3 → 2 negatives (even → positive). Value: 4×3÷6=12÷6=2. Sign = positive → +2.",
                        "s5_mul_neg_neg",
                        "Count pizza-debt signs: −4 and −3 are both negative (2 negatives = EVEN) → POSITIVE result. Calculate: 4×3=12 pizza slices, then split 6 ways → 2 slices each. EVEN negatives → positive sign → +2. Robby is correct on both the sign and the value!"),

                    Question.mcq("m5_c5_q2", QuestionType.DEBUGGING,
                        "Robby evaluates: 24 ÷ (−4) × (−3)\n\nRobby works left to right: \"24 ÷ (−4) = −6. Then −6 × (−3) = +18.\"\n\nRobby says the answer is +18. Is this correct?",
                        "24÷(−4)=−6, then −6×(−3)=+18. Answer = +18!",
                        "Yes, Robby is correct — answer is +18",
                        false,
                        List.of("Yes, Robby is correct — answer is +18", "No — answer is −18", "No — answer is +2", "No — answer is −6"),
                        "Verify each step: 24 ÷ (−4): different signs → negative. 24÷4=6 → −6. Then −6 × (−3): same signs → positive. 6×3=18 → +18.",
                        "s5_mul_neg_neg",
                        "Step 1: 24 real pizza slices ÷ (−4) debt groups → different signs → negative → −6. Step 2: −6 pizza debts × (−3) cancel groups → same signs → positive → 6×3=+18. Work left to right, check signs at each step. Robby is right at every step — answer = +18!")
                ))
        )));
    }

    // ── Public API ─────────────────────────────────────────────────────────────
    public Map<String, Module>  getAllModules()    { return modules; }
    public Module               getModule(String id) { return modules.get(id); }
    public Concept              getConcept(String id){ return conceptIndex.get(id); }
    public Map<String, Concept> getConceptIndex() { return conceptIndex; }

    public List<String> getModuleOrder() {
        return List.of("m1","m2","m3","m4","m5");
    }

    public int getTotalQuestions() {
        return modules.values().stream()
            .flatMap(m -> m.getConcepts().stream())
            .mapToInt(c -> c.getQuestions().size()).sum();
    }

    public int getTotalHints() {
        return (int) modules.values().stream()
            .flatMap(m -> m.getConcepts().stream())
            .flatMap(c -> c.getQuestions().stream())
            .filter(q -> q.getHint() != null).count();
    }

    public List<String> getConceptOrder() {
        List<String> order = new ArrayList<>();
        for (String mid : getModuleOrder()) {
            Module m = modules.get(mid);
            if (m != null) m.getConcepts().forEach(c -> order.add(c.getConceptId()));
        }
        return order;
    }
}
