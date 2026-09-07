# Phase 7 — Risk Scoring

## Status
IMPLEMENTED; VERIFICATION PENDING

## Purpose
Combine event severity, deterministic rule detections, and AI anomaly scoring into one explainable 0–100 risk score and LOW/MEDIUM/HIGH/CRITICAL level.

## Formula
- Base event severity: INFO 5, LOW 20, MEDIUM 45, HIGH 70, CRITICAL 90.
- Rule contribution: the higher of event severity and matched-rule severity, plus up to 10 points for multiple rule matches.
- If an AI score is available: `risk = 0.55 * ruleComponent + 0.45 * (anomalyScore * 100)`.
- If AI is unavailable: rule component is used as the safe deterministic fallback.
- Levels: LOW <25, MEDIUM 25–49, HIGH 50–74, CRITICAL >=75.

## Persistence
`risk_scores` stores one score per event, its components, level, rule count, AI availability, explanation, and calculation timestamp.

## API
- `GET /api/v1/risk-scores`
- `GET /api/v1/risk-scores/event/{eventId}`
- `GET /api/v1/risk-scores/level/{level}`

All are readable by ADMIN/ANALYST/VIEWER.

## Pipeline
`Event -> Rule Detection -> AI Anomaly Detection -> Risk Score -> Phase 8 Alerts/Incidents`

## Boundary
This phase does not create alerts, incidents, notifications, or frontend dashboard functionality.
