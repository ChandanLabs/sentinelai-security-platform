# Phase 6 — AI Anomaly Detection Implementation

## Scope
Integrate the Java/Spring Boot backend with the Python/FastAPI machine-learning service using the locked Isolation Forest algorithm.

## Implemented
- Python FastAPI `/predict` endpoint.
- Python FastAPI `/train` endpoint.
- Model persistence with joblib.
- Deterministic numeric feature extraction from normalized events.
- Isolation Forest with fixed random seed for reproducible training.
- Java AI service client using Spring RestClient.
- Persisted `AnomalyDetection` result per event.
- AI scoring is triggered after rule detection during event ingestion.
- Model training endpoint restricted to ADMIN.
- Read-only AI detection endpoints available to all authenticated roles.
- Graceful AI-service failure: events remain stored and rule detection continues.
- Docker volume for persisted model artifacts.
- Generated model artifacts excluded from Git.

## Feature Vector
The ML service derives features from:
- event type stable hash
- severity
- hour of day
- day of week
- message length
- username presence
- metadata size
- normalized IPv4/IPv6 octets

This intentionally uses normalized event data rather than raw text or credentials.

## Operational Flow
1. Events are stored by the Spring Boot service.
2. Existing deterministic rules run.
3. Spring Boot sends the normalized event to FastAPI.
4. FastAPI loads the persisted Isolation Forest model.
5. The service returns anomaly score, anomaly flag, confidence, model version, and explanation.
6. Spring Boot persists the result.
7. Later phases consume rule and AI signals for risk scoring.

## Training
The ADMIN-only training endpoint uses recent stored events as the baseline. At least 20 events are required. Training data is synthetic/security-demo data only.

## Boundary
Risk scoring, alert generation, incident management, frontend dashboard, and production hardening remain future phases.
