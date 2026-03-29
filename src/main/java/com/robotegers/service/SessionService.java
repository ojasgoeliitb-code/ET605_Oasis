package com.robotegers.service;

import com.robotegers.model.Concept;
import com.robotegers.model.Question;
import com.robotegers.model.StudentSession;
import com.robotegers.payload.MergeSessionPayload;
import com.robotegers.payload.PedResponse;
import com.robotegers.payload.SyncRequest;
import com.robotegers.service.AnswerEvaluatorService.EvalResult;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionService {

    private final ModuleService moduleService;
    private final AnswerEvaluatorService evaluator;
    private final Map<String, StudentSession> sessions = new ConcurrentHashMap<>();

    public SessionService(ModuleService moduleService, AnswerEvaluatorService evaluator) {
        this.moduleService = moduleService;
        this.evaluator = evaluator;
    }

    // ── Session lifecycle ──────────────────────────────────────────────────────
    public StudentSession createSession(String studentId) {
        String id = "s_" + studentId + "_g7int_" + System.currentTimeMillis();
        StudentSession s = new StudentSession(id, studentId, "grade7_integers");
        s.setCurrentModuleId("m1");
        s.setCurrentConceptId("m1_c1");
        s.setCurrentQuestionIndex(0);
        s.setTotalQuestions(moduleService.getTotalQuestions());
        s.setTotalHintsEmbedded(moduleService.getTotalHints());
        sessions.put(id, s);
        return s;
    }

    public StudentSession getSession(String id)         { return sessions.get(id); }
    public void           saveSession(StudentSession s) { sessions.put(s.getSessionId(), s); }

    // ── Answer evaluation ──────────────────────────────────────────────────────
    public EvalResult evaluate(String studentAnswer, Question question) {
        return evaluator.evaluate(studentAnswer, question);
    }

    // ── Sync ──────────────────────────────────────────────────────────────────
    public PedResponse processSync(StudentSession session, SyncRequest req) {
        session.applySync(req);
        saveSession(session);
        Concept concept = moduleService.getConcept(req.getConceptId());
        PedResponse resp = validatePedAction(req);
        resp.setConceptId(req.getConceptId());
        if (concept != null) {
            if ("story".equals(resp.getAction()))    resp.setStoryContent(concept.getStoryExample());
            if ("remedial".equals(resp.getAction())) {
                resp.setRemedialContent(concept.getExplanation());
                resp.setStoryContent(concept.getStoryExample());
            }
        }
        return resp;
    }

    private PedResponse validatePedAction(SyncRequest req) {
        String js = req.getPedAction();
        if (req.getMasteryScore() >= 0.9 && req.getConsecutiveWrong() == 0 && !"advance".equals(js))
            return PedResponse.override("advance", "Excellent! Time to move ahead.", "Server: mastery ≥ 0.9");
        if (req.getStruggleIndex() > 0.9 && !"remedial".equals(js))
            return PedResponse.override("remedial", "Let's look at this together from the start.", "Server: struggle > 0.9");
        String message = switch (js != null ? js : "continue") {
            case "advance"   -> "Great job! Let's try something harder.";
            case "remedial"  -> "Let's take a step back and look at this together.";
            case "visual_l2" -> "Let me show you this on the number line.";
            case "guided_l3" -> "Here's a worked example to guide you.";
            case "hint_l1"   -> "Here's a hint to point you in the right direction.";
            case "story"     -> "Here's a story that might help!";
            default          -> null;
        };
        return PedResponse.confirm(js != null ? js : "continue", message, req.getPedReason());
    }

    // ── Navigation ─────────────────────────────────────────────────────────────
    public record NavResult(boolean conceptDone, boolean moduleDone, boolean chapterDone,
                            String nextConceptId, String nextModuleId) {}

    public NavResult advance(StudentSession session) {
        String cid = session.getCurrentConceptId();
        String mid = session.getCurrentModuleId();
        var module   = moduleService.getModule(mid);
        var concepts = module.getConcepts();
        int cPos = -1;
        for (int i = 0; i < concepts.size(); i++)
            if (concepts.get(i).getConceptId().equals(cid)) { cPos = i; break; }

        int qNext = session.getCurrentQuestionIndex() + 1;
        var concept = concepts.get(cPos);
        if (qNext < concept.getQuestions().size()) {
            session.setCurrentQuestionIndex(qNext);
            return new NavResult(false, false, false, cid, mid);
        }
        if (cPos + 1 < concepts.size()) {
            String nextCid = concepts.get(cPos + 1).getConceptId();
            session.setCurrentConceptId(nextCid);
            session.setCurrentQuestionIndex(0);
            return new NavResult(true, false, false, nextCid, mid);
        }
        session.markModuleMastery(mid);
        var mOrder = moduleService.getModuleOrder();
        int mPos = mOrder.indexOf(mid);
        if (mPos + 1 < mOrder.size()) {
            String nextMid = mOrder.get(mPos + 1);
            String nextCid = moduleService.getModule(nextMid).getConcepts().get(0).getConceptId();
            session.setCurrentModuleId(nextMid);
            session.setCurrentConceptId(nextCid);
            session.setCurrentQuestionIndex(0);
            return new NavResult(true, true, false, nextCid, nextMid);
        }
        return new NavResult(true, true, true, null, null);
    }

    // ── Merge Team payload ─────────────────────────────────────────────────────
    public MergeSessionPayload buildMergePayload(StudentSession session) {
        session.finalise(session.getSessionStatus().equals("in_progress")
                ? "completed" : session.getSessionStatus());
        var p = new MergeSessionPayload();
        p.setStudentId(session.getStudentId());
        p.setSessionId(session.getSessionId());
        p.setChapterId(session.getChapterId());
        p.setTimestamp(Instant.now().toString());
        p.setSessionStatus(session.getSessionStatus());
        p.setCorrectAnswers(session.getCorrectAnswers());
        p.setWrongAnswers(session.getWrongAnswers());
        p.setQuestionsAttempted(session.getQuestionsAttempted());
        p.setTotalQuestions(session.getTotalQuestions());
        p.setRetryCount(session.getRetryCount());
        p.setHintsUsed(session.getHintsUsed());
        p.setTotalHintsEmbedded(session.getTotalHintsEmbedded());
        p.setTimeSpentSeconds(session.getTimeSpentSeconds());
        p.setTopicCompletionRatio(Math.round(session.getTopicCompletionRatio() * 100.0) / 100.0);
        return p;
    }
}
