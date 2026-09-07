# SentinelAI — Azure Deployment

## Purpose

Phase 12 prepares SentinelAI for Azure without requiring the developer to provision cloud resources during implementation. The deployment is intentionally staged and uses Infrastructure as Code.

## Azure architecture

- **Azure Container Apps**: frontend, Spring Boot backend, and FastAPI AI service.
- **Azure Database for PostgreSQL Flexible Server**: managed production database.
- **Azure Container Registry (Basic)**: private container image registry.
- **Log Analytics**: Container Apps environment logs.
- **Managed identities**: Container Apps pull private images from ACR without registry passwords.

Azure Container Apps supports managed identities for private ACR image pulls, avoiding long-lived registry credentials. See Microsoft Learn for the current managed-identity guidance. urlAzure Container Apps managed identitieshttps://learn.microsoft.com/en-us/azure/container-apps/managed-identity

## Cost-conscious decisions

- PostgreSQL uses the Burstable `Standard_B1ms` SKU, 32 GB storage, disabled geo-redundant backup and disabled high availability for the student/demo environment.
- Container Apps use small CPU/memory allocations and low replica limits.
- No Kubernetes, Redis, Kafka, Application Gateway, Front Door, or unnecessary networking layer is introduced.
- Local Docker Compose remains the source of truth if Azure credits expire.

**Azure pricing changes over time. Check the Azure pricing calculator and your subscription's current quotas/credits before provisioning.**

## Important: no automatic deployment

Phase 12 creates the deployment assets only. Do not provision Azure resources until the final local verification has passed or the user explicitly approves cloud deployment.

## Files

- `infrastructure/azure/main.bicep` — Azure infrastructure template.
- `infrastructure/azure/parameters.example.json` — parameter example; contains placeholders only.
- `scripts/azure-deploy.ps1` — staged deployment helper; reads secrets from environment variables.

## Deployment sequence

1. Install/update Azure CLI and Bicep.
2. Sign in with `az login`.
3. Select the intended subscription.
4. Create and push backend + AI images to ACR.
5. Set `SENTINEL_POSTGRES_PASSWORD` and `SENTINEL_JWT_SECRET` only in the current shell/secret store.
6. Run `scripts/azure-validate.ps1` first; this performs local Bicep validation and does not provision resources.
7. Run `scripts/azure-deploy.ps1`. The script creates/reuses the Basic ACR, builds/pushes backend and AI images, deploys the backend/AI/database, obtains the backend URL, builds/pushes the frontend with the correct `NEXT_PUBLIC_API_URL`, deploys the frontend, and applies the exact frontend origin to backend CORS.
8. Run final smoke/security checks before calling the cloud deployment verified.

Microsoft's Container Apps documentation confirms `az containerapp up` can create the environment and deploy containerized applications; this project instead uses Bicep so the infrastructure is reproducible and reviewable. urlAzure Container Apps quickstarthttps://learn.microsoft.com/en-us/azure/container-apps/get-started

## Secret handling

Never commit:

- PostgreSQL passwords
- JWT secrets
- Azure access tokens
- Service principal secrets
- Registry credentials
- `.env` files

The deployment script expects `SENTINEL_POSTGRES_PASSWORD` and `SENTINEL_JWT_SECRET` to exist only in the current shell/secret store.

For CI/CD later, prefer GitHub Actions OIDC with Azure rather than storing a long-lived Azure client secret. Do not add OIDC credentials until the deployment workflow is explicitly enabled.

## PostgreSQL networking note

The template enables PostgreSQL public network access and adds the Azure-services firewall rule (`0.0.0.0` to `0.0.0.0`). This is intended for a low-cost student deployment. For a production deployment, move PostgreSQL behind a private network/private endpoint and tighten access.

## Frontend/backend URL dependency

The browser calls the backend directly, so the Next.js image must be built with the externally reachable backend API URL. Because the backend URL is only known after the Container App exists, deployment is staged:

`Infrastructure + backend/AI → obtain backend URL → build frontend → deploy frontend → update backend CORS`

## Rollback / cleanup

If Azure resources are no longer needed, remove the dedicated resource group. Verify that it contains only SentinelAI resources first.

Do not delete shared resource groups.

## Verification status

Phase 12 implementation is complete, but Azure deployment is **not verified**. Final verification remains pending by project policy.
