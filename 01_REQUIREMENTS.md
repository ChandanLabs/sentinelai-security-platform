# SentinelAI Requirements

## Functional Requirements
- **Event Ingestion:** The system must collect and ingest security and application events.
- **Normalization:** The system must parse and normalize ingested events into a standard format.
- **Rule-Based Detection:** The system must apply deterministic rules to identify known suspicious patterns.
- **Anomaly Detection:** The system must use an AI model (Isolation Forest) to detect novel anomalies in event data.
- **Risk Scoring:** The system must calculate a risk score based on rule matches and anomaly scores.
- **Alert Generation:** The system must generate alerts when risk scores exceed configurable thresholds.
- **Incident Management:** The system must allow security analysts to view, investigate, and manage alerts as incidents.
- **Analyst Dashboard:** The system must provide a dark-themed UI dashboard for monitoring and investigation.

## Non-Functional Requirements
- **Security:** The system must enforce strong authentication, RBAC, and secure communication.
- **Local Runnability:** The system must be fully deployable locally via Docker Compose.
- **Modularity:** The system must maintain clear separation between Frontend, Core Backend (Java), and ML backend (Python).
- **Maintainability:** Code must be structured well following OOP/SOLID and functional principles appropriately.

## Users and Roles
- **Admin:** Has full access to configure the platform, users, rules, and system settings.
- **Security Analyst:** Can view dashboards, investigate alerts, resolve incidents, and view event logs.

## Major Modules
- Event Receiver & Processor
- Detection Engine (Rules)
- ML Anomaly Engine (FastAPI)
- Alert & Incident Manager
- Web Dashboard

## Major Workflows
1. **Detection Workflow:** Event -> Normalization -> Rule Check -> ML Check -> Risk Score -> Alert (if threshold met).
2. **Investigation Workflow:** Analyst logs in -> Views dashboard -> Selects Alert -> Analyzes linked events -> Updates Incident status.

## Project Constraints
- Must use the locked tech stack strictly (Java, Next.js, Python, Postgres, etc.).
- Educational system: Security demonstrations use synthetic data. No real malware or offensive tool implementation.

## Academic Objectives
- Demonstrate full-stack engineering capability.
- Integrate Machine Learning with traditional software engineering.
- Implement a realistic cybersecurity use case.
- Deploy in a cloud environment (Azure) while preserving local development.
