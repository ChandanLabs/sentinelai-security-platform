# API Contract

All backend APIs will be exposed under the `/api/v1/` prefix. 

*Phase 2 adds authentication and RBAC endpoints. Verification remains pending until the local environment is run.*

## Planned Endpoint Groups

### Health and Diagnostics (Phase 1)
- `GET /api/v1/health` - Application health check for backend.
- `GET /health` - Application health check for AI service (FastAPI).

### Authentication (Phase 2)
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- `POST /api/v1/auth/logout`
- `GET /api/v1/auth/me` (Bearer token required)

### RBAC Verification (Phase 2)
- `GET /api/v1/test/admin` — ADMIN only
- `GET /api/v1/test/analyst` — ADMIN or ANALYST
- `GET /api/v1/test/viewer` — ADMIN, ANALYST, or VIEWER

### Core Backend — Assets (Phase 3)
- `GET /api/v1/assets` — list assets
- `GET /api/v1/assets/{id}` — get asset
- `POST /api/v1/assets` — create asset (ADMIN/ANALYST)
- `PUT /api/v1/assets/{id}` — update asset (ADMIN/ANALYST)
- `DELETE /api/v1/assets/{id}` — delete asset (ADMIN)

### Core Backend — Applications (Phase 3)
- `GET /api/v1/applications`
- `GET /api/v1/applications/{id}`
- `POST /api/v1/applications`
- `PUT /api/v1/applications/{id}`
- `DELETE /api/v1/applications/{id}`

### Core Backend — Event Sources (Phase 3)
- `GET /api/v1/event-sources`
- `GET /api/v1/event-sources/{id}`
- `POST /api/v1/event-sources`
- `PUT /api/v1/event-sources/{id}`
- `DELETE /api/v1/event-sources/{id}`

### Users and Roles
- `GET /api/v1/users`
- `POST /api/v1/users`
- `GET /api/v1/users/{id}`

### Event Ingestion (Phase 4)
- `POST /api/v1/events` - Ingest a normalized security/application event (ADMIN/ANALYST).
- `GET /api/v1/events` - Retrieve historical events with pagination.
- `GET /api/v1/events/{id}` - Retrieve a single event.
- `GET /api/v1/events/source/{sourceId}` - Retrieve events for an event source.

### Alerts
- `GET /api/v1/alerts`
- `GET /api/v1/alerts/{id}`
- `PUT /api/v1/alerts/{id}/status`

### Incidents
- `GET /api/v1/incidents`
- `POST /api/v1/incidents`
- `GET /api/v1/incidents/{id}`
- `PUT /api/v1/incidents/{id}`

## Phase 5 — Rule Detection APIs

### GET /api/v1/detections/rules
Returns paginated deterministic rule matches, newest first. Requires an authenticated ADMIN, ANALYST, or VIEWER.

### GET /api/v1/detections/rules/event/{eventId}
Returns paginated rule matches associated with one event. Requires an authenticated ADMIN, ANALYST, or VIEWER.


## Phase 6 — AI Anomaly Detection APIs

### GET /api/v1/detections/ai
Returns paginated AI anomaly results, newest first. Requires authenticated ADMIN, ANALYST, or VIEWER.

### GET /api/v1/detections/ai/event/{eventId}
Returns the AI result for one event. Requires authenticated ADMIN, ANALYST, or VIEWER.

### POST /api/v1/detections/ai/train
Trains the Isolation Forest baseline from recent stored events. ADMIN only. Request body is optional:
`{"sampleSize":500}`. At least 20 events are required.

### AI service endpoints
- `GET /health` — service and model readiness.
- `POST /predict` — scores one normalized event.
- `POST /train` — trains and persists the Isolation Forest model from baseline events.

AI availability is best-effort during ingestion: an unavailable or untrained model does not discard the event or prevent deterministic rule detection.


### Risk Scoring — Phase 7
- `GET /api/v1/risk-scores` — paginated risk scores
- `GET /api/v1/risk-scores/event/{eventId}` — risk score for one event
- `GET /api/v1/risk-scores/level/{level}` — filter by LOW/MEDIUM/HIGH/CRITICAL


## Phase 8 — Alerts and Incidents

### Alerts
- `GET /api/v1/alerts` — paginated alerts; ADMIN/ANALYST/VIEWER
- `GET /api/v1/alerts/{id}` — alert details
- `GET /api/v1/alerts/status/{status}` — filter by OPEN/ACKNOWLEDGED/RESOLVED
- `GET /api/v1/alerts/risk/{level}` — filter by risk level
- `GET /api/v1/alerts/incident/{incidentId}` — alerts linked to an incident
- `PATCH /api/v1/alerts/{id}/status` — ADMIN/ANALYST status update

Alerts are generated automatically for HIGH and CRITICAL risk scores. CRITICAL alerts are automatically escalated into an incident.

### Incidents
- `GET /api/v1/incidents` — paginated incidents
- `GET /api/v1/incidents/{id}` — incident details
- `GET /api/v1/incidents/status/{status}` — filter by OPEN/INVESTIGATING/RESOLVED/CLOSED
- `POST /api/v1/incidents` — ADMIN/ANALYST create an incident from an unlinked alert
- `PATCH /api/v1/incidents/{id}/status` — ADMIN/ANALYST lifecycle update

Incident lifecycle: `OPEN -> INVESTIGATING -> RESOLVED -> CLOSED`. Closed incidents cannot be reopened in Phase 8.


## Phase 8 — Alerts and Incidents

### Alerts
- `GET /api/v1/alerts` — paginated alerts; ADMIN/ANALYST/VIEWER
- `GET /api/v1/alerts/{id}` — alert details
- `GET /api/v1/alerts/status/{status}` — filter by OPEN/ACKNOWLEDGED/RESOLVED
- `GET /api/v1/alerts/risk/{level}` — filter by risk level
- `GET /api/v1/alerts/incident/{incidentId}` — alerts linked to an incident
- `PATCH /api/v1/alerts/{id}/status` — ADMIN/ANALYST status update

Alerts are generated automatically for HIGH and CRITICAL risk scores. CRITICAL alerts are automatically escalated into an incident.

### Incidents
- `GET /api/v1/incidents` — paginated incidents
- `GET /api/v1/incidents/{id}` — incident details
- `GET /api/v1/incidents/status/{status}` — filter by OPEN/INVESTIGATING/RESOLVED/CLOSED
- `POST /api/v1/incidents` — ADMIN/ANALYST create an incident from an unlinked alert
- `PATCH /api/v1/incidents/{id}/status` — ADMIN/ANALYST lifecycle update

Incident lifecycle: `OPEN -> INVESTIGATING -> RESOLVED -> CLOSED`. Closed incidents cannot be reopened in Phase 8.
## Deployment note
The API contract remains cloud-provider independent. Phase 12 deploys the same HTTP API in Azure Container Apps; no Azure-specific endpoint contract is introduced.


## Pre-Final Audit Update
The pre-final independent audit has been addressed. Corrective changes are tracked in `PRE_FINAL_AUDIT_FIXES.md`. Full runtime verification remains pending and must be performed before claiming production/demo readiness.
