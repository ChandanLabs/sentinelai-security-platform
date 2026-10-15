# Deployment

SentinelAI is currently an interview/demo application, not a production security-monitoring system. Its public dashboard is intentionally read-only. Event ingestion, incident lifecycle changes, and synthetic-data reset require a server-side `DEMO_INGESTION_KEY`. Only synthetic data should be submitted.

## Local Docker Compose

### Prerequisites

- Docker Desktop with Docker Compose v2, running
- At least 4 GB of RAM available to Docker

### Configure and start

1. If you do not already have a local `.env`, copy the example:

   ```powershell
   Copy-Item .env.example .env
   ```

   Keep an existing `.env`; do not replace it. Set a strong `DB_PASSWORD` and a randomly generated, private `DEMO_INGESTION_KEY` in it. Never commit or share `.env`.
2. Build and start the services:

   ```powershell
   docker compose up -d --build
   docker compose ps
   ```
3. Open the public-style local demo at <http://localhost:3000>.
4. Check the APIs:

   ```powershell
   Invoke-RestMethod http://localhost:8080/api/v1/health
   Invoke-RestMethod http://localhost:8000/health
   ```

The dashboard calls a same-origin Next.js API proxy. The proxy uses `BACKEND_URL` on the server side; it does not bake a cloud API URL into browser JavaScript. Compose sets it to `http://backend:8080`.

| Service | Local address |
|---|---|
| Frontend | <http://localhost:3000> |
| Backend | <http://localhost:8080> |
| Backend health | <http://localhost:8080/api/v1/health> |
| AI service health | <http://localhost:8000/health> |
| PostgreSQL | `localhost:5432`, database `sentinel` |

Events, alerts, and incidents are stored in the Compose PostgreSQL volume. `docker compose down` leaves that volume intact. Do **not** use `docker compose down -v` unless you intend to delete the entire local database.

## Public demo on Render

The repository includes a Render Blueprint in [render.yaml](./render.yaml). It creates PostgreSQL plus Docker web services for the AI health endpoint, backend API, and frontend. The backend and AI service are web services on the free plan, so their health endpoints have public URLs; the AI service currently exposes health only and does not provide inference.

1. Push the repository and `render.yaml` to GitHub.
2. In Render, choose **New → Blueprint**, connect the GitHub repository, and create the Blueprint.
3. During initial setup, provide the non-secret URLs:
   - `AI_SERVICE_URL`: the public HTTPS URL Render assigns to `sentinel-ai`.
   - `BACKEND_URL`: the public HTTPS URL Render assigns to `sentinel-backend`.

   The usual URLs are `https://sentinel-ai.onrender.com` and `https://sentinel-backend.onrender.com`; use the actual URLs shown in Render if those names are unavailable.
4. Render generates `DEMO_INGESTION_KEY` as a secret using Blueprint `generateValue`. Keep it only in the backend's Environment settings and in the secure local shell where you run the demo script. Do not put it in a frontend variable, source file, screenshot, or Git commit.
5. Wait for three Docker builds and all four resources to become available. Verify:
   - Backend: `https://<backend-host>/api/v1/health`
   - AI service: `https://<ai-host>/health`
   - Frontend: `https://<frontend-host>`

   Backend health reports whether the AI service health endpoint responded, and separately reports `aiInference: NOT_CONFIGURED`. It does not claim that AI analysis ran.
6. If the backend or AI URL changes, update the corresponding backend `AI_SERVICE_URL` or frontend `BACKEND_URL` environment variable in Render and redeploy that service.
7. Open the frontend URL and share it. The dashboard and its read APIs are public; do not put real or private data in this demo.

The Render blueprint uses managed PostgreSQL rather than a web-service filesystem. Render free web-service filesystems are ephemeral. Render currently documents that free Postgres databases have a 1 GB limit, expire 30 days after creation, and are deleted after the additional 14-day grace period unless upgraded. Free web services also sleep after 15 minutes without inbound traffic and can take about a minute to wake. Check Render's current [free-instance limitations](https://render.com/docs/free) before deploying; free instances are not recommended for production. No paid service is provisioned by this project. Long-term hosted history requires a durable database plan; approve any paid upgrade yourself.

## Environment variables

| Variable | Used by | Required | Purpose |
|---|---|---:|---|
| `DB_HOST`, `DB_PORT`, `DB_NAME` | Backend | Yes | PostgreSQL connection |
| `DB_USER`, `DB_PASSWORD` | Backend | Yes | PostgreSQL credentials; no application default password |
| `DEMO_INGESTION_KEY` | Backend | Yes for writes | Shared key required for ingestion, incident status changes, and synthetic-data reset |
| `AI_SERVICE_URL` | Backend | No | AI service health URL; defaults to `http://ai-service:8000` for Compose |
| `PORT` | Backend and AI service | No | Platform-assigned listener port |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Backend | No | Defaults to `update` for this demo |
| `BACKEND_URL` | Frontend server | Yes | Backend URL for the runtime Next.js API proxy; never a `NEXT_PUBLIC_` variable |

The previous example JWT settings are not used: authentication and RBAC do not exist in this project yet. The public dashboard is not an authenticated application. Its write key is a shared demo control, not a replacement for user authentication, authorization, or rate limiting. Do not use this deployment for real security telemetry or private data.

## Persistence and reset

- PostgreSQL is the only implemented application store. File-backed/JSONL storage is not enabled: this backend started with no event repository or persistence abstraction, and adding a second storage implementation would not be a safe configuration-only change.
- Local history is retained in the Compose database volume. Render uses managed Postgres, but its free database expires as described above.
- Ingested records are accepted only with the `SIMULATED DEMO EVENT` message prefix. The protected reset endpoint removes only those marked events and the alerts/incidents linked to them:

  ```powershell
  Invoke-RestMethod -Method Post `
    -Uri "https://<backend-host>/api/v1/events/demo-data/reset" `
    -Headers @{ "X-Demo-Ingestion-Key" = $env:SENTINEL_DEMO_WRITE_KEY }
  ```

  A reset cannot be undone. Do not run it if you want to keep the synthetic history. Browser-only stream items are separate localStorage demo data and can be cleared from the Events page; they are never sent to the backend.

## Implemented behavior and known limitations

- Events receive a UUID and server timestamp and are committed with their generated alerts/incidents in PostgreSQL.
- Implemented deterministic rules: five `login_failed` events from one source IP in five minutes, `privilege_escalation`, and `suspicious_command`.
- The AI service currently implements only `GET /health`. Event responses say `aiAnalysisStatus: NOT_CONFIGURED`; AI inference is **not** run.
- Public dashboard data is refreshed by polling. It displays stored backend records separately from the browser-only simulated stream.
- Incident status advances one step at a time through the protected API; the public UI is read-only.
- There is no full account system, JWT/RBAC, rate limiting, audit log, migrations, or real telemetry collector. Keep the hosted demo single-instance and synthetic-only.
