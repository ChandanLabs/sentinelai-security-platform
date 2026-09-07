# Phase 5 — Rule-Based Detection

## Implemented
- Deterministic, explainable rule engine evaluated immediately after event ingestion.
- Persisted `RuleDetection` records linked to triggering events.
- Duplicate protection per event/rule pair.
- Built-in defensive rules:
  - `AUTH_BRUTE_FORCE`: 5+ failed authentication events from the same source IP within 5 minutes.
  - `CRITICAL_EVENT_BURST`: 3+ critical events from the same source IP within 10 minutes.
  - `PRIVILEGE_ESCALATION`: privilege/role/admin/root/sudo indicators in event type or message.
  - `SUSPICIOUS_COMMAND`: selected suspicious command execution patterns in event messages.
- Detection severity and human-readable reason are stored for later risk scoring and alert generation.
- Read-only detection APIs for dashboard/analyst use.

## API
- `GET /api/v1/detections/rules`
- `GET /api/v1/detections/rules/event/{eventId}`

Both endpoints are readable by ADMIN, ANALYST, and VIEWER. Detection creation is internal to the ingestion pipeline.

## Design boundary
Phase 5 does not implement ML anomaly scoring, risk calculation, alerts, incidents, notifications, or automated response. Those remain later phases.

## Verification
Automated verification remains pending until the complete project is ready for the user's local test run.
