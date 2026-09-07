# SentinelAI — Final Demo Runbook

## Purpose

This runbook demonstrates the real defensive pipeline using synthetic security events. It does not use real credentials, real customer data, or offensive tooling.

## Demo story

1. Start PostgreSQL, backend, AI service, and frontend.
2. Sign in as a synthetic ADMIN account.
3. Open the dashboard and show the baseline state.
4. Run `scripts/demo-seed.ps1` to create a synthetic asset, application, event source, 25 normal events, an AI baseline, and one suspicious critical event.
5. Show the suspicious event in the Events view.
6. Show deterministic rule detections.
7. Show the AI anomaly result and model version.
8. Show the calculated 0–100 risk score.
9. Show the generated HIGH/CRITICAL alert.
10. Show the linked incident and lifecycle.
11. Acknowledge/resolve the alert and move the incident through its valid lifecycle.
12. Explain that all data used in the demonstration is synthetic.

## Demo account setup

Registration creates VIEWER by design. For a local-only demonstration, register a synthetic account and use the SQL helper `scripts/demo-promote-user.sql` to promote it to ADMIN. Do not use this helper in production.

## Evidence to capture

- Login screen
- Dashboard overview
- Event record
- Rule detection
- AI detection with model version
- Risk score and explanation
- Alert status/risk
- Incident status
- Docker service status
- GitHub Actions CI result
- Azure architecture/deployment assets (do not expose secrets)

## Failure-safe demo rule

If AI is unavailable, the backend intentionally falls back to deterministic rule-based risk scoring. Explain this as a resilience feature rather than claiming AI scored the event.
