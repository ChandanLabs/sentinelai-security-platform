# Deployment

## Local Development (Docker Compose)

All four services run locally via Docker Compose.

### Prerequisites
- Docker Desktop (v28+) with Docker Compose v2
- 4 GB RAM available to Docker

### Steps

```powershell
# 1. Copy the env template and fill in your own values
cp .env.example .env

# 2. Start all services (postgres, ai-service, backend, frontend)
docker compose up -d

# 3. Verify all containers are healthy
docker compose ps

# 4. Check backend health
Invoke-RestMethod http://localhost:8080/api/v1/health

# 5. Check AI service health
Invoke-RestMethod http://localhost:8000/health

# 6. Open the dashboard
Start-Process "http://localhost:3000"
```

### Service ports

| Service     | Port  | URL                              |
|-------------|-------|----------------------------------|
| Frontend    | 3000  | http://localhost:3000            |
| Backend API | 8080  | http://localhost:8080            |
| AI Service  | 8000  | http://localhost:8000            |
| PostgreSQL  | 5432  | localhost:5432 (DB: sentinel)    |

---

## Cloud Deployment — Render.com

SentinelAI deploys to [Render.com](https://render.com) using the `render.yaml` Blueprint in the root of this repository.
The Blueprint provisions all four services automatically.

### Required variables (set manually in Render dashboard)

| Variable            | Service           | Notes                                         |
|---------------------|-------------------|-----------------------------------------------|
| `JWT_SECRET`        | sentinel-backend  | Run `openssl rand -hex 64` to generate        |
| `NEXT_PUBLIC_API_URL` | sentinel-frontend | Set to the public URL of sentinel-backend after first deploy |

All other variables are either auto-injected from the linked database or have defaults in `render.yaml`.

### Deploy steps

1. **Push to GitHub** — ensure `render.yaml` is in the repo root.
2. **New Blueprint** — in the Render dashboard click *New → Blueprint*, select your repo.
3. Render reads `render.yaml` and queues four builds:
   - `sentinel-db` (managed Postgres)
   - `sentinel-ai` (FastAPI, Docker)
   - `sentinel-backend` (Spring Boot, Docker)
   - `sentinel-frontend` (Next.js, Docker)
4. **Set secrets** — go to each service → *Environment* and fill in the two secret variables above.
5. **Wait for builds** — first build takes ~5–10 minutes per service (free tier).
6. **Get backend URL** — copy `sentinel-backend.onrender.com` and set it as `NEXT_PUBLIC_API_URL` on `sentinel-frontend`, then redeploy the frontend.
7. Open `sentinel-frontend.onrender.com` in the browser.

### Free tier limitations (Render)

- Services spin down after 15 minutes of inactivity (first request is slow to wake).
- Free Postgres DB is deleted after 90 days — upgrade to Starter ($7/mo) for persistence.
- 512 MB RAM per service — sufficient for this project.

---

## Environment Variables Reference

| Variable                              | Required | Default       | Description                                  |
|---------------------------------------|----------|---------------|----------------------------------------------|
| `DB_NAME`                             | Yes      | sentinel      | PostgreSQL database name                     |
| `DB_USER`                             | Yes      | sentinel_user | PostgreSQL user                              |
| `DB_PASSWORD`                         | Yes      | —             | PostgreSQL password (use a strong value)     |
| `JWT_SECRET`                          | Yes      | —             | 64-char hex string for JWT signing           |
| `JWT_EXPIRATION_MS`                   | No       | 86400000      | JWT validity in milliseconds (24 h default)  |
| `AI_SERVICE_URL`                      | No       | http://ai-service:8000 | Internal URL of the FastAPI AI service |
| `JPA_DDL_AUTO`                        | No       | update        | Hibernate DDL mode (use `validate` in prod)  |
| `NEXT_PUBLIC_API_URL`                 | Yes      | —             | Public URL of the backend API (frontend use) |
