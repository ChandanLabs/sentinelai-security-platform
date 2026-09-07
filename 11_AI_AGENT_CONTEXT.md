# SentinelAI — AI Agent Continuation Context

## READ FIRST
When continuing this project in a new AI-agent session, read ONLY these files first:
1. `00_PROJECT_MEMORY.md` — locked stack, security rules, current status, agent rules.
2. `11_AI_AGENT_CONTEXT.md` — this compact handoff.
3. `07_ROADMAP.md` — phase boundaries.
4. The current phase report, currently `PHASE_11_IMPLEMENTATION.md`.
5. Only then inspect source files directly relevant to the requested task.

For Phase 14/final submission work, also read `PHASE_14_IMPLEMENTATION.md` and the specific documentation file requested. Do not scan the whole repository unless required.

Do NOT ask the agent to re-read the whole repository.

## PROJECT
SentinelAI is a defensive, cloud-deployable security monitoring platform. Stack is locked: Next.js/TypeScript/Tailwind; Java 21/Spring Boot/Security/JPA/JWT/OpenAPI/JUnit/Mockito; PostgreSQL; Python 3.11/FastAPI/pandas/NumPy/scikit-learn/joblib/Isolation Forest; Docker/Compose/GitHub Actions; Azure primary.

## CURRENT STATUS
Phases 1–14 implementation complete. Verification is intentionally pending until the final whole-project verification pass. Verification is intentionally pending until the project implementation is finished. Do not claim the project is verified.

## EVENT PIPELINE
Event ingestion -> Rule detection -> AI anomaly detection -> Risk scoring -> Alert generation -> Incident management -> Dashboard.

## PHASE 7 RISK FORMULA
Severity base: INFO 5, LOW 20, MEDIUM 45, HIGH 70, CRITICAL 90.
Rule component = max(event severity, matched rule severity) + up to 10 points for multiple matches.
If AI score exists: `0.55 * ruleComponent + 0.45 * anomalyScore*100`.
If AI unavailable: use rule component as deterministic fallback.
Levels: <25 LOW, 25–49 MEDIUM, 50–74 HIGH, >=75 CRITICAL.

## IMPORTANT CODE LOCATIONS
- Event pipeline: `backend/src/main/java/com/sentinel/backend/core/event/EventService.java`
- Rule engine: `.../core/detection/RuleEngine.java`
- AI service integration: `.../core/ai/AiServiceClient.java`
- Risk scoring: `.../core/risk/RiskScoreService.java`
- Risk API: `.../core/risk/RiskScoreController.java`
- AI FastAPI: `ai-service/main.py`

## PHASE BOUNDARY
Phase 14 documentation/PPT/viva implementation is complete. The next activity is final whole-project verification/audit. A pre-final independent static audit has already been performed and corrected; see `PRE_FINAL_AUDIT_FIXES.md`. Do not add new feature phases unless final verification reveals a genuine defect. Azure deployment assets are implemented, but do not provision Azure until final verification or explicit approval.

## CHANGE RULES
Inspect before editing. Make the smallest change. Preserve architecture and API conventions. No secrets. No offensive cyber functionality. Use synthetic security data. Update memory/docs after meaningful architecture decisions.

## FINAL VERIFICATION
At the end of implementation, run backend tests, AI tests, frontend checks, Docker Compose build/start, API smoke tests, security/RBAC checks, and end-to-end event pipeline checks. Record real results in verification reports.


## Phase 9 continuation context
- Phase 9 frontend dashboard is implemented under `frontend/src/app/`.
- API integration is centralized in `frontend/src/lib/api.ts`.
- Authentication pages: `frontend/src/app/login/page.tsx`.
- Main console: `frontend/src/app/dashboard/page.tsx`.
- Browser API URL: `NEXT_PUBLIC_API_URL`, default `http://localhost:8080/api/v1`.
- Do not redesign the backend contract or add a new frontend state-management library without approval.
- Read this file plus `PHASE_9_IMPLEMENTATION.md` before continuing; do not scan the entire repository unless a dependency requires it.

## Phase 10 continuation state
- Phase 10 security hardening and test scaffolding has been implemented.
- Full verification is intentionally pending until the complete project is implemented.
- Do not start deployment/CI/CD work before Phase 10 is reviewed and the project reaches the final verification pass.
- Continue using `11_AI_AGENT_CONTEXT.md` and the current phase report as the compact handoff context.


## Phase 11 continuation state
- Docker Compose is the local orchestration source of truth for postgres, ai-service, backend, and frontend.
- Backend Docker build uses Maven + Java 21 multi-stage image.
- Frontend Docker build uses Next.js standalone output and `NEXT_PUBLIC_API_URL` build arg.
- AI service persists model artifacts in the Compose `ai_model_data` volume.
- GitHub Actions workflow: `.github/workflows/ci.yml`.
- CI jobs: backend Maven tests, AI Python compilation, frontend production build, Docker Compose config validation, and image builds.
- Docker health checks are defined for PostgreSQL, AI service, and backend; frontend waits for backend health.
- Environment variables are documented in `.env.example`; never commit `.env` or real credentials.
- Phase 11 verification is pending by project policy.

## Phase 12 continuation state
- Phase 12 Azure deployment implementation is complete; cloud provisioning/verification is pending.
- Read `PHASE_12_IMPLEMENTATION.md` and `infrastructure/azure/AZURE_DEPLOYMENT.md` before modifying deployment assets.
- Azure resources: Container Apps, ACR Basic, PostgreSQL Flexible Server, Log Analytics.
- Azure container apps use system-assigned managed identities for ACR pulls.
- Deployment is staged: deploy backend/AI -> obtain backend URL -> build frontend with `NEXT_PUBLIC_API_URL` -> deploy frontend -> set exact backend CORS origin.
- Do not add Kubernetes, Redis, Kafka, Front Door, or other infrastructure without an approved architecture decision.
- Do not provision Azure resources until final local verification or explicit user approval.


## Phase 13 Continuation
- Phase 13 demo preparation is implemented.
- Read `DEMO_RUNBOOK.md` before changing demo assets.
- `scripts/demo-seed.ps1` creates synthetic baseline/suspicious data; do not replace it with real data.
- `scripts/demo-promote-user.sql` is local/demo-only and must never be used as a production admin bootstrap.
- Phase 14 documentation/PPT/viva is complete; next activity is final verification.


## Phase 14 continuation
- Documentation package is complete: report guide, PPT outline, viva Q&A, traceability matrix, results template, and final submission checklist.
- Never invent final test/ML/performance/cloud results. Use `RESULTS_TEMPLATE.md` and populate it only from actual verification evidence.
- After verification, update memory, roadmap, README, and final verification reports with real results.


## Pre-final audit state
- Independent static audit completed before final testing.
- Corrective fixes are documented in `PRE_FINAL_AUDIT_FIXES.md`.
- The external audit did not execute Maven, Docker, Azure, or live APIs.
- Final local verification remains mandatory and is the next project activity.
- Do not regenerate or redesign the project during verification; fix only evidence-backed defects.
