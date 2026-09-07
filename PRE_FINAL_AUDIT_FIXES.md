# SentinelAI — Pre-Final Audit Fixes

This document records corrective changes made after an independent static audit. The project was not modified by the external auditor.

## Corrected blockers
- Fixed Spring Security Referrer-Policy writer import.
- Restricted `/api/v1/auth/**` public access to register/login/refresh/logout; `/auth/me` now requires authentication.
- Added the missing one-argument rule-detection repository finder used by risk scoring.
- Added the missing `IncidentSeverity` import.
- Added `backend/src/test/resources/application-test.yml` using H2 and `create-drop`.
- Fixed `RiskScoreServiceTest` to mock the entity ID correctly.
- Added `frontend/public/.gitkeep` so the standalone Docker build has the expected directory.

## Corrected security/reliability issues
- Risk scoring now treats deterministic rule/severity score as a floor; AI cannot downgrade a confirmed high/critical signal.
- Removed the event-ingestion transaction boundary around external AI HTTP calls so a slow AI service does not hold the event transaction open.
- Added AI REST connect/read timeouts.
- Removed runnable production-style default DB/JWT secrets from application and Compose configuration; values must be supplied through environment configuration.
- Enforced the documented incident lifecycle: OPEN -> INVESTIGATING -> RESOLVED -> CLOSED.

## Corrected demo
The demo seed now creates five `login_failed` events from one synthetic IP and then a `privilege_escalation` CRITICAL event. This deliberately exercises the deterministic rule engine and the critical alert/incident path.

## Remaining deployment caveat
Azure provisioning is still not executed. Azure infrastructure must be treated as deployment assets until actual cloud verification is performed. Any Azure-specific issue discovered during final testing must be fixed before claiming deployment readiness.

## Verification policy
This file records implementation changes only. It does not claim Maven, Docker, browser, API, Azure, or end-to-end verification. Those are performed in the final verification pass.
