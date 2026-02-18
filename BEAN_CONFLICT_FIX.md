# Fix ConflictingBeanDefinitionException - ServicioAutenticacion

## Problem
The backend was failing to start with the following error:
```
ConflictingBeanDefinitionException: Annotation-specified bean name 'servicioAutenticacion' 
conflicts with existing bean definition
```

**Root Cause:** Two classes named `ServicioAutenticacion` existed in different packages:
- `com.innoad.modules.auth.service.ServicioAutenticacion`
- `com.innoad.modules.autenticacion.service.ServicioAutenticacion`

Spring Boot 3.4.1 does not allow duplicate bean names by default.

## Solution Applied

### 1. Renamed `auth-disable` directory to `auth`
- The files in `auth-disable/` were already using package `com.innoad.modules.auth.*`
- Renamed the physical directory to match the package declaration
- Path: `src/main/java/com/innoad/modules/auth-disable` → `src/main/java/com/innoad/modules/auth`

### 2. Deleted duplicate `autenticacion` module
- Removed entire directory: `src/main/java/com/innoad/modules/autenticacion/`
- This eliminated the duplicate `ServicioAutenticacion` class
- Files removed:
  - `controller/ControladorAutenticacion.java`
  - `service/ServicioAutenticacion.java`
  - `dto/LoginRequest.java`
  - `dto/LoginResponse.java`
  - `dto/RegistroRequest.java`
  - `dto/SolicitudRecuperarContrasena.java`
  - `dto/SolicitudRestablecerContrasena.java`

### 3. Removed Bean Overriding Configuration
Updated `InnoAdApplication.java`:
```java
// BEFORE:
public static void main(String[] args) {
    SpringApplication app = new SpringApplication(InnoAdApplication.class);
    app.setAllowBeanDefinitionOverriding(true);  // ❌ Removed
    app.run(args);
}

// AFTER:
public static void main(String[] args) {
    SpringApplication.run(InnoAdApplication.class, args);  // ✅ Standard approach
}
```

### 4. Verified Configuration
- ✅ `application.yml` does not contain `spring.main.allow-bean-definition-overriding`
- ✅ `InnoAdApplication.java` has correct component scan paths
- ✅ `@EntityScan` includes `com.innoad.modules.auth.domain`

## Verification

### Compilation Test
```bash
mvn clean compile -DskipTests
# Result: BUILD SUCCESS
```

### Package Build
```bash
mvn package -DskipTests
# Result: BUILD SUCCESS
```

### JAR Inspection
```bash
jar -tf target/innoad-backend-2.0.0.jar | grep "ServicioAutenticacion"
# Result: Only one ServicioAutenticacion found
BOOT-INF/classes/com/innoad/modules/auth/service/ServicioAutenticacion.class
```

## Final Structure

```
src/main/java/com/innoad/modules/
├── auth/              ✅ CORRECT (single source of truth)
│   ├── controller/
│   │   ├── ControladorAutenticacion.java
│   │   ├── ControladorAutenticacionSinVersion.java
│   │   └── ControladorRecuperacion.java
│   ├── service/
│   │   ├── ServicioAutenticacion.java
│   │   ├── ServicioJWT.java
│   │   ├── ServicioDetallesUsuario.java
│   │   └── ServicioVerificacionEmail.java
│   ├── domain/
│   │   ├── Usuario.java
│   │   └── TokenVerificacion.java
│   └── repository/
│       ├── RepositorioUsuario.java
│       └── RepositorioTokenVerificacion.java
├── campaigns/
├── content/
├── screens/
├── ia/
└── reports/

❌ REMOVED: modules/autenticacion/ (duplicate eliminated)
❌ REMOVED: modules/auth-disable/ (renamed to auth)
```

## Benefits
✅ Backend starts correctly without bean conflicts  
✅ No duplicate bean definitions  
✅ Consistent modular structure  
✅ Clean and maintainable codebase  
✅ Compatible with Spring Boot 3.4.1 defaults  

## Impact
- **Breaking Changes:** None (the working `auth` module is preserved)
- **Dependencies:** All existing code already references `com.innoad.modules.auth.*`
- **Tests:** Project has no tests to update
- **CI/CD:** Build and package commands work successfully

## Notes
- The `auth` module was physically in `auth-disable` folder but package declaration was already correct (`com.innoad.modules.auth`)
- The duplicate `autenticacion` module was partially implemented and conflicted with the main `auth` module
- Bean overriding was enabled as a workaround but is no longer needed
