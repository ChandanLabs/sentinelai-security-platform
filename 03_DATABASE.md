# Database

PostgreSQL is the only implemented application persistence provider. Spring Data JPA creates/updates the demo schema with `spring.jpa.hibernate.ddl-auto=update`. A production schema migration tool is not configured.

## Persisted records

- **SecurityEvent** (`security_events`): server UUID, event type, assigned severity and timestamp, source IP, demo username/message, illustrative risk score, matched rule, and AI analysis status.
- **AlertRecord** (`security_alerts`): generated title/severity/status, timestamp, source IP, linked event UUID, detection rule, and event count.
- **IncidentRecord** (`security_incidents`): generated title/severity/status, creation/update timestamps, and linked alert UUID.

Event, alert, and incident records created by an ingestion request are written in the same Spring transaction. The synthetic-data reset removes marked events and their linked records in incident → alert → event order. It does not truncate unrelated tables or drop the database.

Only the synthetic marker-prefixed request messages are accepted. Do not place credentials, API keys, or real company/user telemetry in the demo database.

## Local and hosted persistence

- Docker Compose uses the named `postgres_data` volume; `docker compose down` preserves it. `docker compose down -v` deletes the entire local database.
- Render uses linked managed PostgreSQL. Render free Postgres is limited and expires; it is not permanent hosted storage.
- No JSONL/file-backed provider is implemented. Adding one is deferred rather than exposing ephemeral cloud filesystem data as durable history.

## Not implemented

User/organization/role tables, telemetry-source management, audit logs, retention jobs, backup tooling, multi-tenant isolation, and production schema migrations are not implemented.
