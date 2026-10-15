# SentinelAI

**Interview demo for security-event ingestion and rule-based alerting**

SentinelAI is a small, synthetic-data-only demonstration of a security monitoring workflow. It is not an operational SIEM or a production security product.

## Architecture and data flow

- **Frontend:** Next.js dashboard with a same-origin API proxy.
- **Backend:** Spring Boot API validates and timestamps demo events, persists them in PostgreSQL, evaluates deterministic rules, and creates linked alerts/incidents.
- **AI service:** FastAPI health endpoint. Inference is **not implemented**; events report `aiAnalysisStatus: NOT_CONFIGURED`.
- **Storage:** PostgreSQL only. No file-backed mode is implemented.

```text
PowerShell demo client -- write key --> Spring Boot API
                                          |-- PostgreSQL (events, alerts, incidents)
                                          |-- AI service health check only
Browser --> Next.js frontend --> server-side API proxy --> Spring Boot API
```

The dashboard is public and read-only. The event-ingest API, incident status updates, and synthetic-data reset require `DEMO_INGESTION_KEY`. Do not send real credentials, company data, or actual telemetry.

## Run locally

Prerequisites: running Docker Desktop and Docker Compose v2. Preserve an existing `.env`; otherwise copy the template and set a strong database password and random write key.

```powershell
if (-not (Test-Path -LiteralPath ".env")) { Copy-Item .env.example .env }
# Edit .env and replace the DB_PASSWORD and DEMO_INGESTION_KEY placeholders.
docker compose up -d --build
docker compose ps
```

Open:

- Dashboard: <http://localhost:3000>
- Backend health: <http://localhost:8080/api/v1/health>
- AI health: <http://localhost:8000/health>

`docker compose down` stops the services but keeps the database volume. `docker compose down -v` deletes the entire local database; do not use it as a routine demo reset.

## Run the live scenario

The copy-paste-ready PowerShell flow, response checks, API examples, incident lifecycle actions, and safe synthetic-data reset are in [09_DEMO_SCRIPT.md](./09_DEMO_SCRIPT.md). It generates events marked `SIMULATED DEMO EVENT`, verifies their server IDs/timestamps/rule outcomes, and uses the protected API. The dashboard polls the backend so persisted events appear without refreshing the page.

The Events page also retains a separate browser-only synthetic stream for visual demonstration. Those localStorage rows are clearly labelled and are not backend events, are not analyzed, and are not persisted in PostgreSQL.

## Public deployment

See [10_DEPLOYMENT.md](./10_DEPLOYMENT.md) for the Render Blueprint steps, required environment variables, public access model, storage behavior, free-tier caveats, and limitations. The Render free Postgres database has an expiration period and is not permanent storage.

## Repository layout

- `frontend/` — dashboard and runtime backend proxy
- `backend/` — Spring Boot API, PostgreSQL entities/repositories, rules, and tests
- `ai-service/` — FastAPI health-only service
- `docker-compose.yml` — local PostgreSQL and three application services
- `render.yaml` — Render Blueprint
- `09_DEMO_SCRIPT.md` — repeatable demo workflow
- `10_DEPLOYMENT.md` — local and hosted deployment instructions

## Current scope

Implemented: synthetic event ingestion and validation, server-side event IDs/timestamps, PostgreSQL persistence, three deterministic event rules, linked alerts/incidents, read APIs, event details, dashboard metrics, polling, protected incident lifecycle updates, and a reset limited to marked synthetic events.

Not implemented: user accounts, authentication/JWT/RBAC, rate limiting, audit logs, a real telemetry collector, AI inference, production migrations, multi-tenant data isolation, or a file-based storage option. Use synthetic information only and treat every public read API as publicly accessible.
