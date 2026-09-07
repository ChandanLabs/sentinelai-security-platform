# Phase 12 — Azure Deployment Implementation

## Objective
Prepare SentinelAI for reproducible, cost-conscious Azure deployment without provisioning cloud resources during implementation.

## Implemented
- Azure Container Apps environment definition.
- Azure Container Registry (Basic) definition.
- Azure Database for PostgreSQL Flexible Server definition.
- Log Analytics workspace.
- System-assigned managed identities for container apps.
- ACR pull role assignments using managed identity.
- Backend, AI-service, and frontend Container App definitions.
- Small resource/replica limits for student/demo usage.
- Staged deployment script because the browser needs the deployed backend URL at Next.js build time.
- Secret-safe deployment parameter model using secure Bicep parameters/environment variables.
- Azure deployment guide and cost/security notes.

## Deliberate non-goals
- No Azure resources are provisioned by this phase.
- No Kubernetes.
- No Redis/Kafka.
- No public registry credentials.
- No production private-network topology yet.
- No GitHub OIDC deployment workflow until final verification and explicit approval.

## Verification policy
The Bicep template and scripts are implementation assets. Azure provisioning and end-to-end cloud verification remain pending until the project reaches the final verification stage.

## Next
Phase 13 — Final Demo Preparation, followed by Phase 14 — College Documentation, PPT and Viva preparation. Final verification will be performed before the project is declared complete.
