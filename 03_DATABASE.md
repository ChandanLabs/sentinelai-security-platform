# Database Domain Model

The domain is implemented incrementally by phase. PostgreSQL is the system of record.

## Implemented in Phase 2
- **User** — authenticated platform user with role and account state.
- **RefreshToken** — hashed refresh-token record with expiry/revocation state.
- **AuditEvent** — authentication/security audit record.

## Implemented in Phase 3
- **Asset** — monitored device/server/application host.
- **Application** — monitored software/service deployed on an asset.
- **EventSource** — logical source that emits security/application events for an application.

Relationships:
- Asset 1 -> N Application
- Application 1 -> N EventSource
- EventSource will be the parent context for ingested Events in Phase 4.

## Implemented in Phase 4
- **Event** — normalized security/application event linked to an EventSource. Stores event type, severity, timestamp, source IP, username, message, optional JSON metadata, and ingestion timestamp.

## Implemented in Phase 5
- **RuleDetection** — persisted deterministic rule match linked to the triggering Event, including rule code/name, severity, reason, and detection timestamp.

## Implemented in Phase 6
- **AnomalyDetection** — persisted AI scoring result linked one-to-one to an Event, including model version, normalized anomaly score, anomaly flag, confidence, reason, status, and detection timestamp.

## Implemented in Phase 7
- **RiskScore** — one explainable risk calculation per event, combining deterministic rule severity and AI anomaly score.

## Planned Later
- Anomaly
- RiskScore
- Alert
- Incident
- Notification
- Organization / Permission (only if later justified)

Schema creation currently uses JPA `ddl-auto: update` for local development. Production migration strategy will be addressed during the hardening/deployment phase.


## Phase 8 Tables

### `alerts`
Stores actionable HIGH/CRITICAL risk notifications. Each event can have at most one alert. Alerts optionally reference an incident.

### `incidents`
Stores analyst investigation cases with severity, lifecycle status, creator, timestamps, and resolution timestamp.


## Phase 8 Tables

### `alerts`
Stores actionable HIGH/CRITICAL risk notifications. Each event can have at most one alert. Alerts optionally reference an incident.

### `incidents`
Stores analyst investigation cases with severity, lifecycle status, creator, timestamps, and resolution timestamp.
## Deployment note
Phase 12 provisions PostgreSQL Flexible Server as the managed cloud equivalent of the local PostgreSQL service. The application schema remains provider-independent.


## Pre-Final Audit Update
The pre-final independent audit has been addressed. Corrective changes are tracked in `PRE_FINAL_AUDIT_FIXES.md`. Full runtime verification remains pending and must be performed before claiming production/demo readiness.
