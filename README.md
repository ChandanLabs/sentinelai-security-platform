# SentinelAI

**Authenticated application-activity monitoring demo**

SentinelAI records actions performed inside its sample web workspace, evaluates deterministic server-side rules, and displays account-scoped events, alerts, and incidents. It does not monitor a user's computer, PowerShell, or other websites, and it is not a production SIEM.

## Architecture and data flow

- **Frontend:** Next.js dashboard with a same-origin API proxy.
- **Backend:** Spring Boot API with account authentication, session/CSRF checks, workspace actions, PostgreSQL persistence, and deterministic detection.
- **AI service:** FastAPI health endpoint only. Inference is **not implemented**; events report `aiAnalysisStatus: NOT_CONFIGURED`.
- **Storage:** PostgreSQL, with account-owned documents and security records.

```text
Browser -- authenticated workspace actions --> Next.js
Next.js -- same-origin proxy (session cookie + CSRF) --> Spring Boot
Spring Boot --> PostgreSQL (accounts, documents, events, alerts, incidents)
Spring Boot --> AI service health check only
```

## Run locally

Prerequisites: Docker Desktop and Docker Compose v2. Keep an existing `.env`; otherwise copy `.env.example` and set a strong, private database password.

```powershell
if (-not (Test-Path -LiteralPath ".env")) { Copy-Item .env.example .env }
# Edit .env and replace DB_PASSWORD.
docker compose up -d --build
docker compose ps
```

Open:

- Dashboard: <http://localhost:3000>
- Backend health: <http://localhost:8080/api/v1/health>
- AI health: <http://localhost:8000/health>

`docker compose down` stops services but preserves the database volume. `docker compose down -v` deletes the entire local database; do not use it as a routine demo reset.

## Try real workspace activity

Create a demo account, open a sample document, use the controlled denied-action buttons, export the document list, and inspect **Events**, **Alerts**, and **Incidents**. For a rule walkthrough with actual UI actions, see [09_DEMO_SCRIPT.md](./09_DEMO_SCRIPT.md).

Current server-side rules:

| Rule | Trigger |
|---|---|
| `AUTH_BRUTE_FORCE` | Five failed sign-in attempts against the same known account in five minutes |
| `FAILED_THEN_SUCCESS` | A successful sign-in after one or more failed attempts against that account within five minutes |
| `REPEATED_DENIED_ACCESS` | Three denied resource requests by an account in five minutes |
| `PRIVILEGED_ACCESS_DENIED` | A standard account requests the restricted administrative operation |
| `REPEATED_DATA_EXPORT` | Three document-list exports by an account in ten minutes |

These are deterministic rules, not AI findings. Risk scores are illustrative severity mappings, not probabilities. Source IP is intentionally not collected.

## Public demo deployment

See [10_DEPLOYMENT.md](./10_DEPLOYMENT.md) for local and Render instructions. A public deployment allows visitors to create demo accounts; use synthetic usernames and content only. The app is not production-ready and should not receive real credentials, private data, or security telemetry.

## Repository layout

- `frontend/` — dashboard and same-origin runtime API proxy
- `backend/` — Spring Boot API, authentication, workspace, rules, persistence, and tests
- `ai-service/` — FastAPI health-only service
- `docker-compose.yml` — local PostgreSQL and application services
- `render.yaml` — Render Blueprint
- `09_DEMO_SCRIPT.md` — real workspace-action walkthrough
- `10_DEPLOYMENT.md` — local and hosted deployment instructions

## Scope and limitations

Implemented: account registration/login/logout/deletion, password hashing, server-side sessions and CSRF protection, owner-scoped sample documents and activity history, deterministic detection, persisted events/alerts/incidents, incident lifecycle transitions, and 30-day event/alert/incident retention.

Not implemented: operating-system or third-party application monitoring, AI inference, verified email/password recovery/MFA, roles or administrative account management, distributed sessions or rate limits, signup-abuse protection, production migrations/backups, or a production security threat model. Sessions and login throttling are in-memory and require a single backend instance.
