# API Contract

All backend routes use `/api/v1`. JSON names are camelCase. Event IDs and timestamps are assigned by the backend. Paginated reads return `content`, `totalElements`, `totalPages`, and `number`. Except for health and CSRF initialization, application APIs require the browser's authenticated session. State-changing routes also require `X-CSRF-Token`.

## Health and session setup

- `GET /api/v1/health` — backend/database status and AI service health. `aiInference` is `NOT_CONFIGURED`.
- `GET /health` — AI service health; returns `inference: NOT_CONFIGURED`. No AI analysis route exists.
- `GET /api/v1/auth/csrf` — initialize/read the session-bound CSRF token and return it as `token`.

## Authentication

- `POST /api/v1/auth/register` — create an account and its sample documents, then establish a session. Body: `{"username":"analyst-demo","password":"a-unique-demo-password"}`.
- `POST /api/v1/auth/login` — authenticate and establish a session. Requires `X-CSRF-Token`; invalid credentials return `401`.
- `GET /api/v1/auth/me` — return the current account.
- `POST /api/v1/auth/logout` — record logout and invalidate the session. Requires `X-CSRF-Token`.
- `DELETE /api/v1/auth/account` — delete the account, documents, events, alerts, and incidents, then invalidate the session. Requires `X-CSRF-Token`.

Passwords must meet the API's length/character validation. The demo throttles failed sign-ins by normalized username in process memory; signup has no rate limit. Do not use an important password.

## Workspace actions

- `GET /api/v1/workspace/documents` — list the signed-in account's documents.
- `GET /api/v1/workspace/documents/{uuid}` — read an owned document and record a server-side activity event. Non-owned or unavailable documents return `404`.
- `POST /api/v1/workspace/admin` — record a denied privileged-operation attempt and return `403`. Requires `X-CSRF-Token`.
- `POST /api/v1/workspace/export` — record a data export and return that account's document list as CSV. Requires `X-CSRF-Token`.

The workspace API does not execute operating-system commands or access other sites.

## Events, alerts, incidents, and dashboard

- `GET /api/v1/events?page=0&size=20` — list the signed-in account's persisted events, newest first.
- `GET /api/v1/events/{uuid}` — retrieve one event belonging to that account.
- `GET /api/v1/alerts?page=0&size=20` — list that account's alerts.
- `GET /api/v1/incidents?page=0&size=20` — list that account's incidents.
- `PATCH /api/v1/incidents/{uuid}/status` — advance an incident by exactly one step (`OPEN` → `INVESTIGATING` → `RESOLVED` → `CLOSED`). Requires `X-CSRF-Token`; invalid transitions return `409`. The change is recorded as account activity.
- `GET /api/v1/dashboard/stats` — account-scoped metrics and severity/risk summaries.

There is no public event-ingestion or synthetic-reset endpoint. Events are created only as the backend processes an authenticated workspace action.

## Deterministic detection rules

| Event(s) | Rule | Severity | Window / condition |
|---|---|---|---|
| `login_failed` | `AUTH_BRUTE_FORCE` | HIGH | At least 5 failures against the same known account in 5 minutes |
| `login_success` after `login_failed` | `FAILED_THEN_SUCCESS` | HIGH | One or more prior failures for that account within 5 minutes |
| `access_denied` | `REPEATED_DENIED_ACCESS` | HIGH | At least 3 denied resource requests by that account in 5 minutes |
| `privileged_access_denied` | `PRIVILEGED_ACCESS_DENIED` | HIGH | Any denied administrative request |
| `data_export` | `REPEATED_DATA_EXPORT` | MEDIUM | At least 3 exports by that account in 10 minutes |

Below-threshold failures, denials, and exports are still recorded with lower severity. Severity maps to illustrative risk scores (INFO 0, LOW 25, MEDIUM 50, HIGH 75, CRITICAL 100); these are not probabilities or ML output. Event records use `aiAnalysisStatus: NOT_CONFIGURED`. Source IP is `not-collected`; unknown usernames are not persisted as account-owned events.
