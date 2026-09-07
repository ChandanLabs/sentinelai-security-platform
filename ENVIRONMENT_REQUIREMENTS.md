# Environment Requirements

The following dependencies are missing or misconfigured on the host machine. These must be installed or started to run the SentinelAI Phase 1 verification natively or via Docker.

## 1. Node.js
- **Missing Dependency:** Node.js and npm
- **Required Version:** v18.x or v20.x (LTS)
- **Why the project needs it:** Required to build and run the Next.js frontend application natively.
- **Official Installation Method:** Download the Windows Installer from [nodejs.org](https://nodejs.org/).
- **Verification Command:** `node --version` and `npm --version`

## 2. Java Development Kit (JDK)
- **Missing Dependency:** Java 21 JDK (Currently installed version is 1.8)
- **Required Version:** Java 21
- **Why the project needs it:** Required to compile and run the Spring Boot core backend natively.
- **Official Installation Method:** Download from [Adoptium (Eclipse Temurin)](https://adoptium.net/) or Microsoft Build of OpenJDK.
- **Verification Command:** `java -version` and `javac -version` (should show 21)

## 3. Python
- **Missing Dependency:** Python and pip
- **Required Version:** Python 3.11
- **Why the project needs it:** Required to run the FastAPI AI service and machine learning libraries natively.
- **Official Installation Method:** Download the Windows Installer from [python.org](https://www.python.org/downloads/) (Ensure "Add python.exe to PATH" is checked).
- **Verification Command:** `python --version` and `pip --version`

## 4. Docker Engine (Daemon)
- **Missing Dependency:** Docker Desktop Engine is installed but NOT running.
- **Required Version:** Any recent version (currently 28.4.0 is installed).
- **Why the project needs it:** Required to orchestrate PostgreSQL and the application containers via `docker-compose.yml`.
- **Official Installation Method:** Start the "Docker Desktop" application from the Windows Start Menu and wait for the engine to initialize.
- **Verification Command:** `docker info` (should output server details without errors).
