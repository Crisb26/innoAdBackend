# Guía de Contribución - InnoAd Backend

## Información del Proyecto

### Stack Tecnológico

**Backend Framework:**
- Spring Boot 3.5.0
- Java 21 (LTS)
- Maven 3.9.x

**Dependencias Principales:**
- Spring Security 6.5.0
- Spring Data JPA 3.5.0
- JWT (jjwt) 0.12.6
- H2 Database 2.3.232
- Redis (Lettuce) 6.5.5
- Lombok 1.18.30
- SpringDoc OpenAPI 2.8.0

**Herramientas de Desarrollo:**
- Docker para contenerización
- Git para control de versiones
- Swagger para documentación de API

### Arquitectura del Proyecto

El proyecto sigue una arquitectura en capas:

```
Capa de Presentación (Controladores)
    ↓
Capa de Lógica de Negocio (Servicios)
    ↓
Capa de Persistencia (Repositorios)
    ↓
Capa de Datos (Entidades JPA)
```

**Componentes Principales:**

1. **Configuración (`configuracion/`)**
   - `ConfiguracionSeguridad.java`: Configuración de Spring Security, CORS, filtros JWT
   - `StartupChecks.java`: Verificaciones al inicio de la aplicación

2. **Controladores (`controlador/`)**
   - `ControladorAutenticacion.java`: Endpoints de registro y login

3. **DTOs (`dto/`)**
   - `solicitud/`: Objetos para recibir datos del cliente
   - `respuesta/`: Objetos para enviar respuestas al cliente

4. **Modelos (`modelo/`)**
   - `entidades/`: Entidades JPA mapeadas a base de datos
     - `Usuario.java`: Usuarios del sistema
     - `Publicidad.java`: Gestión de publicidades
     - `ConversacionIA.java`: Historial de conversaciones con IA
     - `ConfiguracionSistema.java`: Configuración global
   - `enumeraciones/`: Enumeraciones del sistema
     - `RolUsuario.java`: Roles (ADMIN, ANUNCIANTE, CLIENTE)

5. **Repositorios (`repositorio/`)**
   - Interfaces que extienden JpaRepository para operaciones CRUD

6. **Servicios (`servicio/`)**
   - `ServicioAutenticacion.java`: Lógica de registro y login
   - `ServicioJWT.java`: Generación y validación de tokens
   - `ServicioEmail.java`: Envío de correos electrónicos
   - `ServicioAgenteIA.java`: Integración con servicios de IA
   - `ServicioModoMantenimiento.java`: Gestión del modo mantenimiento

7. **Filtros (`filtro/`)**
   - `FiltroAutenticacionJWT.java`: Intercepta peticiones para validar tokens

## Configuración del Entorno de Desarrollo

### Requisitos Previos

1. **Java Development Kit 21**
   - Descargar: https://adoptium.net/
   - Verificar instalación: `java -version`
   - Configurar JAVA_HOME en variables de entorno

2. **Maven**
   - El proyecto incluye Maven Wrapper (mvnw)
   - No es necesario instalar Maven globalmente

3. **Git**
   - Descargar: https://git-scm.com/
   - Configurar usuario:
     ```bash
     git config --global user.name "Tu Nombre"
     git config --global user.email "tu@email.com"
     ```

4. **IDE Recomendado: Visual Studio Code**
   - Descargar: https://code.visualstudio.com/
   - Extensiones obligatorias:
     - Extension Pack for Java (Microsoft)
     - Spring Boot Extension Pack (VMware)
     - Lombok Annotations Support
   - Extensiones opcionales:
     - GitLens
     - Docker
     - REST Client

5. **Docker Desktop** (opcional para despliegue local)
   - Descargar: https://www.docker.com/products/docker-desktop

### Clonar el Repositorio

```bash
# Clonar el proyecto
git clone https://github.com/Crisb26/innoAdBackend.git

# Entrar al directorio
cd innoAdBackend

# Verificar rama actual
git branch
```

### Configuración Inicial

1. **Copiar archivos de configuración de ejemplo:**

Crear archivo `src/main/resources/application-local.yml` con tus configuraciones locales:

```yaml
jwt:
  secret: tu-clave-secreta-local-minimo-256-bits
  expiration: 3600000
  refresh-expiration: 86400000

spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: tu-email@gmail.com
    password: tu-app-password
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

2. **Compilar el proyecto:**

```bash
# En Windows
.\mvnw clean install

# En Linux/Mac
./mvnw clean install
```

3. **Ejecutar la aplicación:**

```bash
# Con Maven Wrapper
.\mvnw spring-boot:run

# Con perfil específico
.\mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

4. **Verificar que funciona:**

Abrir navegador en:
- API: http://localhost:8000
- Swagger: http://localhost:8000/swagger-ui.html
- Health: http://localhost:8000/actuator/health

## Flujo de Trabajo con Git

### Estructura de Ramas

- `main`: Rama principal, código estable en producción
- `develop`: Rama de desarrollo, integración de features
- `feature/*`: Ramas para nuevas funcionalidades
- `bugfix/*`: Ramas para corrección de errores
- `hotfix/*`: Ramas para correcciones urgentes en producción

### Crear una Nueva Funcionalidad

```bash
# 1. Actualizar rama main
git checkout main
git pull origin main

# 2. Crear rama para tu feature
git checkout -b feature/nombre-descriptivo

# 3. Realizar cambios y commits
git add .
git commit -m "tipo: descripción clara del cambio"

# 4. Subir cambios
git push origin feature/nombre-descriptivo

# 5. Crear Pull Request en GitHub
```

### Convenciones de Commits

Usar conventional commits:

```
feat: nueva funcionalidad
fix: corrección de bug
docs: cambios en documentación
style: formateo de código
refactor: refactorización sin cambios funcionales
test: agregar o modificar tests
chore: tareas de mantenimiento
```

Ejemplos:
```bash
git commit -m "feat: agregar endpoint para listar publicidades activas"
git commit -m "fix: corregir validación de email en registro"
git commit -m "docs: actualizar README con instrucciones de Docker"
```

## Estructura de Base de Datos

### Entidades Principales

**Usuario:**
- id (Long, PK)
- nombre (String)
- apellido (String)
- email (String, único)
- password (String, hasheado)
- rol (RolUsuario enum)
- activo (Boolean)
- createdAt (LocalDateTime)

**Publicidad:**
- id (Long, PK)
- titulo (String)
- descripcion (String)
- contenido (Text)
- imagenUrl (String)
- activa (Boolean)
- fechaInicio (LocalDateTime)
- fechaFin (LocalDateTime)
- usuario (FK a Usuario)

**ConversacionIA:**
- id (Long, PK)
- mensajeUsuario (Text)
- respuestaIA (Text)
- timestamp (LocalDateTime)
- usuario (FK a Usuario)

### Relaciones

- Usuario 1:N Publicidad (un usuario puede tener muchas publicidades)
- Usuario 1:N ConversacionIA (un usuario puede tener muchas conversaciones)

## Testing

### Ejecutar Tests

```bash
# Todos los tests
.\mvnw test

# Tests específicos
.\mvnw test -Dtest=ControladorAutenticacionTest

# Con coverage
.\mvnw verify
```

### Escribir Tests

Los tests deben ubicarse en `src/test/java` siguiendo la misma estructura de paquetes.

Ejemplo:
```java
@SpringBootTest
class ServicioAutenticacionTest {
    
    @Autowired
    private ServicioAutenticacion servicioAutenticacion;
    
    @Test
    void deberiaRegistrarUsuarioExitosamente() {
        // Arrange
        SolicitudRegistro solicitud = new SolicitudRegistro();
        // ... configurar solicitud
        
        // Act
        RespuestaAutenticacion respuesta = servicioAutenticacion.registrar(solicitud);
        
        // Assert
        assertNotNull(respuesta.getToken());
    }
}
```

## Debugging

### Configuración de VS Code

Crear `.vscode/launch.json`:

```json
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "Spring Boot-InnoAdApplication",
            "request": "launch",
            "cwd": "${workspaceFolder}",
            "mainClass": "com.innoad.InnoAdApplication",
            "projectName": "innoad-backend",
            "args": "",
            "envFile": "${workspaceFolder}/.env"
        }
    ]
}
```

### Logs

Los logs se configuran en `application.yml`:

```yaml
logging:
  level:
    com.innoad: DEBUG
    org.springframework.security: DEBUG
```

## Problemas Comunes y Soluciones

### Error: "Port 8000 already in use"

```bash
# Windows
netstat -ano | findstr :8000
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8000
kill -9 <PID>
```

### Error: "Cannot resolve dependencies"

```bash
# Limpiar cache de Maven
.\mvnw clean
rmdir /s /q %USERPROFILE%\.m2\repository\com\innoad
.\mvnw install
```

### Error: "JWT token validation failed"

Verificar que la clave secreta JWT esté configurada correctamente y tenga al menos 256 bits (32 caracteres).

### Error: "Unable to connect to Redis"

Si no usas Redis localmente, deshabilitarlo en `application-dev.yml`:

```yaml
spring:
  data:
    redis:
      enabled: false
  cache:
    type: simple
```

## Estándares de Código

### Convenciones de Nombres

- **Clases**: PascalCase (ej: `ServicioAutenticacion`)
- **Métodos**: camelCase (ej: `generarToken`)
- **Constantes**: UPPER_SNAKE_CASE (ej: `MAX_INTENTOS_LOGIN`)
- **Paquetes**: lowercase (ej: `com.innoad.servicio`)

### Anotaciones Lombok

El proyecto usa Lombok para reducir boilerplate:

- `@Data`: genera getters, setters, toString, equals, hashCode
- `@Builder`: patrón builder para construcción de objetos
- `@NoArgsConstructor`: constructor sin argumentos
- `@AllArgsConstructor`: constructor con todos los argumentos

### Documentación de Código

Documentar métodos públicos con JavaDoc:

```java
/**
 * Genera un token JWT para el usuario autenticado.
 * 
 * @param userDetails Detalles del usuario autenticado
 * @return Token JWT como String
 * @throws TokenGenerationException si hay error al generar el token
 */
public String generarToken(UserDetails userDetails) {
    // implementación
}
```

## Despliegue Local con Docker

### Construir Imagen

```bash
# Construir imagen
docker build -t innoad-backend:latest .

# Verificar imagen creada
docker images | findstr innoad
```

### Ejecutar Contenedor

```bash
# Ejecutar contenedor
docker run -d ^
  --name innoad-backend ^
  -p 8000:8000 ^
  -e SPRING_PROFILES_ACTIVE=prod ^
  -e JWT_SECRET=tu-clave-secreta ^
  innoad-backend:latest

# Ver logs
docker logs -f innoad-backend

# Detener contenedor
docker stop innoad-backend

# Eliminar contenedor
docker rm innoad-backend
```

## Recursos Adicionales

### Documentación Oficial

- Spring Boot: https://spring.io/projects/spring-boot
- Spring Security: https://spring.io/projects/spring-security
- Spring Data JPA: https://spring.io/projects/spring-data-jpa
- JWT: https://jwt.io/

### Herramientas Útiles

- Postman: Para probar APIs
- DBeaver: Cliente de base de datos
- Git Graph (VS Code): Visualizar historial de commits

## Contacto y Soporte

Para preguntas o problemas:

1. Revisar esta documentación
2. Consultar el README.md
3. Buscar en Issues de GitHub existentes
4. Crear un nuevo Issue describiendo el problema detalladamente
5. Contactar al equipo de desarrollo

## Notas Finales

- Siempre trabajar en una rama separada, nunca directamente en `main`
- Hacer commits pequeños y frecuentes con mensajes descriptivos
- Probar los cambios localmente antes de hacer push
- Mantener el código limpio y bien documentado
- Seguir los estándares de código establecidos
- Actualizar la documentación cuando sea necesario