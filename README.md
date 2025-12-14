# InnoAd Backend 🚀

API REST para gestión de campañas publicitarias con autenticación JWT, IA integrada y pantallas digitales.

## 🛠️ Stack Tecnológico

| Componente | Versión |
|-----------|---------|
| Spring Boot | 3.5.8 |
| Java | 21 |
| PostgreSQL | 17.6 (Azure) |
| Maven | 3.9.x |
| Seguridad | Spring Security + JWT |

## 📋 Requisitos

- **Java 21+**
- **Maven 3.9+**
- **Git**

## 🚀 Instalación

```bash
# 1. Clonar/extraer proyecto
git clone <repo>
cd innoadBackend

# 2. Compilar
mvn clean compile

# 3. Ver estructura
ls -la src/main/java/com/innoad/modules/
```

## 🏃 Ejecución Local

```bash
# Perfil desarrollo (H2 en memoria)
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Perfil producción (PostgreSQL Azure)
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```

**Backend disponible en**: http://localhost:8080

## 📚 Módulos Principales

```
src/main/java/com/innoad/modules/
├── admin/              # Control de mantenimiento y seguridad
├── campanas/           # Gestión de campañas
├── contenidos/         # Almacenamiento de multimedia
├── pantallas/          # Gestión de pantallas digitales
├── usuarios/           # Gestión de usuarios y roles
├── reportes/           # Estadísticas y reportes
├── chat/               # Sistema de chat con IA
└── utils/              # Utilidades compartidas
```

## 🔐 Seguridad

- **Autenticación**: JWT (tokens)
- **Autorización**: Control de roles (ADMIN, USUARIO, VISITANTE, etc.)
- **Base Datos**: Credenciales en variables de entorno
- **Modo Mantenimiento**: Sistema profesional de control de acceso

## 📡 Endpoints Principales

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/auth/login` | Autenticación |
| GET | `/api/campanas` | Listar campañas |
| POST | `/api/campanas` | Crear campaña |
| GET | `/api/pantallas` | Listar pantallas |
| POST | `/api/admin/mantenimiento/activar` | Activar modo mantenimiento |
| GET | `/api/admin/mantenimiento/estado` | Estado del sistema |

## 🗄️ Base de Datos

### Entornos

- **DEV**: H2 en memoria (sin configuración)
- **PROD**: PostgreSQL 17.6 en Azure Flexible Server

### Conexión

Las credenciales se cargan desde variables de entorno:

```
DB_HOST=servidor.postgres.database.azure.com
DB_PORT=5432
DB_NAME=innoad
DB_USER=usuario
DB_PASSWORD=contraseña
```

## 🐳 Docker

```bash
# Construir imagen
docker build -t innoad-backend:latest .

# Ejecutar contenedor
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_HOST=servidor.postgres.database.azure.com \
  -e DB_USER=usuario \
  -e DB_PASSWORD=contraseña \
  innoad-backend:latest
```

## ☁️ Producción (Azure Container Apps)

```
URL: https://innoad-backend.wonderfuldune-d0f51e2f.eastus2.azurecontainerapps.io
Health: /actuator/health
Versión actual: v2.0.4
```

## 🛠️ Desarrollo

```bash
# Compilar con tests
mvn clean test

# Generar JAR ejecutable
mvn clean package

# Archivo JAR
target/innoad-backend-2.0.0.jar
```

## 📖 Documentación

- **API REST**: Postman collection incluida
- **Estructura**: Ver `src/main/java/com/innoad/`
- **Configuración**: `src/main/resources/application*.yml`

## ✅ Status

- ✅ Compilación: OK
- ✅ Seguridad: Implementada
- ✅ Modo Mantenimiento: Activo
- ✅ IA Chat: Integrada
- ✅ Azure: Desplegado

## 🆕 Cambios recientes

- Se limpió documentación legacy y logs del repo.
- `JWT_SECRET` ahora es Base64 válido para evitar `Illegal base64 character` en login.
- Esquema de `usuarios` alineado (columnas añadidas, constraint `password` relajada).
- Frontend apunta al Container App en Azure.

## 📁 Estructura del Proyecto
- `DELETE /api/contenidos/{id}` - Eliminar

### IA Asistente
- `POST /api/ia/consulta` - Hacer consulta al asistente

## 🔧 Configuración

### Perfiles de Ambiente

- **dev**: Desarrollo local con PostgreSQL local
- **prod**: Producción con Azure PostgreSQL

**Aplicado automáticamente** via `application-prod.yml` en deployment.

### Base de Datos

**Desarrollo**:
```yaml
# application-dev.yml
url: jdbc:postgresql://localhost:5432/innoad_db
username: postgres
password: tu-password
```

**Producción (Azure)**:
```yaml
# application-prod.yml (Variables de entorno)
url: jdbc:postgresql://${AZURE_DB_HOST}:${AZURE_DB_PORT}/${AZURE_DB_NAME}?sslmode=require
username: ${AZURE_DB_USER}
password: ${AZURE_DB_PASSWORD}
```

**Base de Datos Azure**
- Host: `innoad-postgres.postgres.database.azure.com`
- Puerto: `5432`
- Base de datos: `innoad_db`
- Tablas: 53 creadas y funcionando ✅
- SSL: Requerido obligatoriamente

## 🧪 Testing

```bash
# Ejecutar tests
mvn test

# Con cobertura
mvn clean test jacoco:report
```

## 📦 Build

```bash
# Desarrollo
mvn clean package

# Producción (sin tests)
mvn clean package -DskipTests
```

## 🐳 Docker

```bash
# Build imagen
docker build -t innoad-backend .

# Ejecutar contenedor
docker run -p 8080:8080 \
  -e DATABASE_URL=postgresql://... \
  -e JWT_SECRET=... \
  innoad-backend
```

## 📖 Documentación

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/v3/api-docs
- **Health Check**: http://localhost:8080/actuator/health

## 🤝 Contribuir

1. Fork el proyecto
2. Crear rama (`git checkout -b feature/nueva-funcionalidad`)
3. Commit cambios (`git commit -m 'Agregar nueva funcionalidad'`)
4. Push a rama (`git push origin feature/nueva-funcionalidad`)
5. Abrir Pull Request

## 📄 Licencia

Este proyecto es privado y propietario.

## 👥 Autores

- **Equipo InnoAd** - Desarrollo inicial

## 🆘 Soporte

Para dudas o problemas:
- Crear issue en GitHub
- Email: soporte@innoad.com

---

**Versión**: 2.0.0  
**Última actualización**: Noviembre 2025
│   ├── screens/           # Gestión de pantallas
│   └── stats/             # Estadísticas y reportes
├── servicio/              # Servicios compartidos
├── shared/                # Componentes compartidos
│   └── security/          # Configuración de seguridad
└── utilidad/              # Utilidades y helpers
```

## Endpoints de la API

### Autenticación (`/api/v1/auth` y `/api/auth`)

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| POST | `/iniciar-sesion` | Login de usuario | No |
| POST | `/registrar` | Registro de nuevo usuario | No |
| POST | `/refresh` | Renovar token JWT | No |
| PUT | `/perfil` | Actualizar perfil de usuario | Sí |
| GET | `/verificar-email` | Verificar email con token | No |

**Ejemplo: Actualizar Perfil**
```json
PUT /api/v1/auth/perfil
Authorization: Bearer {token}

{
  "email": "nuevo@email.com",
  "telefono": "+57 300 123 4567",
  "direccion": "Calle 123 #45-67, Bogotá",
  "avatarUrl": "https://example.com/avatar.jpg"
}
```

### Campañas (`/api/v1/campanas`)

| Método | Endpoint | Descripción | Roles |
|--------|----------|-------------|-------|
| GET | `/` | Listar campañas | Todos |
| POST | `/` | Crear campaña | Admin, Gerente |
| PUT | `/{id}` | Actualizar campaña | Admin, Gerente |
| DELETE | `/{id}` | Eliminar campaña | Admin |

### Contenido (`/api/v1/contenidos`)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/` | Listar contenidos |
| POST | `/` | Subir contenido |
| GET | `/{id}` | Obtener contenido |
| DELETE | `/{id}` | Eliminar contenido |

### Pantallas (`/api/v1/pantallas`)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/` | Listar pantallas |
| POST | `/` | Registrar pantalla |
| PUT | `/{id}` | Actualizar pantalla |
| GET | `/{id}/estado` | Estado de pantalla |

## Modelos de Datos

### Usuario

```java
{
  "id": Long,
  "nombre": String,
  "apellido": String,
  "email": String (único),
  "password": String (encriptado),
  "telefono": String,
  "cedula": String (20 chars max),
  "direccion": String (200 chars max),
  "avatarUrl": String,
  "rol": RolUsuario (ADMIN, GERENTE, TECNICO, USUARIO),
  "verificado": Boolean,
  "fechaCreacion": LocalDateTime
}
```

### Campaña

```java
{
  "id": Long,
  "nombre": String,
  "descripcion": String,
  "fechaInicio": LocalDateTime,
  "fechaFin": LocalDateTime,
  "estado": EstadoCampana,
  "contenidos": List<Contenido>
}
```

## Seguridad y Autenticación

### JWT (JSON Web Tokens)

El sistema utiliza JWT para autenticación stateless. El token se genera al iniciar sesión y debe incluirse en todas las peticiones protegidas:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Roles y Permisos

| Rol | Permisos |
|-----|----------|
| **ADMIN** | Acceso total al sistema |
| **GERENTE** | Gestión de campañas, contenidos, reportes |
| **TECNICO** | Gestión de pantallas, mantenimiento |
| **USUARIO** | Visualización de campañas y estadísticas |

### CORS

Configurado para permitir solicitudes desde:
- http://localhost:4200 (Angular dev server)
- https://tu-dominio-produccion.com

## Base de Datos

### Inicialización

Ejecutar los scripts en orden:

1. **DATABASE-SCRIPT.sql**: Crea tablas y estructura
2. **init-database.sql**: Datos iniciales (usuarios de prueba)

```bash
psql -U innoaduser -d innoaddb -f DATABASE-SCRIPT.sql
psql -U innoaduser -d innoaddb -f init-database.sql
```

### Usuarios Predeterminados

| Usuario | Password | Rol | Descripción |
|---------|----------|-----|-------------|
| admin | Admin123! | ADMIN | Administrador del sistema |
| tecnico | Admin123! | TECNICO | Soporte técnico |
| dev | Admin123! | GERENTE | Desarrollo y pruebas |
| usuario | Admin123! | USUARIO | Usuario estándar |

## Desarrollo y Cambios Recientes

### Implementación de Perfil de Usuario (Última Actualización)

**Funcionalidades Añadidas:**

1. **Nuevos Campos en Modelo Usuario**
   - `cedula`: String (20 caracteres) - Documento de identidad
   - `direccion`: String (200 caracteres) - Dirección física
   - `avatarUrl`: URL del avatar del usuario

2. **Nuevos Endpoints**
   - `PUT /api/v1/auth/perfil`: Actualización de perfil con validación de email único
   - `PUT /api/auth/perfil`: Versión sin versionado de API

3. **DTOs Creados**
   - `SolicitudActualizarPerfil`: Para recibir actualizaciones de perfil
   - Extensión de `RespuestaLogin.UsuarioLogin`: Incluye nuevos campos

4. **Servicios Implementados**
   - `actualizarPerfil()` en `ServicioAutenticacion`: Actualiza datos del usuario autenticado con validación de contexto de seguridad

### Problemas Resueltos

#### 1. Error de Conversión de Enum RolUsuario
**Problema**: No se podía asignar directamente `RolUsuario` (enum) a `RolSimple` que esperaba `String`.

**Solución**: Implementación de switch statement en `ControladorAutenticacion.java` línea 83:
```java
String rolString = switch (respuestaAuth.getRol()) {
    case ADMIN -> "ADMIN";
    case GERENTE -> "GERENTE";
    case TECNICO -> "TECNICO";
    case USUARIO -> "USUARIO";
};
```

#### 2. Compilación Maven Bloqueada
**Problema**: Maven quedaba colgado en fase de compilación.

**Solución**: 
- Usar `mvn clean package -DskipTests` para compilación rápida
- En casos extremos: `mvn package -Dmaven.compiler.skip=true`

#### 3. Logs de Desarrollo en Producción
**Problema**: `System.out.println` y `System.err.println` en código de producción.

**Solución**: Reemplazados con SLF4J Logger apropiado:
```java
// Antes
System.err.println("Error: " + e.getMessage());

// Después
log.error("Error al enviar email de verificación: {}", e.getMessage());
```

### Estado Actual del Proyecto

**✅ Completado:**
- Sistema de autenticación JWT con refresh tokens
- Gestión completa de usuarios con roles y permisos
- CRUD de campañas, contenidos y pantallas
- Actualización de perfil de usuario con avatar
- Verificación de email con tokens
- Logging apropiado con SLF4J
- Validación de datos con Bean Validation
- Manejo de excepciones centralizado
- Documentación Swagger/OpenAPI

**🔄 En Desarrollo:**
- Sistema de estadísticas avanzadas
- Asistente IA para recomendaciones
- Notificaciones push

**📋 Pendiente:**
- Tests unitarios completos (actualmente con `-DskipTests`)
- Tests de integración
- Métricas con Actuator
- Caché con Redis

## Scripts de Utilidad

### Windows

```batch
# Iniciar backend
START.bat

# Compilar y ejecutar
RUN.bat

# Desplegar con Docker
deploy.bat

# Verificar conexión a BD
test-connection.bat

# Test de login
test-login.bat
```

## Troubleshooting

### Error: Puerto 8080 en uso

```bash
# Windows
netstat -ano | findstr ":8080"
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:8080 | xargs kill -9
```

### Error: No se puede conectar a PostgreSQL

1. Verificar que PostgreSQL esté corriendo:
```bash
docker ps  # Si usas Docker
# O verificar servicio en Windows/Linux
```

2. Verificar credenciales en `application-dev.yml`

3. Verificar firewall no bloquee puerto 5432

### Error: Token JWT inválido

- El token expira en 24 horas por defecto
- Usar endpoint `/refresh` para obtener nuevo token
- Verificar que `JWT_SECRET` sea el mismo en todas las instancias

## Contacto y Soporte

- **Repositorio**: https://github.com/Crisb26/innoadBackend
- **Issues**: https://github.com/Crisb26/innoadBackend/issues

## Licencia

MIT License - Ver archivo LICENSE para más detalles.
