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
CURRENT PHASE: PHASE 1 — FOUNDATION

**Completed Work:**
- Phase 1 implementation: COMPLETE
- Phase 1 verification: PENDING

**Known Limitations:**
- N/A at Phase 1

**Important Decisions:**
- See `08_DECISIONS.md`

# INSTRUCTIONS TO FUTURE AI AGENTS
- Strictly follow phase boundaries. Do not implement features outside the current phase.
- Do not blindly overwrite files. Inspect, understand, check for existing functionality, and make the smallest appropriate change.
- Never regenerate the entire project unnecessarily.
- Never change the locked stack.
- Never say "works" without running relevant verifications.
