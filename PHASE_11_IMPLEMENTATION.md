# Phase 11 — Docker + CI/CD Implementation

## Objective
Make SentinelAI reproducible locally and continuously validated in GitHub Actions without changing the application architecture.

## Implemented
- Docker Compose orchestration for PostgreSQL, AI service, backend, and frontend.
- Health checks and dependency ordering.
- Backend multi-stage Java 21/Maven image.
- Frontend Next.js standalone production image.
- Frontend API URL build argument.
- AI service production image with persistent model volume.
- Service-specific `.dockerignore` files.
- Local environment template in `.env.example`.
- GitHub Actions CI for backend, AI service, frontend, Compose validation, and Docker builds.

## Verification policy
No claim of end-to-end runtime verification is made in this phase. Final verification will be performed after all implementation phases are complete.

## Next
Phase 12 — Azure deployment, followed by final verification and demo/documentation phases.
