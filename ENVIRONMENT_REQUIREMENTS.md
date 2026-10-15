# Environment Requirements

## Docker workflow (recommended)

Docker supplies the project runtimes inside the containers. The host needs:

- Docker Desktop with Docker Compose v2 installed and the Docker Engine running.
- At least 4 GB of memory available to Docker.
- A local `.env` with a strong PostgreSQL password and demo write key.

The host does not need Node.js, Java, or Python installed just to start the Compose deployment. In this workspace Docker Engine 28.4.0 was available and running when the updated services were built.

## Native development tools

Only install/use these when running a service outside Docker:

- Node.js 20 LTS for the Next.js frontend.
- JDK 21 for the Spring Boot backend.
- Python 3.11 for the FastAPI AI service.

Use the existing Python virtual environment for native AI-service development; do not create another one if the project environment is already configured. Docker-based checks use the Python 3.11 image and do not depend on the host virtual environment.

## Verify local containers

```powershell
docker info
docker compose ps
Invoke-RestMethod http://localhost:8080/api/v1/health
Invoke-RestMethod http://localhost:8000/health
```

The AI service response states `inference: NOT_CONFIGURED`: installing machine-learning packages does not mean a model is trained or running.
