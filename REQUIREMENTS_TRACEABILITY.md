# SentinelAI — Requirements Traceability

| ID | Requirement / Objective | Main Implementation | Final Evidence |
|---|---|---|---|
| OBJ-01 | Ingest structured security events | Event + EventSource modules | API smoke + E2E |
| OBJ-02 | Detect known suspicious patterns | RuleEngine | Rule tests + E2E |
| OBJ-03 | Detect anomalous behavior | FastAPI Isolation Forest + Java client | AI test + E2E |
| OBJ-04 | Prioritize security findings | RiskScoreService | Risk tests + E2E |
| OBJ-05 | Manage alerts/incidents | AlertService + IncidentService | Lifecycle tests + E2E |
| OBJ-06 | Provide secure analyst access | JWT + RBAC + security hardening | Auth/RBAC tests |
| OBJ-07 | Provide analyst visibility | Next.js dashboard | Frontend build + UI evidence |
| OBJ-08 | Support reproducible deployment | Docker Compose + CI/CD + Azure IaC | Compose/CI/Azure evidence |

## Functional Requirements

| ID | Requirement | Module |
|---|---|---|
| FR-01 | User registration/login/logout/refresh | Authentication |
| FR-02 | Role-based authorization | Security/RBAC |
| FR-03 | Manage assets | Core backend |
| FR-04 | Manage applications | Core backend |
| FR-05 | Manage event sources | Core backend |
| FR-06 | Ingest and retrieve events | Event ingestion |
| FR-07 | Evaluate deterministic rules | Rule detection |
| FR-08 | Run anomaly detection | AI service |
| FR-09 | Calculate risk | Risk scoring |
| FR-10 | Generate/manage alerts | Alerts |
| FR-11 | Create/manage incidents | Incidents |
| FR-12 | View security operations data | Dashboard |
| FR-13 | Run locally with Docker | Infrastructure |
| FR-14 | Validate changes through CI | GitHub Actions |
| FR-15 | Provide Azure deployment assets | Infrastructure/Azure |

## Non-Functional Requirements
- Security: authentication, RBAC, validation, secure password storage, CORS, security headers, secret-safe configuration.
- Maintainability: modular package structure and project-memory documentation.
- Portability: local Docker Compose plus cloud deployment assets.
- Explainability: deterministic rules, risk explanation, AI model/version metadata.
- Resilience: deterministic risk fallback when AI is unavailable.
