# SentinelAI

**Intelligent Security Monitoring & Anomaly Detection Platform**

## Project Description
SentinelAI is a defensive cybersecurity monitoring platform designed to ingest application and security events, process them through deterministic rules and AI-based anomaly detection (Isolation Forest), calculate risk scores, and present alerts via a dark-themed analyst dashboard.

## Architecture Overview
- **Frontend:** Next.js (TypeScript, Tailwind CSS, shadcn/ui)
- **Backend Core:** Spring Boot (Java 21, Spring Security, Data JPA)
- **AI Service:** FastAPI (Python, scikit-learn)
- **Database:** PostgreSQL
- **Infrastructure:** Docker Compose (Local), Azure (Cloud)

## Tech Stack
- React, Next.js, Tailwind CSS
- Java 21, Spring Boot 3
- Python 3, FastAPI, Pandas, scikit-learn
- PostgreSQL 15
- Docker, Git

## Local Setup
Ensure you have Docker and Docker Compose installed.

1. Clone the repository.
2. The environment is pre-configured for local execution in `.env.example`.
3. Start the application:
   ```bash
   docker compose up -d --build
   ```
4. Access endpoints:
   - Frontend: `http://localhost:3000`
   - Backend API Health: `http://localhost:8080/api/v1/health`
   - AI Service Health: `http://localhost:8000/health`

## Project Structure
- `frontend/` - Next.js UI dashboard
- `backend/` - Spring Boot API and core business logic
- `ai-service/` - FastAPI ML anomaly detection
- `infrastructure/` - Docker and cloud deployment configs
- `docs/` - Architectural and research documentation
- `tests/` - System tests
- `scripts/` - Automation scripts

## Development Phases
- Phase 1: Foundation (CURRENT)
- Phase 2: Authentication and RBAC
- Phase 3: Core backend
- Phase 4: Event and log ingestion
- ...

## CURRENT STATUS
PHASE 1 — FOUNDATION
