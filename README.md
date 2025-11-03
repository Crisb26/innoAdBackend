# InnoAd Backend

## Descripcion del Proyecto
Sistema de gestion de publicidad potenciado por Inteligencia Artificial, desarrollado con Spring Boot 3.5.0. Proporciona una API REST robusta para la gestion de publicidad, usuarios y conversaciones con IA, implementando caracteristicas avanzadas de seguridad y rendimiento.

## Tecnologias Principales
- Java 21 (LTS)
- Spring Boot 3.5.0
- Spring Security con JWT
- Spring Data JPA
- H2 Database (desarrollo) / PostgreSQL (produccion)
- Redis para cache
- Maven 3.9+
- Docker
- Swagger/OpenAPI para documentacion

## Requisitos de Sistema

### Requisitos Minimos
- JDK 21
- Maven 3.9.x
- Docker Desktop (opcional)
- 4GB RAM
- 2 nucleos de CPU
- 10GB espacio en disco

### Software Requerido
1. **Java Development Kit (JDK) 21**
   - Descargar de: https://adoptium.net/
   - Configurar JAVA_HOME

2. **Maven 3.9+**
   - Incluido con el wrapper (mvnw)

3. **Docker Desktop** (opcional para desarrollo, requerido para produccion)
   - Descargar de: https://www.docker.com/products/docker-desktop

## Inicio Rapido

### Opcion 1: Ejecucion directa (Desarrollo)

```bash
# Compilar el proyecto
mvn clean package -DskipTests

# Ejecutar el backend
java -jar target/innoad-backend-2.0.0.jar
```

O usar el script proporcionado:
```bash
start-backend.bat
```

El backend estara disponible en: http://localhost:8080

### Opcion 2: Docker (Produccion)

```bash
# Construir imagen
docker build -t innoad-backend:latest .

# Ejecutar contenedor
docker run -d \
  --name innoad-backend \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  innoad-backend:latest
```

### Opcion 3: Docker Compose (Recomendado)

```bash
# Iniciar servicios
docker-compose up -d

# Ver logs
docker-compose logs -f backend

# Detener servicios
docker-compose down
```

## Configuracion

### Variables de Entorno

Copiar `.env.example` a `.env` y ajustar los valores:

```properties
# Configuracion del servidor
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=dev

# JWT
JWT_SECRET=MiClaveSecretaSuperSeguraParaInnoAdQueDebeSerMuyLarga2024!

# Base de datos (H2 para desarrollo, PostgreSQL para produccion)
DB_URL=jdbc:h2:mem:innoad_db
DB_USERNAME=sa
DB_PASSWORD=

# Correo electronico
MAIL_USERNAME=tu-email@gmail.com
MAIL_PASSWORD=tu-password-de-aplicacion

# Frontend URL
FRONTEND_URL=http://localhost:8080

# Redis (opcional)
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# IA API (opcional)
AI_API_KEY=tu-api-key-aqui
AI_MODEL=gpt-4
```

### Perfiles de Spring

- `dev`: Desarrollo local (H2, logs verbosos)
- `prod`: Produccion (PostgreSQL, seguridad reforzada)

## Estructura del Proyecto

```
innoadBackend/
├── src/
│   ├── main/
│   │   ├── java/com/innoad/
│   │   │   ├── configuracion/    # Configuraciones de Spring
│   │   │   ├── controlador/      # Controladores REST
│   │   │   ├── dto/              # Objetos de transferencia de datos
│   │   │   ├── filtro/           # Filtros de seguridad
│   │   │   ├── modelo/           # Entidades JPA
│   │   │   ├── repositorio/      # Repositorios JPA
│   │   │   ├── servicio/         # Logica de negocio
│   │   │   └── utilidad/         # Clases utilitarias
│   │   └── resources/
│   │       └── application.yml   # Configuracion principal
│   └── test/                     # Pruebas unitarias e integracion
├── Dockerfile                    # Configuracion de Docker
├── docker-compose.yml            # Orquestacion de servicios
├── pom.xml                       # Dependencias Maven
└── README.md                     # Documentacion
```

## Documentacion de la API

### Acceso a Swagger UI
Documentacion interactiva disponible en:
```
http://localhost:8080/swagger-ui.html
```

### Endpoints Principales

#### Autenticacion (Publicos)
- `POST /api/v1/autenticacion/iniciar-sesion` - Login
- `POST /api/v1/autenticacion/refrescar-token` - Renovar token JWT
- `POST /api/v1/autenticacion/cerrar-sesion` - Logout
- `POST /api/v1/autenticacion/registrar` - Registro de usuarios

#### Health Check
- `GET /actuator/health` - Estado del servicio

#### Otros Endpoints (Requieren autenticacion)
- Publicidad: `/api/v1/publicidad/**`
- Conversaciones IA: `/api/v1/ia/**`
- Administracion: `/api/v1/admin/**`

### Seguridad
- Autenticacion mediante JWT
- Tokens Bearer en header `Authorization`
- Roles: ADMINISTRADOR, TECNICO, DESARROLLADOR, USUARIO
- Refresh tokens para renovacion automatica

## Usuarios Precargados

Al iniciar por primera vez, se crean automaticamente estas cuentas:

| Usuario | Contrasena | Rol |
|---------|-----------|-----|
| admin | Admin123! | ADMINISTRADOR |
| tecnico | Tecnico123! | TECNICO |
| dev | Dev123! | DESARROLLADOR |
| usuario | Usuario123! | USUARIO |

## Integracion con Frontend

El backend esta configurado para integrarse con el frontend Angular en:
- **Desarrollo**: http://localhost:4200
- **Produccion**: http://localhost:8080

### Contrato de API

Todas las respuestas siguen el formato `RespuestaAPI<T>`:

```json
{
  "exitoso": true,
  "mensaje": "Operacion exitosa",
  "datos": { ... },
  "timestamp": "2025-11-03T...",
  "errores": []
}
```

### Ejemplo de Login

```bash
curl -X POST http://localhost:8080/api/v1/autenticacion/iniciar-sesion \
  -H "Content-Type: application/json" \
  -d '{
    "nombreUsuarioOEmail": "admin",
    "contrasena": "Admin123!"
  }'
```

Respuesta:
```json
{
  "exitoso": true,
  "mensaje": "Autenticacion exitosa",
  "datos": {
    "token": "eyJhbGc...",
    "tokenActualizacion": "eyJhbGc...",
    "expiraEn": 86400,
    "usuario": {
      "id": 1,
      "nombreUsuario": "admin",
      "email": "admin@innoad.com",
      "rol": { "nombre": "Administrador" },
      "permisos": [...]
    }
  }
}
```

## Desarrollo

### Ejecutar en modo desarrollo

```bash
# Con Maven
mvn spring-boot:run

# O con el JAR compilado
java -jar target/innoad-backend-2.0.0.jar
```

### Ejecutar pruebas

```bash
# Pruebas unitarias
mvn test

# Pruebas de integracion
mvn verify

# Todas las pruebas con cobertura
mvn clean test jacoco:report
```

### Acceso a H2 Console (solo desarrollo)

URL: http://localhost:8080/h2-console

- JDBC URL: `jdbc:h2:mem:innoad_db`
- Usuario: `sa`
- Contrasena: (vacio)

## Despliegue en Produccion

### Prerequisitos
1. Configurar variables de entorno de produccion
2. Usar perfil `prod`: `-Dspring.profiles.active=prod`
3. Configurar PostgreSQL (no usar H2)
4. Configurar servidor SMTP real
5. Usar secreto JWT seguro (no el de ejemplo)

### Docker en Produccion

```bash
# Build
docker build -t innoad-backend:latest .

# Run con variables de entorno
docker run -d \
  --name innoad-backend \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e JWT_SECRET="tu-secreto-muy-seguro" \
  -e DB_URL="jdbc:postgresql://db:5432/innoad" \
  -e DB_USERNAME="innoad_user" \
  -e DB_PASSWORD="password-seguro" \
  -e MAIL_USERNAME="noreply@tudominio.com" \
  -e MAIL_PASSWORD="password-smtp" \
  innoad-backend:latest
```

## Solucion de Problemas

### El backend se cierra inmediatamente
- Verificar que no estas usando perfil `prod` sin configurar las variables requeridas
- El `StartupChecks` valida configuracion obligatoria en produccion

### Error de conexion CORS
- Verificar que el frontend este en los origenes permitidos (application.yml)
- Asegurar que CORS esta habilitado en ConfiguracionSeguridad

### Problemas con JWT
- Verificar que `jwt.secret` esta configurado
- Token debe enviarse como: `Authorization: Bearer <token>`

### Error de compilacion
```bash
# Limpiar cache de Maven
mvn clean

# Reinstalar dependencias
mvn clean install -U
```

## Monitoreo

### Actuator Endpoints
- Health: `/actuator/health`
- Metrics: `/actuator/metrics`
- Info: `/actuator/info`

### Logs
Los logs se almacenan en: `logs/innoad-backend.log`

Nivel de logs configurable en application.yml

## Contribucion

Ver archivo `CONTRIBUTING.md` para guias de contribucion.

## Licencia

Proyecto academico - InnoAd Team 2025

## Contacto

Para reportar problemas o sugerir mejoras, crear un issue en el repositorio.
