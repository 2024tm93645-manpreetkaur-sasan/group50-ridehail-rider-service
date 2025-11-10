# Rider Service

A lightweight Spring Boot microservice for managing rider information.

> **Tech Stack:**
> Java 25 • Spring Boot 3.5+ • Gradle • Docker • Docker Compose • PostgreSQL 
> 
> JSON Logging • Correlation ID Filter
> 
> No UI (use Postman / curl)

---

#  Quickstart — Run Locally (Docker Only)

```bash
# 0) Stop & clean previous stack (recommended)
docker compose down -v
```

```bash
# 1) Build Rider Service JAR
cd rider-service
./gradlew clean bootJar
```

```bash
# 2) Build Docker image (fresh build)
docker build --no-cache -t rhf/rider-service:latest .
cd ..
```

```bash
# 3) Start Rider Service + PostgreSQL
docker compose up -d
```

```bash
# 4) Check if service is running
curl http://localhost:9081/actuator/health
```

```bash
# 5) List riders
curl http://localhost:9081/v1/riders
```

---

#  Exposed Ports

| Component     | Port     | Description                |
| ------------- |----------|----------------------------|
| Rider Service | **9081** | REST API                   |
| PostgreSQL    | **5438** | Container 5432 → Host 5438 |

---

#  API Endpoints

### **Health**

```
GET /actuator/health
GET /actuator/info
```

### **Rider CRUD**

```
GET    /v1/riders
GET    /v1/riders/{id}
POST   /v1/riders
PUT    /v1/riders/{id}
DELETE /v1/riders/{id}
```

**Content-Type:** application/json

---

#  Sample Requests

###  Create Rider

```bash
curl -X POST http://localhost:9081/v1/riders \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "phone": "555-1001"
  }'
```

###  Get All Riders

```bash
curl http://localhost:9081/v1/riders
```

### Update Rider

```bash
curl -X PUT http://localhost:9081/v1/riders/{id} \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Name",
    "email": "x@y.com",
    "phone": "123"
  }'
```

---

#  JSON Logging + Correlation ID

Your service emits **structured JSON logs**, e.g.:

```json
{
  "timestamp": "2025-02-09T12:44:23Z",
  "level": "INFO",
  "correlationId": "f7a9012b-8ac0-4c90-8d3e-19b4c03bff80",
  "service": "rider-service",
  "method": "GET",
  "path": "/v1/riders",
  "status": 200,
  "durationMs": 12
}
```

### Correlation ID Behavior

* If the client sends `X-Correlation-Id`, the service uses it.
* If not provided, the service **generates** a new UUID.
* Log entries and downstream operations all include this ID.

Useful for debugging distributed transactions.

---

#  Database

### Auto-seeded CSV

On first startup, the service loads:

```
rider-service/src/main/resources/rhfd_riders.csv
```

### Schema

Created automatically via `schema.sql`.

---

#  Docker Compose Commands

```bash
docker compose up -d              # start stack
docker compose down -v            # stop + remove volumes
docker compose logs -f rider-service   # follow logs
docker exec -it rider-db psql -U rider -d riderdb   # DB shell
```

---
