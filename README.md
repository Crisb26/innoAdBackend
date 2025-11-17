# InnoAd Backend 🚀

API REST para gestión de campañas publicitarias digitales con pantallas inteligentes e IA integrada.

## 🛠️ Stack Tecnológico

- **Framework**: Spring Boot 3.5.7
- **Lenguaje**: Java 21
- **Base de Datos**: PostgreSQL 18.0
- **Build**: Maven 3.9.11
- **Seguridad**: Spring Security + JWT
- **Contenedores**: Docker

## 📋 Requisitos

- Java JDK 21+
- Maven 3.9.11+
- PostgreSQL 18.0+ (o Railway PostgreSQL)
- Docker (opcional)

## 🚀 Inicio Rápido

### Desarrollo Local

```bash
# 1. Configurar variables de entorno
cp .env.example .env
# Editar .env con tus credenciales

# 2. Compilar
mvn clean package -DskipTests

# 3. Ejecutar
java -jar target/innoad-backend-2.0.0.jar
```

**API disponible en**: http://localhost:8080

### Con Docker

```bash
docker-compose up -d --build
```

## ☁️ Despliegue en Railway

Ver guía completa: [DEPLOY.md](DEPLOY.md)

### Pasos Rápidos:

1. **Crear PostgreSQL en Railway**
   - New Project → Provision PostgreSQL
   - Copiar `DATABASE_URL`

2. **Desplegar Backend**
   - Deploy from GitHub
   - Configurar variables de entorno (ver `.env.example`)
   - Railway detectará el `Dockerfile` automáticamente

3. **Variables de Entorno Requeridas**:
   ```
   DATABASE_URL=postgresql://...
   JWT_SECRET=tu-secreto
   SPRING_PROFILES_ACTIVE=prod
   MAIL_USERNAME=tu-email@gmail.com
   MAIL_PASSWORD=tu-app-password
   ```

## 📁 Estructura del Proyecto

```
src/main/java/com/innoad/
├── configuracion/       # Configuración (CORS, Security, JWT)
├── modules/
│   ├── auth/           # Autenticación y usuarios
│   ├── campaigns/      # Gestión de campañas
│   ├── content/        # Contenido multimedia
│   ├── screens/        # Pantallas digitales
│   ├── ia/             # Asistente IA
│   └── reports/        # Reportes y estadísticas
└── shared/             # Utilidades compartidas
```

## 🔐 Seguridad

- JWT para autenticación
- Roles: Administrador, Editor, Usuario
- Encriptación BCrypt
- CORS configurado

## 📝 Endpoints Principales

### Autenticación
- `POST /api/auth/registrarse` - Registro
- `POST /api/auth/iniciar-sesion` - Login
- `GET /api/auth/perfil` - Perfil usuario
- `PUT /api/auth/perfil` - Actualizar perfil

### Campañas
- `GET /api/campanias` - Listar campañas
- `POST /api/campanias` - Crear campaña
- `PUT /api/campanias/{id}` - Actualizar
- `DELETE /api/campanias/{id}` - Eliminar

### Contenidos
- `GET /api/contenidos` - Listar contenidos
- `POST /api/contenidos` - Subir contenido
- `DELETE /api/contenidos/{id}` - Eliminar

### IA Asistente
- `POST /api/ia/consulta` - Hacer consulta al asistente

## 🔧 Configuración

### Perfiles

- **dev**: Desarrollo local
- **prod**: Producción

```bash
# Cambiar perfil
java -jar app.jar --spring.profiles.active=prod
```

### Base de Datos

**Desarrollo**:
```yaml
url: jdbc:postgresql://localhost:5432/innoad_db
username: postgres
password: tu-password
```

**Producción (Railway)**:
```yaml
url: ${DATABASE_URL}
```

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
