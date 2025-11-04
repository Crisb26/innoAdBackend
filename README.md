# InnoAd Backend

Sistema de gestion de publicidad con IA integrada - API REST

## Despliegue Rapido

### Backend (Puerto 8081)

```bash
cd "c:\Users\bueno\Desktop\PROYECTO FINAL INNOAD\innoadBackend"
deploy.bat
```

El backend se ejecutara en: **http://localhost:8081**

## Endpoints Principales

- API Base: http://localhost:8081/api/v1
- Health Check: http://localhost:8081/actuator/health
- API Docs: http://localhost:8081/swagger-ui.html
- H2 Console: http://localhost:8081/h2-console

## Autenticacion

### Usuarios por defecto

| Usuario | Password | Rol |
|---------|----------|-----|
| admin | Admin123! | ADMINISTRADOR |
| tecnico | Tecnico123! | TECNICO |
| developer | Dev123! | DESARROLLADOR |
| usuario | Usuario123! | USUARIO |

### Ejemplo de Login

```bash
POST http://localhost:8081/api/v1/autenticacion/iniciar-sesion
Content-Type: application/json

{
  "nombreUsuarioOEmail": "admin",
  "contrasena": "Admin123!"
}
```

## Requisitos

- Java 21
- Maven 3.9+

## Stack Tecnologico

- Spring Boot 3.5.0
- Spring Security + JWT
- H2 Database (en memoria)
- Spring Data JPA
- Lombok
- OpenAPI/Swagger

## Integracion con Frontend

CORS configurado para:
- http://localhost:8080 (Frontend en produccion)
- http://localhost:4200 (Frontend en desarrollo)
- http://127.0.0.1:8080

## Compilacion Manual

```bash
mvn clean package -DskipTests
java -jar target/innoad-backend-2.0.0.jar
```
