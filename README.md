# InnoAd Backend

REST API for digital advertising campaign management with intelligent screens.

## Stack

- Spring Boot 3.5.7
- Java 21
- PostgreSQL 16
- Maven 3.9.11
- JWT Authentication
- Spring Security
- Docker

## Requirements

- Java 21+
- Maven 3.9+
- PostgreSQL 16+
- Docker (recommended)

## Quick Start

### Docker (Recommended)

```bash
docker-compose -f docker-compose.full.yml up -d --build
```

Access:
- API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html
- PostgreSQL: localhost:5432

### Local Development

```bash
mvn clean package -DskipTests
java -jar target/innoad-backend-2.0.0.jar
```

## Configuration

- Port: 8080
- Context: /api/v1
- Database: PostgreSQL
- JWT Expiration: 24h
- CORS: http://localhost:4200

## API Endpoints

### Authentication
- POST /api/v1/autenticacion/iniciar-sesion
- POST /api/v1/autenticacion/registrar
- POST /api/v1/autenticacion/refresh

### Campaigns
- GET /api/v1/campanas
- POST /api/v1/campanas
- PUT /api/v1/campanas/{id}
- DELETE /api/v1/campanas/{id}

### Content
- GET /api/v1/contenidos
- POST /api/v1/contenidos
- GET /api/v1/contenidos/{id}

### Screens
- GET /api/v1/pantallas
- POST /api/v1/pantallas
- PUT /api/v1/pantallas/{id}

## Project Structure

```
src/main/java/com/innoad/
├── configuracion/      # Spring Configuration
├── controlador/        # REST Controllers
├── dto/                # Data Transfer Objects
├── modelo/             # JPA Entities
├── repositorio/        # JPA Repositories
├── servicio/           # Business Logic
├── filtro/             # Security Filters
└── utilidad/           # Utilities
```

## Profiles

- dev: application-dev.yml
- prod: application-prod.yml

```bash
java -jar target/innoad-backend-2.0.0.jar --spring.profiles.active=prod
```

## Database

Run DATABASE-SCRIPT.sql to initialize the database with all tables and seed data.

## Default Users

- admin / Admin123!
- tecnico / Admin123!
- dev / Admin123!
- usuario / Admin123!

## Repository

https://github.com/Crisb26/innoadBackend

## License

MIT
