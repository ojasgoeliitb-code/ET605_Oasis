# Robotegers — Adaptive Tutoring System
### Grade 7 Integers · Spring Boot · Oasis ET 605 Project

---

## Overview

Robotegers is an Intelligent Tutoring System (ITS) using the **Learning-by-Teaching** paradigm.
Students *teach* a robot integer rules. The robot deliberately makes mistakes; students correct them.
The **PED Model** adapts in real time based on student performance.

---

## Architecture

```
robotegers/
├── pom.xml
└── src/main/
    ├── java/com/robotegers/
    │   ├── RobotagersApplication.java      # Spring Boot entry point
    │   ├── controller/
    │   │   ├── ITSController.java          # REST API (/api/**)
    │   │   └── WebController.java          # Serves HTML frontend
    │   ├── model/
    │   │   ├── Module.java                 # Learning module
    │   │   ├── Question.java               # Question with type, hints, remediation
    │   │   └── StudentSession.java         # Session state + PED model
    │   ├── payload/
    │   │   ├── MergeSessionPayload.java    # Merge Team session schema
    │   │   └── ChapterMetadataPayload.java # Merge Team chapter metadata
    │   └── service/
    │       ├── ModuleService.java          # All 6 modules + 18 questions
    │       └── SessionService.java         # Answer eval, PED logic, payload builder
    └── resources/
        ├── application.properties
        └── templates/index.html            # Full frontend (Thymeleaf)
```

---

## Running the Application

### Prerequisites
- Java 17+
- Maven 3.8+

### Run
```bash
cd robotegers
mvn spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080) in your browser.

---

## Modules Covered

| # | Module | Difficulty | Questions |
|---|--------|-----------|-----------|
| 1 | Understanding Integers | 0.30 | Rule Teaching, Prediction, Debugging |
| 2 | Integers on the Number Line | 0.35 | Rule Teaching, Debugging, Prediction |
| 3 | Addition of Integers | 0.50 | Rule Teaching, Debugging, Prediction |
| 4 | Subtraction of Integers | 0.55 | Rule Teaching, Debugging, Prediction |
| 5 | Multiplication of Integers | 0.65 | Rule Teaching, Debugging, Prediction |
| 6 | Division of Integers | 0.70 | Rule Teaching, Debugging, Prediction |

---

## PED Model — Adaptive Logic

```
Consecutive Errors → PED State → Action
──────────────────────────────────────────────────
0                  → NORMAL    → Proceed normally
1                  → HINT      → Show concept hint
2                  → NUMBER_LINE → Concept clarification / visual cue
3+                 → REMEDIATION → Full guided worked example
```

On a correct answer: PED state resets to NORMAL. Module mastery score updates.

---

## REST API Reference

### Session Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/session/start` | Start a new session |
| GET | `/api/session/{id}` | Get current session state |
| POST | `/api/session/{id}/exit` | Exit mid-session (saves payload) |

**Start session body:**
```json
{ "student_id": "student_1042" }
```

### Interaction

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/session/{id}/answer` | Submit an answer |
| POST | `/api/session/{id}/hint` | Request a hint |

**Answer body:**
```json
{ "answer": "Integers include positive, negative numbers and zero." }
```

**Answer response:**
```json
{
  "correct": true,
  "ped_action_type": "CORRECT",
  "ped_message": "✅ Great job! You taught the robot correctly.",
  "module_complete": false,
  "chapter_complete": false,
  "next_state": { ... }
}
```

### Modules

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/modules` | List all modules |
| GET | `/api/modules/{moduleId}` | Module detail |

### Merge Team Integration

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/merge/chapter-metadata` | Chapter metadata payload |
| GET | `/api/merge/session-payload/{id}` | Session payload for a session |

---

## Merge Team Payload

Generated automatically at chapter completion or confirmed exit.

```json
{
  "student_id": "student_1042",
  "session_id": "s_student_1042_g7int_1710000000000",
  "chapter_id": "grade7_integers",
  "timestamp": "2026-03-18T10:30:00Z",
  "session_status": "completed",
  "correct_answers": 14,
  "wrong_answers": 4,
  "questions_attempted": 18,
  "total_questions": 18,
  "retry_count": 3,
  "hints_used": 5,
  "total_hints_embedded": 18,
  "time_spent_seconds": 1240,
  "topic_completion_ratio": 0.78
}
```

Payload is validated against Merge Team sanity rules before being marked `payload_valid: true`.

---

## Question Types

| Type | Robot Behaviour | Student Task |
|------|----------------|-------------|
| RULE_TEACHING | Robot asks the student to explain a rule | Student explains correctly; robot may have a wrong belief |
| PREDICTION | Robot will attempt a calculation | Student predicts the correct answer |
| DEBUGGING | Robot makes a deliberate error | Student identifies and corrects the mistake |

---

## Answer Evaluation

- **Numeric answers**: Normalised and compared (handles − vs - sign variants)
- **Yes/No answers**: Flexible matching
- **Explanation answers** (RULE_TEACHING): Keyword matching — at least 40% of significant keywords from the model answer must appear in the student's response

---

## Notes for Integration

- Sessions are stored in-memory (`ConcurrentHashMap`). Replace with a database for production.
- `student_id` must come from your platform's auth/session layer — pass it in the `/session/start` body.
- The Merge Team computes performance scores and recommendations centrally. This module only sends clean interaction data.
- On network failure: store the payload locally and retry with the same `session_id` (safe to resend).
