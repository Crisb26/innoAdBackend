# Guía para Agentes de IA (Copilot)

Objetivo: Ayudar de forma segura y eficiente en este backend Spring Boot (Java 21) modernizado. Usa este documento como brújula antes de proponer cambios.

## Contexto del proyecto
- Stack: Java 21, Spring Boot 3.5.x, Spring Security 6.x, JPA/Hibernate, H2 (memoria) por defecto, PostgreSQL en `dev`, Redis (opcional), Lombok, jjwt 0.12.6, Springdoc (OpenAPI), Maven.
- Módulos clave:
  - `configuracion`: seguridad, CORS, seed de datos.
  - `controlador`: endpoints REST.
  - `servicio`: lógica de negocio (auth, JWT, email, IA, mantenimiento).
  - `modelo`: entidades JPA y enums.
  - `repositorio`: Spring Data JPA.
  - `filtro`: Filtro JWT.
- Puerto por defecto: `server.port=8080`.
- Context path: `/api/v1` (todas las rutas lo incluyen).
- Swagger/OpenAPI: `/api/v1/swagger-ui.html` y `/api/v1/api-docs`.

## Seguridad y autenticación
- JWT con `jjwt 0.12.6`:
  - Verificación: `Jwts.parser().verifyWith(SecretKey)`.
  - Parseo: `parseSignedClaims(token).getPayload()`.
- Roles (`RolUsuario`): `ADMINISTRADOR`, `TECNICO`, `DESARROLLADOR`, `USUARIO`, `VISITANTE`.
- Endpoints públicos: `/api/autenticacion/**`, `/actuator/health`, Swagger.
- CORS permitido: `http://localhost:8080`, `http://localhost:4200`, `http://127.0.0.1:8080`, y dominios `innoad.com`.

## Flujo de autenticación
- Registro: `POST /api/autenticacion/registrar` (DTO: `SolicitudRegistro`).
- Login v1 (frontend): `POST /api/autenticacion/iniciar-sesion` → `RespuestaAPI<RespuestaLogin>` con `{ token, tokenActualizacion, usuario{ rol{nombre}, permisos[] }, expiraEn }`.
- Refresh: `POST /api/autenticacion/refrescar-token` (DTO: `SolicitudRefreshToken`).
- Logout: `POST /api/autenticacion/cerrar-sesion` (stateless no-op).
- Compat: `POST /api/autenticacion/login` mantiene contrato antiguo (`RespuestaAutenticacion`).

## Datos de ejemplo (seed)
- Se crea automáticamente al arrancar si no hay usuarios (`InicializadorDatos`).
- Credenciales:
  - admin / Admin123! (ADMINISTRADOR)
  - tecnico / Tecnico123! (TECNICO)
  - dev / Dev123! (DESARROLLADOR)
  - usuario / Usuario123! (USUARIO)

## Cómo ejecutar
- Compilar: `mvn -q -DskipTests package`
- Local: `mvn spring-boot:run`
- Perfiles: `application.yml` (base H2), `application-dev.yml` (Postgres local), `application-prod.yml` (prod). Usa `-Dspring.profiles.active=dev|prod`.

## Reglas para cambios
- Mantén APIs públicas y contratos DTO estables; si cambian, actualiza Swagger y documentación.
- Prefiere cambios pequeños, atómicos y probados.
- Usa `PasswordEncoder` para contraseñas; nunca en claro.
- Verifica CORS cuando añadas nuevos orígenes o puertos.
- Agrega logs claros (nivel INFO/DEBUG) y evita credenciales o secretos en logs.
- Si añades dependencias, valida CVEs con OWASP DC y pinnea versiones.

## Tareas frecuentes
- Endpoints: crea en `controlador`, delega en `servicio`, persiste con `repositorio`, datos en `modelo`.
- Seguridad: ajusta `ConfiguracionSeguridad` (rutas, roles, CORS). Inserta filtros antes de `UsernamePasswordAuthenticationFilter` cuando corresponda.
- JWT: usa `ServicioJWT`; no dupliques lógica JWT en controladores.
- Semillas/demos: usa `InicializadorDatos` y evita semillas en producción (controla por perfil/properties si es necesario).

## Criterios de aceptación
- Build y arranque sin errores.
- Endpoints documentados en Swagger y probados (happy path + errores comunes).
- Validación de entrada con `jakarta.validation` y mensajes útiles.
- Manejo de errores claro (HTTP 4xx/5xx) sin exponer stacktraces sensibles.

## No hagas
- No hardcodees secretos/URLs de producción.
- No rompas contratos de DTO sin actualizar front/docs.
- No desactives seguridad o CORS de forma global en prod.

## Referencias rápidas
- Login sample body:
  {
    "nombreUsuarioOEmail": "admin",
    "contrasena": "Admin123!"
  }
- Registro sample body:
  {
    "nombre": "Nombre",
    "apellido": "Apellido",
    "email": "correo@dominio.com",
    "nombreUsuario": "usuario",
    "contrasena": "Password123!",
    "rol": "USUARIO"
  }
