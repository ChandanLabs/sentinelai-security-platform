# Security Notes

## Current public demo model

- The dashboard and API read routes are public and must contain synthetic demo data only.
- Event ingestion, incident status changes, and marked-demo-data reset require `X-Demo-Ingestion-Key`, configured as the backend-only `DEMO_INGESTION_KEY`.
- The frontend must never receive or store this shared key. Use HTTPS for public requests.
- Event messages are required to start with `SIMULATED DEMO EVENT`; never submit real credentials, API keys, personal data, or actual telemetry.
- Error responses do not intentionally return secrets. Keys are not logged by application code.

The shared key protects demo write operations only. It is not per-user authentication, JWT, or RBAC. This demo also lacks rate limiting, audit logging, tenant isolation, and a production threat model. Do not deploy it as a real monitoring system or use it to protect sensitive data.

## Future security requirements

A production version needs, at minimum, verified user authentication, password hashing, JWT/session management, authorization/RBAC, audit logging, rate limiting, migrations, secret-management controls, and a defined data-retention/tenant-isolation model. These features are not currently implemented; the former browser login was removed because it only simulated authentication.
