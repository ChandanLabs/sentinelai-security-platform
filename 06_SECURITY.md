# Security Guidelines

## Security Requirements
At minimum, the platform will implement:
- Password hashing (bcrypt/Argon2)
- Authentication (JWT-based)
- Authorization & RBAC
- Input validation (at API boundary and Frontend)
- API security (Spring Security)
- Rate limiting
- CORS policy
- Audit logging of all critical actions
- Secret management (Environment Variables)
- HTTPS in production
- Secure error handling (no stack traces in API responses)
- Dependency management (vuln scanning)

## Phase 2 Implementation
- BCrypt password hashing (work factor 12)
- JWT access-token authentication
- Hashed, rotating refresh tokens with revocation and expiry
- Role-based authorization: ADMIN, ANALYST, VIEWER
- Request validation on authentication endpoints
- Centralized JSON authentication/authorization errors
- Authentication audit events for registration, login, refresh, and logout

## Remaining Security Hardening
- Rate limiting, production CORS policy, HTTPS, dependency vulnerability scanning, and broader audit coverage remain planned for later phases.


## Pre-Final Audit Update
The pre-final independent audit has been addressed. Corrective changes are tracked in `PRE_FINAL_AUDIT_FIXES.md`. Full runtime verification remains pending and must be performed before claiming production/demo readiness.
