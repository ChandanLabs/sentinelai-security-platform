# SentinelAI — Viva Questions and Answers

## 1. What is SentinelAI?
SentinelAI is a defensive security monitoring platform that ingests structured events, applies explainable rules and machine-learning anomaly detection, calculates risk, creates alerts/incidents, and presents them through an analyst dashboard.

## 2. Why combine rules and machine learning?
Rules are deterministic and explainable for known patterns. ML can identify unusual combinations or behavior that fixed rules may not capture. Combining both improves coverage while retaining explainability.

## 3. Why Isolation Forest?
Isolation Forest is an unsupervised anomaly-detection algorithm suited to finding unusual observations without requiring a large labelled attack dataset. It works by isolating observations through random partitioning; unusual points tend to be isolated more quickly.

## 4. What is the difference between an alert and an incident?
An alert is a security finding that needs attention. An incident is a managed investigation/workflow that groups or tracks a significant security situation through states such as investigation, resolution, and closure.

## 5. Why use a 0–100 risk score?
It provides a simple prioritization scale that can combine multiple signals and map them to LOW, MEDIUM, HIGH, and CRITICAL levels.

## 6. What happens if the AI service is unavailable?
The backend uses the documented deterministic rule-based risk fallback instead of pretending that an AI result exists.

## 7. Why JWT?
JWT access tokens support stateless API authentication. Refresh tokens are separately managed and rotated/revoked to improve session control.

## 8. Why BCrypt?
BCrypt is designed for password hashing and includes a work factor, making brute-force password guessing more expensive than with fast general-purpose hashes.

## 9. What is RBAC?
Role-Based Access Control restricts operations according to roles. SentinelAI defines ADMIN, ANALYST, and VIEWER roles with different permissions.

## 10. Why PostgreSQL?
It provides reliable relational persistence for users, assets, applications, event sources, events, detections, risk scores, alerts, and incidents.

## 11. Why FastAPI separately from Spring Boot?
The separation lets the Java backend own business/security workflows while the Python service owns ML-specific processing and dependencies.

## 12. Why Docker?
Docker makes local environments reproducible and packages the services consistently for CI/CD and cloud deployment.

## 13. What is the biggest limitation?
The current ingestion model focuses on structured JSON security events rather than implementing a full enterprise-scale log collector/parser ecosystem. ML quality also depends on the quality and representativeness of the training data.

## 14. Is SentinelAI an offensive security tool?
No. It is intentionally defensive. It monitors synthetic/authorized events and does not implement credential theft, malware, persistence, exploitation, or destructive response actions.

## 15. How would you improve it in the future?
Add richer log connectors/parsers, stronger labelled evaluation datasets, model monitoring, drift detection, correlation across multiple event sources, improved alert deduplication, and carefully governed automated response capabilities.

## 16. Why not Kubernetes/Kafka/Redis?
They add operational complexity and resource cost. The current architecture is intentionally smaller and appropriate for the project's scope. They can be considered only when scale requirements justify them.

## 17. How do you prove the project works?
Through the final verification plan: build/test checks, API smoke tests, security/RBAC checks, Docker Compose startup, AI-service checks, and an end-to-end synthetic event pipeline. Claims are recorded only from actual runs.

## 18. What is the role of CI/CD?
CI automatically checks the codebase on changes, reducing the chance that broken backend, frontend, AI, or container configuration reaches the main branch.

## 19. Why Azure?
Azure is the primary cloud target selected for the project, while keeping local Docker Compose as the fallback so the application remains usable if cloud credits expire.

## 20. What would you say if an examiner asks for accuracy?
Do not invent a percentage. Explain that the final accuracy/precision/recall figures depend on a defined evaluation dataset and must be reported from measured experiments. The current project should report the real final verification/evaluation evidence rather than an unsupported number.
