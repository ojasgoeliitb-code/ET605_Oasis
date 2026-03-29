package com.robotegers.controller;

import com.robotegers.model.Concept;
import com.robotegers.model.Module;
import com.robotegers.model.Question;
import com.robotegers.model.StudentSession;
import com.robotegers.payload.ChapterMetadataPayload;
import com.robotegers.payload.MergeSessionPayload;
import com.robotegers.payload.PedResponse;
import com.robotegers.payload.SyncRequest;
import com.robotegers.service.AnswerEvaluatorService.EvalResult;
import com.robotegers.service.ModuleService;
import com.robotegers.service.SessionService;
import com.robotegers.service.SessionService.NavResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ITSController {

    private final SessionService sessionService;
    private final ModuleService  moduleService;

    public ITSController(SessionService s, ModuleService m) {
        sessionService = s; moduleService = m;
    }

    @PostMapping("/session/start")
    public ResponseEntity<Map<String,Object>> start(@RequestBody Map<String,String> body) {
        StudentSession s = sessionService.createSession(body.getOrDefault("student_id","anon"));
        return ResponseEntity.ok(stateOf(s));
    }

    @GetMapping("/session/{sid}")
    public ResponseEntity<Map<String,Object>> get(@PathVariable String sid) {
        StudentSession s = sessionService.getSession(sid);
        return s == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(stateOf(s));
    }

    @PostMapping("/session/{sid}/exit")
    public ResponseEntity<Map<String,Object>> exit(@PathVariable String sid) {
        StudentSession s = sessionService.getSession(sid);
        if (s == null) return ResponseEntity.notFound().build();
        s.finalise("exited_midway"); sessionService.saveSession(s);
        MergeSessionPayload p = sessionService.buildMergePayload(s);
        return ResponseEntity.ok(Map.of("message","Saved.","merge_payload",p,"payload_valid",p.isValid()));
    }

    @PostMapping("/session/{sid}/sync")
    public ResponseEntity<PedResponse> sync(@PathVariable String sid, @RequestBody SyncRequest req) {
        StudentSession s = sessionService.getSession(sid);
        if (s == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(sessionService.processSync(s, req));
    }

    @PostMapping("/session/{sid}/answer")
    public ResponseEntity<Map<String,Object>> answer(@PathVariable String sid,
                                                      @RequestBody Map<String,String> body) {
        StudentSession s = sessionService.getSession(sid);
        if (s == null) return ResponseEntity.notFound().build();
        Concept concept = moduleService.getConcept(s.getCurrentConceptId());
        if (concept == null) return ResponseEntity.badRequest().build();

        Question q  = concept.getQuestions().get(s.getCurrentQuestionIndex());
        EvalResult ev = sessionService.evaluate(body.getOrDefault("answer",""), q);

        Map<String,Object> resp = new HashMap<>();
        resp.put("correct",        ev.correct());
        resp.put("partial_credit", ev.partialCredit());
        resp.put("feedback",       ev.feedback());
        resp.put("correct_answer", q.getCorrectAnswer());
        resp.put("concept_id",     s.getCurrentConceptId());
        resp.put("question_id",    q.getQuestionId());
        resp.put("hints", Map.of(
            "l1", q.getHint() != null ? q.getHint() : "",
            "l2_image", "/images/numberlines/" + (q.getHintL2() != null ? q.getHintL2() : "") + ".svg",
            "l3", q.getGuidedExample() != null ? q.getGuidedExample() : ""
        ));

        if (ev.correct()) {
            NavResult nav = sessionService.advance(s);
            sessionService.saveSession(s); // persist navigation so fallback fetch is always fresh
            resp.put("concept_done", nav.conceptDone());
            resp.put("module_done",  nav.moduleDone());
            resp.put("chapter_done", nav.chapterDone());
            if (nav.chapterDone()) {
                s.finalise("completed"); sessionService.saveSession(s);
                MergeSessionPayload p = sessionService.buildMergePayload(s);
                resp.put("merge_payload", p);
                resp.put("payload_valid", p.isValid());
            } else {
                resp.put("next_state", stateOf(s));
            }
        }
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/modules")
    public ResponseEntity<Map<String,Object>> modules() {
        List<Map<String,Object>> list = moduleService.getAllModules().entrySet().stream().map(e -> {
            Map<String,Object> m = new HashMap<>();
            m.put("module_id",   e.getKey());
            m.put("title",       e.getValue().getTitle());
            m.put("difficulty",  e.getValue().getDifficulty());
            m.put("concept_ids", e.getValue().getConcepts().stream().map(Concept::getConceptId).toList());
            return m;
        }).toList();
        return ResponseEntity.ok(Map.of("modules",list,
            "total_concepts",  moduleService.getConceptIndex().size(),
            "total_questions", moduleService.getTotalQuestions()));
    }

    @GetMapping("/merge/chapter-metadata")
    public ResponseEntity<ChapterMetadataPayload> meta() {
        return ResponseEntity.ok(new ChapterMetadataPayload());
    }

    @GetMapping("/merge/session-payload/{sid}")
    public ResponseEntity<Map<String,Object>> mergeGet(@PathVariable String sid) {
        StudentSession s = sessionService.getSession(sid);
        if (s == null) return ResponseEntity.notFound().build();
        MergeSessionPayload p = sessionService.buildMergePayload(s);
        Map<String,Object> r = new HashMap<>();
        r.put("payload", p); r.put("payload_valid", p.isValid());
        if (!p.isValid()) r.put("validation_errors", p.validationErrors());
        return ResponseEntity.ok(r);
    }

    // ── State builder ──────────────────────────────────────────────────────────
    private Map<String,Object> stateOf(StudentSession s) {
        Map<String,Object> st = new HashMap<>();
        st.put("session_id", s.getSessionId());
        st.put("student_id", s.getStudentId());
        st.put("chapter_id", s.getChapterId());

        String mid = s.getCurrentModuleId();
        String cid = s.getCurrentConceptId();
        Module  mod = moduleService.getModule(mid);
        Concept con = moduleService.getConcept(cid);

        st.put("module_id",           mid);
        st.put("module_title",        mod != null ? mod.getTitle()         : null);
        st.put("concept_id",          cid);
        st.put("concept_title",       con != null ? con.getTitle()         : null);
        st.put("concept_explanation", con != null ? con.getExplanation()   : null);
        st.put("concept_story",       con != null ? con.getStoryExample()  : null);

        int qIdx = s.getCurrentQuestionIndex();
        st.put("question_index", qIdx);

        if (con != null && qIdx < con.getQuestions().size()) {
            Question q = con.getQuestions().get(qIdx);
            Map<String,Object> qd = new HashMap<>();
            qd.put("question_id",   q.getQuestionId());
            qd.put("type",          q.getType().name());
            qd.put("input_type",    q.getInputType().name());
            qd.put("options",       q.getOptions());
            qd.put("prompt",        q.getPrompt());
            qd.put("robot_attempt", q.getRobotAttempt());
            qd.put("robot_is_wrong",q.isRobotIsWrong());
            qd.put("hint_l2_image", "/images/numberlines/" + (q.getHintL2() != null ? q.getHintL2() : "") + ".svg");
            st.put("question", qd);
            st.put("total_questions_in_concept", con.getQuestions().size());

            // Include hints so they are available BEFORE the student answers
            Map<String,Object> hints = new HashMap<>();
            hints.put("l1", q.getHint() != null ? q.getHint() : "");
            hints.put("l2_image", "/images/numberlines/" + (q.getHintL2() != null ? q.getHintL2() : "") + ".svg");
            hints.put("l3", q.getGuidedExample() != null ? q.getGuidedExample() : "");
            st.put("hints", hints);
        }

        var order = moduleService.getModuleOrder();
        st.put("module_progress", order.indexOf(mid) + 1);
        st.put("total_modules",   order.size());

        if (mod != null) {
            var cs = mod.getConcepts();
            int cp = -1;
            for (int i = 0; i < cs.size(); i++)
                if (cs.get(i).getConceptId().equals(cid)) { cp = i; break; }
            st.put("concept_progress_in_module", cp + 1);
            st.put("total_concepts_in_module",   cs.size());
        }

        st.put("module_mastery",  s.getModuleMastery());
        st.put("session_status",  s.getSessionStatus());
        return st;
    }
}
