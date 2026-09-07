# Architecture Decision Records

## ADR-001: Spring Boot as Core Backend
- **Decision:** Spring Boot is the core backend.
- **Reason:** Strong Java backend ecosystem, security support, maintainability, and alignment with the student's backend development objectives.

## ADR-002: PostgreSQL as Primary Database
- **Decision:** PostgreSQL is the primary database.
- **Reason:** The domain contains structured entities and relationships.

## ADR-003: FastAPI for AI Workloads
- **Decision:** Python FastAPI is separated from the Java backend for machine-learning workloads.
- **Reason:** Clear separation between application/business logic and ML processing.

## ADR-004: Docker Compose for Local Development
- **Decision:** Docker Compose is used for local multi-service development.
- **Reason:** Consistent local environments and easier deployment transition.

## ADR-005: Azure as Primary Cloud Target
- **Decision:** Azure is the primary cloud target with AWS as fallback.
- **Reason:** Cloud deployment should remain portable.

## ADR-005 — Deterministic rule detection before ML
- **Decision:** Run a small set of explainable rules immediately after event persistence and store rule matches as `RuleDetection` records.
- **Reason:** This creates an auditable baseline for security detection before introducing ML. Each detection has a rule code, severity, and human-readable reason.
- **Boundary:** Rules do not create alerts or incidents directly; later phases consume detections for risk scoring and alerting.

## ADR — Azure deployment topology
- Decision: Use Azure Container Apps for the three application services and PostgreSQL Flexible Server for the database.
- Reason: preserves the Docker-based architecture while avoiding Kubernetes complexity and keeping student/demo resource sizes small.
- Decision: use managed identity for ACR pulls rather than registry passwords.
- Decision: stage deployment because the frontend browser configuration requires the externally deployed backend URL.
- Status: accepted for Phase 12; cloud provisioning pending.
