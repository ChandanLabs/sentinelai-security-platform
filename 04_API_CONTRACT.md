# API Contract

All routes use `/api/v1`. JSON names are camelCase. Event IDs are server-generated UUIDs; event timestamps are assigned by the backend. Paginated reads return `content`, `totalElements`, `totalPages`, and `number`.

## Health

- `GET /api/v1/health` — backend/database application status and AI service status. `aiInference` is `NOT_CONFIGURED`.
- `GET /health` — AI service health. Returns `{"status":"UP","inference":"NOT_CONFIGURED"}`; no analysis endpoint is implemented.

## Events

- `POST /api/v1/events` — validate and persist one synthetic event, evaluate deterministic rules, and create an associated alert/incident when a rule threshold is met.
- `GET /api/v1/events?page=0&size=20` — list persisted events, newest first.
- `GET /api/v1/events/{uuid}` — retrieve one persisted event.

Writes require `X-Demo-Ingestion-Key`. The request body must include:

```json
{
  "eventType": "login_failed",
  "sourceIp": "198.51.100.42",
  "username": "admin-demo",
  "message": "SIMULATED DEMO EVENT — synthetic failed-login attempt"
}
```

All fields are validated. The message must start with `SIMULATED DEMO EVENT`; a real telemetry or authentication integration is not supported. The response includes `id`, `timestamp`, server-assigned `severity`/`riskScore`, `detectionRule`, `aiAnalysisStatus: NOT_CONFIGURED`, and linked `alertId`/`incidentId` values when a rule created those records. `GET /api/v1/events/{uuid}` returns the association IDs for inspecting the event's alert and incident.

Implemented rules:

- Five `login_failed` events from the same source IP within five minutes fire `AUTH_BRUTE_FORCE`.
- `privilege_escalation` fires `PRIVILEGE_ESCALATION`.
- `suspicious_command` fires `SUSPICIOUS_COMMAND`.

The rule scores are illustrative demo values, not probabilities or validated model output.

## Alerts, incidents, and dashboard

- `GET /api/v1/alerts?page=0&size=20` — persisted alerts, newest first.
- `GET /api/v1/incidents?page=0&size=20` — persisted incidents, newest first.
- `PATCH /api/v1/incidents/{uuid}/status` — move one incident forward one lifecycle step (`OPEN` → `INVESTIGATING` → `RESOLVED` → `CLOSED`).
- `GET /api/v1/dashboard/stats` — counts and severity/risk summaries calculated from PostgreSQL records.
- `POST /api/v1/events/demo-data/reset` — remove only events whose message starts with the synthetic marker and their linked alerts/incidents.

Incident status changes and reset also require `X-Demo-Ingestion-Key`. Read endpoints are public because the hosted dashboard is an intentionally open, read-only demo. The shared write key is not user authentication and must never be embedded in the frontend.

## Not implemented

User login/refresh, JWT, RBAC, audit logs, alert acknowledgements, AI inference, and real telemetry ingestion are not available. Routes documented as planned in earlier design material remain unimplemented unless listed above.
