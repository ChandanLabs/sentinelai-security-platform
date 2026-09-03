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
