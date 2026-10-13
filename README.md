# Air Quality Monitor — Backend

Spring Boot REST API with full CRUD for air quality records. Built as the
backend half of the Smart Air Quality Monitoring and Health Alert System
project.

## Tech stack

- Java 21, Spring Boot 3.3
- Spring Web, Spring Data JPA, Bean Validation
- MySQL (production, via Aiven) / H2 in-memory (local development)
- Maven

## Project structure

```
src/main/java/com/aqms/backend/
  BackendApplication.java        entry point
  model/AirQualityRecord.java    JPA entity
  repository/                    Spring Data repository
  controller/                    REST endpoints
  config/CorsConfig.java         CORS setup for the deployed frontend
  exception/                     centralized error handling
```

## Run locally

No database setup needed — the default profile uses an in-memory H2
database, so this works out of the box.

```bash
mvn spring-boot:run
```

The API starts on `http://localhost:8080`. Visit `http://localhost:8080/`
for a status check, or `http://localhost:8080/h2-console` to inspect the
in-memory database (JDBC URL: `jdbc:h2:mem:aqms`, user `sa`, no password).

## API endpoints

| Method | Endpoint             | Description                      |
|--------|-----------------------|-----------------------------------|
| GET    | `/api/records`         | List all records (optional `?city=`) |
| GET    | `/api/records/{id}`    | Get one record                    |
| POST   | `/api/records`         | Create a record                   |
| PUT    | `/api/records/{id}`    | Update a record                   |
| DELETE | `/api/records/{id}`    | Delete a record                   |
| GET    | `/api/health`          | Health check                      |

Example request body for `POST`/`PUT`:

```json
{
  "city": "Hyderabad",
  "aqi": 142,
  "pm25": 68.4,
  "pm10": 110.2,
  "remarks": "Hazy morning, construction nearby"
}
```

`category` and `recordedAt` are computed by the server — you don't send them.

## Running against MySQL installed on your laptop

If you have MySQL 8 installed locally (MySQL Installer on Windows), use the
`local` profile. The `aqms` database is created automatically on first run.

PowerShell:

```powershell
$env:DB_PASSWORD = "your-mysql-root-password"
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

Command Prompt / macOS / Linux equivalents work the same way: set
`DB_PASSWORD`, then run `mvn spring-boot:run -Dspring-boot.run.profiles=local`.
Optional overrides: `DB_USER` (default `root`) and `DB_NAME` (default `aqms`).

## Running against MySQL (Aiven) instead of H2

1. Create a free MySQL service on [Aiven](https://aiven.io/) and open its
   **Connection details**.
2. Set these environment variables (locally in a `.env`/your shell, or on
   your deployment platform):

   ```
   SPRING_PROFILES_ACTIVE=prod
   DB_HOST=<your Aiven host>
   DB_PORT=<your Aiven port>
   DB_NAME=<your Aiven database name>
   DB_USER=<your Aiven username>
   DB_PASSWORD=<your Aiven password>
   APP_CORS_ALLOWED_ORIGINS=<your deployed frontend URL>
   ```

3. Run `mvn spring-boot:run` (or the packaged jar) — it will connect to
   Aiven instead of H2 and auto-create the `air_quality_records` table.

## Deploying to Render

Render has no native Java runtime, so the backend is deployed as a Docker
image using the `Dockerfile` in this folder.

1. Push this folder to its own GitHub repository.
2. On [Render](https://render.com/), create a **Web Service** from that repo.
3. Set **Language / Runtime** to **Docker**. Render finds the `Dockerfile`
   automatically, so there is no build or start command to enter.
4. Add the environment variables listed above under the service's
   **Environment** tab (Render injects `PORT` automatically — the app
   already reads it).
6. Once deployed, note the service URL (e.g.
   `https://air-quality-backend.onrender.com`) — the frontend needs it.

## Build a jar manually

```bash
mvn clean package -DskipTests
java -jar target/backend.jar
```
