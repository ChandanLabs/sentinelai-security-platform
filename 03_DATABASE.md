# Database

PostgreSQL is the only implemented persistence provider. Spring Data JPA uses `spring.jpa.hibernate.ddl-auto=update` by default for this demo; a production migration tool is not configured.

## Persisted records

- **WorkspaceUser**: account ID, normalized username, BCrypt password hash, and creation time.
- **WorkspaceDocument**: owner account ID, sample title/content, and document ID.
- **SecurityEvent** (`security_events`): owner account ID, server-generated event ID/time, event type, pseudonymous actor/session tag, target, outcome, severity, illustrative risk score, detection rule/evidence, and AI analysis status. Source IP is stored as `not-collected`.
- **AlertRecord** (`security_alerts`): owner account ID, rule/title/severity/status, created/updated times, linked event ID, and grouped event count.
- **IncidentRecord** (`security_incidents`): owner account ID, generated title/severity/status, timestamps, and linked alert ID.

Workspace activity is created by backend operations, not by browser-provided event payloads. Matching event/alert/incident writes occur in a Spring transaction. Repeated matching rules are grouped into an existing open alert/incident for the same owner and rule. Account deletion removes its documents, events, alerts, and incidents.

## Retention and local/hosted storage

- A scheduled job runs daily at 03:15 server time and removes events older than `SENTINEL_DATA_RETENTION_DAYS` (30 days by default), along with linked alerts and incidents.
- User accounts and sample documents are not removed by the retention job; users may delete their account from the workspace.
- Docker Compose uses the named `postgres_data` volume; `docker compose down` preserves it. `docker compose down -v` deletes the entire local database.
- Render uses managed PostgreSQL. Render's free database has capacity and expiration limits; see [10_DEPLOYMENT.md](./10_DEPLOYMENT.md).
- No JSONL/file-backed provider, database backup tooling, or production schema-migration setup is implemented.
