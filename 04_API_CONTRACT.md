# API Contract

All backend APIs will be exposed under the `/api/v1/` prefix. 

*Note: During Phase 1, ONLY health endpoints are implemented.*

## Planned Endpoint Groups

### Health and Diagnostics (Phase 1)
- `GET /api/v1/health` - Application health check for backend.
- `GET /health` - Application health check for AI service (FastAPI).

### Authentication (Future)
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`

### Users and Roles (Future)
- `GET /api/v1/users`
- `POST /api/v1/users`
- `GET /api/v1/users/{id}`

### Event Ingestion (Future)
- `POST /api/v1/events` - Main entrypoint for log ingestion.
- `GET /api/v1/events` - Retrieve historical events for dashboard.

### Alerts (Future)
- `GET /api/v1/alerts`
- `GET /api/v1/alerts/{id}`
- `PUT /api/v1/alerts/{id}/status`

### Incidents (Future)
- `GET /api/v1/incidents`
- `POST /api/v1/incidents`
- `GET /api/v1/incidents/{id}`
- `PUT /api/v1/incidents/{id}`
