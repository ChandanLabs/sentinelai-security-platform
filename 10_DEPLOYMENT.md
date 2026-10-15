# Deployment

SentinelAI is an authenticated application-activity demo, not a production security-monitoring system. It records activity inside its own sample workspace only; it does not monitor computers or third-party applications. Public deployments allow visitors to create accounts, so use synthetic content and unique demo passwords only.

## Local Docker Compose

### Prerequisites

- Docker Desktop with Docker Compose v2, running
- At least 4 GB of RAM available to Docker

### Configure and start

1. If you do not already have a local `.env`, copy the example:

   ```powershell
   Copy-Item .env.example .env
   ```

   Keep an existing `.env`; do not replace it. Set a strong `DB_PASSWORD` and never commit or share `.env`.
2. Build and start the services:

   ```powershell
   docker compose up -d --build
   docker compose ps
   ```
3. Open the application at <http://localhost:3000> and register a throwaway demo account.
4. Check backend and AI health:

   ```powershell
   Invoke-RestMethod http://localhost:8080/api/v1/health
   Invoke-RestMethod http://localhost:8000/health
   ```

| Service | Local address |
|---|---|
| Frontend | <http://localhost:3000> |
| Backend | <http://localhost:8080> |
| Backend health | <http://localhost:8080/api/v1/health> |
| AI health | <http://localhost:8000/health> |
| PostgreSQL | `localhost:5432`, database `sentinel` |

The browser uses the same-origin Next.js API proxy; Compose sets its backend target to `http://backend:8080`. PostgreSQL records persist in the Compose volume. `docker compose down` preserves that volume. Do **not** use `docker compose down -v` unless you intend to delete the entire local database.

## Public demo on Render

The repository includes a Render Blueprint in [render.yaml](./render.yaml). It provisions managed PostgreSQL and Docker web services for the AI health endpoint, backend API, and frontend. The AI service is health-only; it provides no inference.

1. Push the repository, including `render.yaml`, to GitHub.
2. In Render, choose **New → Blueprint**, connect the repository, and create the Blueprint.
3. Provide the `AI_SERVICE_URL` and `BACKEND_URL` values marked `sync: false` in the Blueprint. Use the actual service URLs Render assigns; if using private service URLs, verify the services can reach one another.
4. Set `SESSION_COOKIE_SECURE=true` for the backend. The Blueprint already enables this for its HTTPS deployment.
5. Wait for the database and all three services to become available. Verify:
   - Backend: `https://<backend-host>/api/v1/health`
   - AI service: `https://<ai-host>/health`
   - Frontend: `https://<frontend-host>`
6. Register a test account through the frontend and perform the walkthrough in [09_DEMO_SCRIPT.md](./09_DEMO_SCRIPT.md).

The Blueprint uses managed PostgreSQL rather than a web-service filesystem. Render free services have sleep/availability and resource limits; its free Postgres is temporary and has capacity/expiration limits. Check Render's current [free-instance limitations](https://render.com/docs/free) before deploying. Use a durable database plan if persistent hosted history is required; do not approve a paid upgrade unless you intend to.

## Environment variables

| Variable | Used by | Purpose |
|---|---|---|
| `DB_HOST`, `DB_PORT`, `DB_NAME` | Backend | PostgreSQL connection; supplied by Compose or linked Render database |
| `DB_USER`, `DB_PASSWORD` | Backend | Local database credentials; Compose requires a private password |
| `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` | Backend | Optional direct Spring datasource configuration; Compose sets these |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Backend | Schema behavior; defaults to `update` for this demo |
| `AI_SERVICE_URL` | Backend | AI service health URL; inference is not configured |
| `SESSION_COOKIE_SECURE` | Backend | Set `true` for HTTPS; Compose sets `false` for local HTTP |
| `SENTINEL_DATA_RETENTION_DAYS` | Backend | Event/alert/incident retention in days; defaults to 30 |
| `PORT` | Backend and AI service | Platform-assigned listener port |
| `BACKEND_URL` | Frontend server | Runtime API proxy target; never a `NEXT_PUBLIC_` variable |

There is no shared demo ingestion key or public synthetic event-write endpoint. Users sign up and workspace events are created by backend-handled actions.

## Persistence, safety, and known limits

- PostgreSQL is the only application store. Local data remains in the Compose volume; hosted durability depends on the selected Render database plan.
- The backend deletes events and linked alerts/incidents older than the configured retention period; accounts and documents remain until the user deletes the account.
- Passwords are BCrypt-hashed. Sessions and login throttling are process-local/in-memory; run a single backend instance for this demo. Restarts invalidate sessions and clear throttle counters.
- Signup has no abuse rate limit, and there is no email verification, MFA, or password recovery. Do not reuse passwords.
- Public service URLs allow anyone to create accounts; do not enter real credentials, private information, or actual company telemetry.
- Deterministic rules and risk scores are illustrative. The AI service currently implements only `GET /health`; inference is not run.
- No production migration, backup/restore, distributed-session, or full security-review setup is included. This demo is not suitable for protecting systems or responding to real incidents.
