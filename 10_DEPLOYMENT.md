# Deployment Strategy

## Local Development
- **Orchestration:** Docker Compose
- **Purpose:** Ensure the project remains functional locally even if cloud credits expire.

## Cloud Deployment (Planned)
- **Primary:** Microsoft Azure
- **Fallback:** AWS

## Rules
- Do NOT provision cloud infrastructure during Phase 1.
- All services must be deployable via Docker and decoupled from specific cloud vendor lock-ins where possible.

## Phase 12 Azure implementation
Azure deployment assets now exist under `infrastructure/azure/` and `scripts/azure-deploy.ps1`. The target is Azure Container Apps + PostgreSQL Flexible Server + ACR + Log Analytics. Cloud provisioning remains intentionally pending until final verification or explicit approval.


## Pre-Final Audit Update
The pre-final independent audit has been addressed. Corrective changes are tracked in `PRE_FINAL_AUDIT_FIXES.md`. Full runtime verification remains pending and must be performed before claiming production/demo readiness.
