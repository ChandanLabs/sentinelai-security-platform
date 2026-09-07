# PROJECT IDENTITY

**Project Name:** SentinelAI
**Academic Title:** Design and Development of an AI-Powered Intelligent Security Monitoring and Anomaly Detection Platform
**Project Type:** Final-year B.Tech project
**Domains:** Artificial Intelligence, Cybersecurity, Backend Engineering, Cloud Computing, DevOps

## Primary Objective
Build a realistic, secure, cloud-deployable security monitoring platform that collects application/security events, detects suspicious behavior through deterministic rules and machine-learning-based anomaly detection, calculates risk scores, generates alerts, and provides an analyst dashboard for investigation.

# LOCKED TECHNOLOGY STACK
- **Frontend:** Next.js, TypeScript, Tailwind CSS, shadcn/ui, Recharts, Lucide, React Hook Form, Zod
- **Backend:** Java, Spring Boot, Spring Security, Spring Data JPA, Hibernate, Bean Validation, JWT, OpenAPI, JUnit 5, Mockito
- **Database:** PostgreSQL
- **AI Service:** Python, FastAPI, Pandas, NumPy, scikit-learn, joblib (Algorithm: Isolation Forest)
- **Infrastructure:** Docker, Docker Compose, Git, GitHub, GitHub Actions
- **Cloud:** Microsoft Azure (Primary), AWS (Fallback)

# ARCHITECTURE PRINCIPLES
Frontend -> Spring Boot REST API -> PostgreSQL
Spring Boot -> Python FastAPI (Machine Learning/Anomaly Detection)

**Event Flow:**
Event ingestion -> Normalization -> Rule detection -> AI anomaly detection -> Risk scoring -> Alert generation -> Incident management

- Cloud infrastructure must remain an implementation detail.
- The application must remain runnable locally.

# CODING PRINCIPLES
- Clear package structure
- Small modules
- Meaningful names
- Typed interfaces
- Environment-based configuration
- Centralized error handling
- **No dead code or fake services**

# SECURITY PRINCIPLES
- Defensive monitoring and educational system.
- NO credential theft, malware, persistence mechanisms, exploit automation, destructive actions, or offensive attack tooling.
- Security demonstrations must use synthetic data.
- Standard security practices: password hashing, RBAC, input validation, API security, CORS, audit logging.

# UI DESIGN PRINCIPLES
- Theme: Dark enterprise security console.
- Colors: Background: #0B1020, Surface: #111827, Primary: #6366F1, Success: #22C55E, Warning: #F59E0B, Danger: #EF4444, Text: #F8FAFC, Muted: #94A3B8.
- Fonts: Inter (normal UI), JetBrains Mono (logs, IDs, IPs).
- Characteristics: Professional, minimal, readable, accessible, data-focused, consistent.

# COST RULES
- The project must remain locally functional even if cloud credits expire.
- Do NOT provision cloud infrastructure unnecessarily during early phases.

# CURRENT PROJECT STATUS
CURRENT PHASE: PHASE 14 — COLLEGE DOCUMENTATION / PPT / VIVA

**Completed Work:**
- Phase 1 implementation: COMPLETE
- Phase 1 verification: PENDING
- Phase 2 backend implementation: COMPLETE
- Phase 2 verification: PENDING
- Phase 3 core backend implementation: COMPLETE
- Phase 3 verification: PENDING
- Phase 4 event ingestion implementation: COMPLETE
- Phase 4 verification: PENDING
- Phase 5 rule-based detection implementation: COMPLETE
- Phase 5 verification: PENDING
- Phase 6 AI anomaly detection implementation: COMPLETE
- Phase 6 verification: PENDING
- Phase 7 risk scoring implementation: COMPLETE
- Phase 7 verification: PENDING
- Phase 8 alerts and incidents implementation: COMPLETE
- Phase 8 verification: PENDING

**Known Limitations:**
- Phase 4 ingestion accepts authenticated structured JSON events; normalization is intentionally limited to canonical field formatting and JSON metadata validation.
- Phase 5 contains a deliberately small set of deterministic, explainable defensive rules. AI anomaly detection is implemented with a separate FastAPI Isolation Forest service. Risk scoring is implemented in Phase 7. Phase 8 generates alerts for HIGH/CRITICAL risk, automatically escalates CRITICAL alerts into incidents, and provides analyst incident/alert lifecycle management.

**Important Decisions:**
- See `08_DECISIONS.md`

# INSTRUCTIONS TO FUTURE AI AGENTS
- Strictly follow phase boundaries. Do not implement features outside the current phase.
- Do not blindly overwrite files. Inspect, understand, check for existing functionality, and make the smallest appropriate change.
- Never regenerate the entire project unnecessarily.
- Never change the locked stack.
- Never say "works" without running relevant verifications.
- At the start of every new session, read this file plus `11_AI_AGENT_CONTEXT.md` and the current phase implementation report before modifying code.
- When a phase is implemented, update the memory, roadmap, API contract, database model, README, and phase report so another AI agent can continue without re-reading the whole repository.


## Phase 9 Status
Frontend Security Dashboard implemented. Verification remains pending until final project integration testing. See `PHASE_9_IMPLEMENTATION.md`.

## Phase 10 continuation state
- Phase 10 security hardening and test scaffolding has been implemented.
- Full verification is intentionally pending until the complete project is implemented.
- Do not start deployment/CI/CD work before Phase 10 is reviewed and the project reaches the final verification pass.
- Continue using `11_AI_AGENT_CONTEXT.md` and the current phase report as the compact handoff context.

## Phase 12 continuation state
- Phase 12 Azure deployment assets are implemented but cloud provisioning is intentionally pending.
- Azure target: Container Apps for frontend/backend/AI, PostgreSQL Flexible Server, Basic ACR, Log Analytics.
- Managed identities are used for ACR image pulls; no registry passwords are stored in source.
- Deployment is staged because the Next.js frontend requires the deployed backend URL at build time.
- `infrastructure/azure/main.bicep` and `scripts/azure-deploy.ps1` are the deployment sources of truth.
- Never commit Azure credentials, PostgreSQL passwords, JWT secrets, or deployment parameter files containing real secrets.
- Local Docker Compose remains the fallback/source of truth if cloud credits expire.
- Azure verification is pending and must not be claimed as complete.


## Phase 13 Update
- Final demo preparation implemented with synthetic defensive data and a repeatable demo runbook.
- Use `DEMO_RUNBOOK.md` and `scripts/demo-seed.ps1`.

## Phase 14 Update
- College submission package implemented.
- Report guide: `FINAL_REPORT_GUIDE.md`
- Presentation outline: `PPT_OUTLINE.md`
- Viva preparation: `VIVA_QA.md`
- Traceability: `REQUIREMENTS_TRACEABILITY.md`
- Final results template: `RESULTS_TEMPLATE.md`
- Submission checklist: `FINAL_SUBMISSION_CHECKLIST.md`
- Do not fabricate final results. The next step is whole-project verification and evidence collection.


## PRE-FINAL AUDIT CORRECTIONS
An independent static audit identified compile/configuration/demo issues. Corrective implementation is recorded in `PRE_FINAL_AUDIT_FIXES.md`. Key corrections: SecurityConfig auth boundaries, ReferrerPolicy import, risk repository finder, IncidentSeverity import, H2 test profile, RiskScoreServiceTest mock, frontend public directory, AI HTTP timeouts, ingestion transaction boundary, deterministic risk floor, safer secret configuration, incident lifecycle, and demo rule triggers. Do not claim final verification yet.
