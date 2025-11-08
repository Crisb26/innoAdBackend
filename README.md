# InnoAd Backend

API REST para sistema de gestión de campañas publicitarias digitales con pantallas inteligentes.

## Tecnologías

- **Spring Boot** 3.5.7
- **Java** 21
- **PostgreSQL** 18
- **Maven** 3.9.11
- **JWT** Authentication
- **Spring Security**
- **Docker** support

## Requisitos

- Java 21 o superior
- Maven 3.9+
- PostgreSQL 16+
- Docker (opcional)

## Instalación

### Opción 1: Con Docker

```bash
docker-compose up -d
```

### Opción 2: Local

1. Crear base de datos PostgreSQL:
```sql
CREATE DATABASE innoad_db;
```

2. Configurar variables de entorno (opcional):
```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/innoad_db
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=tu_password
```

3. Compilar:
```bash
mvn clean package -DskipTests
```

4. Ejecutar:
```bash
java -jar target/innoad-backend-2.0.0.jar
```

5. Verificar: http://localhost:8080/actuator/health

## Configuración

- **Puerto**: 8080
- **Context Path**: `/api/v1`
- **Base de datos**: PostgreSQL (auto-creación de tablas con Hibernate)
- **JWT**: Expiración 24 horas
- **CORS**: Configurado para `http://localhost:4200`

## API Documentation

Swagger UI: http://localhost:8080/swagger-ui.html

## Principales Endpoints

### Autenticación
- `POST /api/v1/autenticacion/iniciar-sesion` - Login
- `POST /api/v1/autenticacion/registrar` - Registro
- `POST /api/v1/autenticacion/refresh` - Refrescar token

### Campañas
- `GET /api/v1/campanas` - Listar campañas
- `POST /api/v1/campanas` - Crear campaña
- `PUT /api/v1/campanas/{id}` - Actualizar campaña
- `DELETE /api/v1/campanas/{id}` - Eliminar campaña

### Contenidos
- `GET /api/v1/contenidos` - Listar contenidos
- `POST /api/v1/contenidos` - Subir contenido
- `GET /api/v1/contenidos/{id}` - Obtener contenido

### Pantallas
- `GET /api/v1/pantallas` - Listar pantallas
- `POST /api/v1/pantallas` - Registrar pantalla
- `PUT /api/v1/pantallas/{id}` - Actualizar pantalla

## Estructura del Proyecto

```
src/main/java/com/innoad/
├── configuracion/      # Configuraciones de Spring
├── controlador/        # Controladores REST
├── dto/                # Data Transfer Objects
├── modelo/             # Entidades JPA
├── repositorio/        # Repositorios JPA
├── servicio/           # Lógica de negocio
├── filtro/             # Filtros de seguridad
└── utilidad/           # Utilidades
```

## Perfiles

- **dev**: Desarrollo (application-dev.yml)
- **prod**: Producción (application-prod.yml)

Activar perfil:
```bash
java -jar target/innoad-backend-2.0.0.jar --spring.profiles.active=prod
```
      application.yml - Configuracion principal
      DATABASE-SCRIPT.sql - Script base de datos


DESARROLLO

mvn spring-boot:run


PRODUCCION

mvn clean package -DskipTests
java -jar target\innoad-backend-2.0.0.jar


REPOSITORIO

https://github.com/Crisb26/innoadBackend


CONTACTO

Cristian Bueno
cristianbueno@innoad.com
