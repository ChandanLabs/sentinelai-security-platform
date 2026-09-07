from __future__ import annotations

import hashlib
import ipaddress
import os
from datetime import datetime
from pathlib import Path
from typing import Any

import joblib
import numpy as np
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field
from sklearn.ensemble import IsolationForest

APP_VERSION = "1.0.0"
MODEL_VERSION = "isolation-forest-v1"
MODEL_PATH = Path(os.getenv("MODEL_PATH", "model/sentinelai-isolation-forest.joblib"))

app = FastAPI(
    title="SentinelAI - AI Anomaly Service",
    version=APP_VERSION,
    description="Defensive anomaly scoring service using Isolation Forest.",
)


class EventFeatures(BaseModel):
    event_type: str = Field(min_length=1, max_length=80)
    severity: str = Field(min_length=1, max_length=20)
    timestamp: datetime
    source_ip: str | None = None
    username: str | None = None
    message: str = Field(min_length=1, max_length=4000)
    metadata_json: str | None = None


class PredictionRequest(BaseModel):
    event: EventFeatures


class PredictionResponse(BaseModel):
    model_ready: bool
    model_version: str
    anomaly_score: float | None = None
    is_anomaly: bool | None = None
    confidence: float | None = None
    reason: str


class TrainRequest(BaseModel):
    events: list[EventFeatures] = Field(min_length=20, max_length=10000)


class TrainResponse(BaseModel):
    model_version: str
    samples: int
    contamination: float
    message: str


def stable_hash(value: str) -> float:
    digest = hashlib.sha256(value.lower().encode("utf-8")).hexdigest()
    return int(digest[:8], 16) / 0xFFFFFFFF


def ip_features(value: str | None) -> tuple[float, float, float, float]:
    if not value:
        return (0.0, 0.0, 0.0, 0.0)
    try:
        ip = ipaddress.ip_address(value)
        packed = list(ip.packed)
        padded = (packed + [0, 0, 0, 0])[:4]
        return tuple(x / 255.0 for x in padded)
    except ValueError:
        return (0.0, 0.0, 0.0, 0.0)


def severity_value(value: str) -> float:
    return {
        "INFO": 0.0,
        "LOW": 0.25,
        "MEDIUM": 0.5,
        "HIGH": 0.75,
        "CRITICAL": 1.0,
    }.get(value.upper(), 0.5)


def to_vector(event: EventFeatures) -> list[float]:
    ip1, ip2, ip3, ip4 = ip_features(event.source_ip)
    return [
        stable_hash(event.event_type),
        severity_value(event.severity),
        event.timestamp.hour / 23.0 if event.timestamp.hour else 0.0,
        event.timestamp.weekday() / 6.0,
        min(len(event.message), 4000) / 4000.0,
        1.0 if event.username else 0.0,
        min(len(event.metadata_json or ""), 8000) / 8000.0,
        ip1, ip2, ip3, ip4,
    ]


def load_model() -> IsolationForest | None:
    if not MODEL_PATH.exists():
        return None
    try:
        return joblib.load(MODEL_PATH)
    except Exception:
        return None


@app.get("/health")
def health_check() -> dict[str, Any]:
    model = load_model()
    return {
        "status": "UP",
        "model_ready": model is not None,
        "model_version": MODEL_VERSION,
    }


@app.post("/predict", response_model=PredictionResponse)
def predict(request: PredictionRequest) -> PredictionResponse:
    model = load_model()
    if model is None:
        return PredictionResponse(
            model_ready=False,
            model_version=MODEL_VERSION,
            reason="AI model is not trained yet. Train the model with baseline events.",
        )

    vector = np.asarray([to_vector(request.event)], dtype=float)
    raw_score = float(model.decision_function(vector)[0])
    prediction = int(model.predict(vector)[0])

    # Normalize the model output into a stable 0..1 anomaly score.
    anomaly_score = float(np.clip(0.5 - raw_score, 0.0, 1.0))
    confidence = float(np.clip(abs(raw_score) / 0.25, 0.0, 1.0))
    is_anomaly = prediction == -1

    return PredictionResponse(
        model_ready=True,
        model_version=MODEL_VERSION,
        anomaly_score=round(anomaly_score, 6),
        is_anomaly=is_anomaly,
        confidence=round(confidence, 6),
        reason=(
            "Event is outside the learned baseline pattern."
            if is_anomaly
            else "Event is consistent with the learned baseline pattern."
        ),
    )


@app.post("/train", response_model=TrainResponse)
def train(request: TrainRequest) -> TrainResponse:
    if len(request.events) < 20:
        raise HTTPException(status_code=400, detail="At least 20 baseline events are required.")

    matrix = np.asarray([to_vector(event) for event in request.events], dtype=float)
    model = IsolationForest(
        n_estimators=200,
        contamination="auto",
        random_state=42,
        n_jobs=-1,
    )
    model.fit(matrix)

    MODEL_PATH.parent.mkdir(parents=True, exist_ok=True)
    joblib.dump(model, MODEL_PATH)

    return TrainResponse(
        model_version=MODEL_VERSION,
        samples=len(request.events),
        contamination=0.1 if getattr(model, "contamination", 0.1) == "auto" else float(getattr(model, "contamination", 0.1)),
        message="Isolation Forest model trained and persisted.",
    )
