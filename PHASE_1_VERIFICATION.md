# Phase 1 Verification Matrix

| Component | Expected | Actual | Status |
| :--- | :--- | :--- | :--- |
| Node.js | available | not installed | FAIL |
| npm | available | restricted/unavailable | FAIL |
| Java 21 | available | version 1.8.0_472 found | FAIL |
| Python 3.11 | available | not installed | FAIL |
| Docker | available | version 28.4.0 | PASS |
| Docker Engine | reachable | connection error | FAIL |
| Frontend build | success | skipped (Node/Docker missing) | FAIL |
| Backend build | success | skipped (Java 21/Docker missing)| FAIL |
| Backend tests | success | skipped | FAIL |
| AI service | starts | skipped | FAIL |
| PostgreSQL | healthy | skipped | FAIL |
| Docker Compose| starts | skipped | FAIL |
| Frontend endpoint| HTTP 200 | skipped | FAIL |
| Backend health | HTTP 200 | skipped | FAIL |
| AI health | HTTP 200 | skipped | FAIL |
