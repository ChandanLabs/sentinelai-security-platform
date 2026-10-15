from fastapi import FastAPI

app = FastAPI(title="SentinelAI - AI Service")

@app.get("/health")
def health_check():
    return {"status": "UP", "inference": "NOT_CONFIGURED"}
