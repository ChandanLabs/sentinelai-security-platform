# CrossGuard

**Cross-Application Security Monitoring and Threat Detection Platform** — interview/demo build.

CrossGuard is an interactive security simulation with two modes. **Demo Mode** records actions in a short-lived, server-side in-memory session. **Actual Mode** represents a signed-in account in the CrossGuard app: events and incidents are stored in PostgreSQL. It only observes actions made inside this app; it does not monitor arbitrary websites, browser tabs, or a user's computer.

## Features

- Demo and account-backed modes using the same action workflow.
- Register/login with BCrypt password hashing and signed JWTs.
- Banking-style workspace: open a sample statement, export a simulated report, try a restricted resource, try a restricted operation, try the secure area with a verification code, and simulate an API-tampering attempt.
- Server-side access checks; denied requests are logged even though the resource remains inaccessible.
- Detection rules: denied actions get HIGH severity; repeated denials within a session/account can rise to CRITICAL; event explanations are shown.
- Separate dashboard page with event history, filters, severity summary, small charts, event details and incident status management.
- Demo session data is intentionally non-persistent. It survives SPA navigation to the dashboard, but a full browser refresh starts a new session. The server expires abandoned sessions after one hour.
- Signed-in account events and incidents persist in PostgreSQL.
- Docker Compose for local launch; Dockerfiles for frontend/backend deployment.

## Run locally (recommended)

Requirements: Docker Desktop with Compose, or Java 21 + Maven 3.9+ and Node.js 20+.

```bash
git clone <your-repository-url>
cd crossguard
docker compose up --build
```

Open **http://localhost:3001** (the frontend host port is set to 3001 to avoid conflicts with other local apps). API health: **http://localhost:8080/api/health**.

Default demo verification code: `CG-DEMO-7429`. It is intentionally a demo-only credential; change `DEMO_ACCESS_CODE` for a deployed demo. Never use this design or credential for real banking authorization.

To stop:
```bash
docker compose down
```
To remove the local database too:
```bash
docker compose down -v
```


### Local troubleshooting

- If another app already uses port 3000, this compose file exposes CrossGuard at **http://localhost:3001**. Do not open port 3000 for this version.
- If Docker reports a tiny build context (for example, only a few hundred bytes) or serves an old UI, check that `frontend/.dockerignore` does not exclude `app/`, `package.json`, or source files. The included context-specific ignore files exclude only dependencies, build output, environment files, and Git metadata.
- Rebuild after code/config changes with `docker compose down` then `docker compose up --build`. To preserve database data, do **not** add `-v` to `docker compose down`.
- Confirm the API is reachable at `http://localhost:8080/api/health`; the frontend status indicator should say API connected.

## Run without Docker

Terminal 1 — PostgreSQL must be running and have a database/user matching the values below:
```bash
cd backend
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/crossguard
export SPRING_DATASOURCE_USERNAME=crossguard
export SPRING_DATASOURCE_PASSWORD=crossguard_dev_password
export JWT_SECRET='replace-this-with-a-long-random-secret-before-sharing'
mvn spring-boot:run
```

Terminal 2:
```bash
cd frontend
npm install
cp .env.local.example .env.local
npm run dev
```

## Deploy for a demo

One straightforward split deployment is **Vercel (Next.js)** + **Render (Spring Boot Docker service)** + **Neon (PostgreSQL)**. Create the Neon database first, deploy the backend Dockerfile to Render, then deploy `frontend/` to Vercel and set `NEXT_PUBLIC_API_URL` to the backend URL. Set these backend environment variables in Render:

- `SPRING_DATASOURCE_URL` = JDBC connection URL from Neon, e.g. `jdbc:postgresql://<host>/<database>?sslmode=require` (use your own values).
- `SPRING_DATASOURCE_USERNAME` and `SPRING_DATASOURCE_PASSWORD` = Neon credentials.
- `JWT_SECRET` = a long random secret (at least 32 bytes).
- `DEMO_ACCESS_CODE` = a private demo code you choose.
- `CORS_ALLOWED_ORIGINS` = your exact Vercel origin, e.g. `https://your-app.vercel.app`.
- `PORT` = Render supplies this automatically.

Set `NEXT_PUBLIC_API_URL` on Vercel to `https://<your-backend>.onrender.com`, then redeploy. `NEXT_PUBLIC_API_URL` is compiled into the Next.js client bundle, so it must be set before the Vercel build. The frontend must call the public backend URL, not an internal Render URL.

Free-tier limitations can change. Render free web services may spin down after inactivity, and Render free PostgreSQL expires after 30 days; Neon currently lists a free plan with limited compute/storage. See current platform docs before choosing: [Render free services](https://render.com/docs/free), [Neon plans](https://neon.com/pricing), [Vercel plans](https://vercel.com/docs/plans). Do not treat free-tier storage as an indefinite backup.

## Interview demo script (2–3 minutes)

1. Open the deployed page in Demo Mode.
2. Open the sample statement — expect ALLOWED / LOW.
3. Attempt restricted resource access twice, then try a restricted operation again — expect DENIED and escalation to CRITICAL after repeated denials.
4. Try secure area with an incorrect code, then with the demo code — show server-side verification.
5. Open **Investigation Dashboard**; filter HIGH/CRITICAL; select an event; show the explanation and incident statuses.
6. Switch to **Actual Mode**, create an account, perform actions, refresh/relogin, and show that this account's events persist.
7. Explain that the frontend and backend validate access separately, rule-based detection is currently implemented, and ML anomaly detection / browser-wide monitoring are future work.

## Quick server-side security check

After the backend is running, request an account-only endpoint without a bearer token. It should return `401 Unauthorized`:

```bash
curl -i http://localhost:8080/api/secure/events
```

This is a safe demonstration of the server enforcing authentication. The **Simulate direct API request** button creates a logged denial event through the demo action route.

## API endpoints

- `GET /api/health`
- `POST /api/auth/register` / `POST /api/auth/login` / `GET /api/auth/me`
- `GET /api/demo/{sessionId}/events` / `POST /api/demo/{sessionId}/actions` / `POST /api/demo/{sessionId}/reset`
- `GET /api/secure/events` / `POST /api/secure/actions`
- `GET /api/secure/incidents` / `PATCH /api/secure/incidents/{id}`

## Security and scope notes

- This is an interview prototype, not a real financial application.
- It records only actions that CrossGuard itself handles. Browser devtools can alter a UI, but cannot make a protected server endpoint grant access without valid server-side authorization.
- Demo session IDs are unguessable UUIDs but do not represent authentication. Demo state is not durable or private enough for sensitive data.
- JWT is kept in browser localStorage for this prototype's simple split-origin setup. Production systems should prefer HttpOnly secure cookies or a carefully designed OAuth flow, rate limiting, CSRF/XSS defenses, secret rotation, database migrations, and privacy/retention controls.
- The demo access code is a step-up-verification simulation, not MFA. Never reuse it for a production system.
