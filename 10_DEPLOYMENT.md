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
