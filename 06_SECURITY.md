# Security Notes

## Current demo protections

- Registration and sign-in use BCrypt password hashes; raw passwords are not written into activity records.
- The backend establishes an HttpOnly, SameSite Strict session cookie and requires a session-bound CSRF token for state-changing requests. The Secure cookie flag is enabled in the Render Blueprint and disabled for local HTTP Compose use.
- The Next.js API proxy forwards only the `JSESSIONID` cookie, not arbitrary browser cookies.
- Workspace documents, events, alerts, incidents, and statistics are scoped to the authenticated account. Cross-account document reads return not-found rather than revealing ownership.
- A user can delete their account and its documents and security records. Events and linked alert/incident records are also automatically retained only for the configured period (30 days by default).
- Login failures are throttled by normalized username in memory. Activity source IP is deliberately not collected. Failed attempts for unknown usernames are not stored as account-owned events to avoid cross-account data leakage.
- `/api/v1/health` and the AI `/health` endpoint are public. Workspace and security-data APIs require an authenticated session.

## Important limitations

This is an application-activity demo, not a production security product. Do not use real passwords, confidential content, or real security telemetry.

- Sessions and login throttling are process-local/in-memory. Multiple backend replicas do not share them; restarts invalidate sessions and reset throttle counters. Keep the demo to one backend instance.
- Signup is not rate-limited, and there is no verified email, MFA, password recovery, or account lockout/support process.
- There are no roles, tenant administrators, distributed rate limits, security audit export, production migrations/backups, or independent security review.
- The deterministic rules and severity-to-risk mapping are illustrative, not a validated risk model. The AI service is health-only; inference is not configured.
- Public deployment URLs allow anyone to create an account. Keep accounts and sample content synthetic and follow the host's current service/database security guidance.

Do not present this demo as host-wide monitoring: it records only actions executed inside this SentinelAI sample workspace.
