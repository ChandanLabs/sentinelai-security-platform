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

## Phase 1 Limitations
*Note: The complete authentication system, JWT validation, and RBAC are explicitly NOT implemented during Phase 1.*
