# 🚀 Instrucciones para Iniciar InnoAd Backend

## ✅ VERIFICACIÓN COMPLETADA

El backend ha sido compilado y probado exitosamente. Todos los sistemas están operativos.

---

## 🎯 INICIO RÁPIDO

### 1. Iniciar el Backend

```bash
cd "c:\Users\bueno\Desktop\PROYECTO FINAL INNOAD\innoadBackend"
mvn spring-boot:run
```

**Tiempo de inicio:** ~45 segundos

### 2. Verificar que Esté Funcionando

El backend estará disponible en:
```
http://localhost:8081
```

Verifica el health check:
```
http://localhost:8081/api/v1/raspberry/health
```

Respuesta esperada:
```json
{
  "exitoso": true,
  "mensaje": "Servidor funcionando correctamente",
  "datos": {
    "status": "UP",
    "service": "InnoAd Backend",
    "version": "2.0.0"
  }
}
```

### 3. Acceder a la Documentación Swagger

```
http://localhost:8081/swagger-ui.html
```

### 4. Acceder a la Consola H2 (Base de Datos)

```
http://localhost:8081/h2-console
```

**Credenciales:**
- JDBC URL: `jdbc:h2:mem:innoad_db`
- Username: `sa`
- Password: *(dejar vacío)*

---

## 👥 USUARIOS POR DEFECTO

El sistema crea automáticamente 4 usuarios para desarrollo:

| Usuario | Email | Contraseña | Rol |
|---------|-------|------------|-----|
| `admin` | admin@innoad.com | `Admin123!` | ADMINISTRADOR |
| `tecnico` | tecnico@innoad.com | `Tecnico123!` | TECNICO |
| `developer` | dev@innoad.com | `Dev123!` | DESARROLLADOR |
| `usuario` | usuario@innoad.com | `Usuario123!` | USUARIO |

**Nota:** Estos usuarios están verificados automáticamente para facilitar las pruebas.

---

## 🧪 PROBAR LOS ENDPOINTS

### Ejemplo 1: Login

```bash
curl -X POST http://localhost:8081/api/v1/autenticacion/iniciar-sesion \
  -H "Content-Type: application/json" \
  -d "{\"nombreUsuarioOEmail\":\"admin\",\"contrasena\":\"Admin123!\"}"
```

### Ejemplo 2: Crear Pantalla (requiere token)

```bash
curl -X POST http://localhost:8081/api/v1/pantallas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TU_TOKEN_AQUI>" \
  -d "{\"nombre\":\"Pantalla Test\",\"ubicacion\":\"Oficina\",\"resolucion\":\"1920x1080\",\"orientacion\":\"HORIZONTAL\"}"
```

### Ejemplo 3: Listar Mis Pantallas

```bash
curl -X GET http://localhost:8081/api/v1/pantallas \
  -H "Authorization: Bearer <TU_TOKEN_AQUI>"
```

### Ejemplo 4: Sincronizar desde Raspberry Pi (público)

```bash
curl -X GET "http://localhost:8081/api/v1/raspberry/sincronizar/PI-A1B2C3D4?direccionIp=192.168.1.100"
```

---

## 📂 ESTRUCTURA DE DIRECTORIOS

```
innoadBackend/
├── src/
│   └── main/
│       ├── java/com/innoad/
│       │   ├── controlador/          # 5 controladores (35+ endpoints)
│       │   │   ├── ControladorAutenticacion.java
│       │   │   ├── ControladorPantalla.java
│       │   │   ├── ControladorContenido.java
│       │   │   └── ControladorRaspberryPi.java
│       │   │
│       │   ├── servicio/             # 10 servicios
│       │   │   ├── ServicioAutenticacion.java
│       │   │   ├── ServicioPantalla.java
│       │   │   ├── ServicioContenido.java
│       │   │   ├── ServicioAlmacenamiento.java
│       │   │   ├── ServicioJWT.java
│       │   │   ├── ServicioEmail.java
│       │   │   └── ServicioAgenteIA.java
│       │   │
│       │   ├── modelo/               # 6 entidades
│       │   │   └── entidades/
│       │   │       ├── Usuario.java
│       │   │       ├── Pantalla.java (NUEVO)
│       │   │       ├── Contenido.java (NUEVO)
│       │   │       ├── Publicidad.java
│       │   │       ├── ConversacionIA.java
│       │   │       └── ConfiguracionSistema.java
│       │   │
│       │   ├── repositorio/          # 6 repositorios
│       │   │   ├── RepositorioUsuario.java
│       │   │   ├── RepositorioPantalla.java (NUEVO)
│       │   │   ├── RepositorioContenido.java (NUEVO)
│       │   │   └── ...
│       │   │
│       │   ├── dto/                  # 13 DTOs
│       │   │   ├── solicitud/        # 8 DTOs de request
│       │   │   └── respuesta/        # 5 DTOs de response
│       │   │
│       │   └── configuracion/        # Configuraciones
│       │       ├── ConfiguracionSeguridad.java (MODIFICADO)
│       │       └── ConfiguracionWeb.java (NUEVO)
│       │
│       └── resources/
│           └── application.yml       # Configuración principal
│
├── uploads/                          # Archivos subidos (se crea automáticamente)
│
├── DOCUMENTACION-APIS-COMPLETA.md   # 📚 Documentación completa
├── RESUMEN-IMPLEMENTACION-COMPLETA.md # 📋 Resumen de cambios
└── INSTRUCCIONES-INICIO.md          # 🚀 Este archivo
```

---

## 🔧 CONFIGURACIÓN OPCIONAL

### Cambiar Puerto

Edita `application.yml`:
```yaml
server:
  port: 8081  # Cambiar a tu puerto deseado
```

### Configurar Email Real

Edita `application.yml`:
```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: tu-email@gmail.com
    password: tu-password-de-aplicacion  # Usar App Password de Gmail
```

### Usar PostgreSQL en lugar de H2

1. Instalar PostgreSQL
2. Crear base de datos: `innoad_db`
3. Editar `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/innoad_db
    username: tu_usuario
    password: tu_password
  jpa:
    hibernate:
      ddl-auto: update  # Cambiar de create-drop a update
```

---

## 📁 DIRECTORIO DE UPLOADS

Los archivos subidos (imágenes, videos) se almacenan en:
```
c:\Users\bueno\Desktop\PROYECTO FINAL INNOAD\innoadBackend\uploads\
```

Este directorio se crea automáticamente al subir el primer archivo.

**URLs públicas de archivos:**
```
http://localhost:8081/uploads/{nombre-archivo}
```

---

## 🐛 SOLUCIÓN DE PROBLEMAS

### Error: Puerto 8081 ya en uso

```bash
# Windows: Encontrar y matar proceso en puerto 8081
netstat -ano | findstr :8081
taskkill /PID <PID> /F
```

### Error: No se puede conectar a la base de datos

- Verifica que no haya otro proceso usando H2
- Reinicia el backend
- Si persiste, elimina el directorio `target/` y recompila

### Error: Email no se envía

- Verifica configuración en `application.yml`
- Para Gmail, usa "App Password" en lugar de tu contraseña normal
- Los emails de verificación son opcionales; el sistema funciona sin email

### Advertencias de Lombok en compilación

Las advertencias sobre `@Builder.Default` son normales y no afectan el funcionamiento. Puedes ignorarlas.

---

## 📊 MONITOREO

### Endpoints de Actuator

- Health: `http://localhost:8081/actuator/health`
- Info: `http://localhost:8081/actuator/info`
- Metrics: `http://localhost:8081/actuator/metrics`

### Logs

Los logs se muestran en la consola. Para guardarlos en archivo, agrega a `application.yml`:

```yaml
logging:
  file:
    name: logs/innoad.log
  level:
    com.innoad: DEBUG
```

---

## 🎯 ENDPOINTS PRINCIPALES

### Para Testing Manual

1. **Health Check:** `GET /api/v1/raspberry/health`
2. **Login:** `POST /api/v1/autenticacion/iniciar-sesion`
3. **Registrarse:** `POST /api/v1/autenticacion/registrarse`
4. **Crear Pantalla:** `POST /api/v1/pantallas`
5. **Crear Contenido:** `POST /api/v1/contenidos`
6. **Sincronizar (Raspberry):** `GET /api/v1/raspberry/sincronizar/{codigo}`

Ver documentación completa en: `DOCUMENTACION-APIS-COMPLETA.md`

---

## ✅ CHECKLIST DE INICIO

- [ ] Backend compilado: `mvn clean compile`
- [ ] Backend iniciado: `mvn spring-boot:run`
- [ ] Health check responde: `http://localhost:8081/api/v1/raspberry/health`
- [ ] Swagger accesible: `http://localhost:8081/swagger-ui.html`
- [ ] Login funciona con usuario `admin` / `Admin123!`
- [ ] Puedes crear una pantalla
- [ ] Puedes crear un contenido

---

## 🚀 PRÓXIMOS PASOS

1. **Probar desde Frontend**
   - Configurar `apiUrl` en frontend: `http://localhost:8081/api/v1`
   - Probar login
   - Probar registro público
   - Probar creación de pantallas y contenidos

2. **Configurar Raspberry Pi**
   - Obtener código de pantalla desde el sistema
   - Instalar player en Raspberry Pi
   - Configurar sincronización automática

3. **Producción**
   - Cambiar a PostgreSQL
   - Configurar email real
   - Configurar variables de entorno
   - Cambiar JWT secret
   - Desplegar en servidor

---

## 📞 DOCUMENTACIÓN ADICIONAL

- **APIs Completas:** Ver `DOCUMENTACION-APIS-COMPLETA.md`
- **Resumen de Cambios:** Ver `RESUMEN-IMPLEMENTACION-COMPLETA.md`
- **Swagger UI:** `http://localhost:8081/swagger-ui.html`
- **H2 Console:** `http://localhost:8081/h2-console`

---

## 🎉 ¡TODO LISTO!

El backend está completamente funcional y listo para usar. Puedes empezar a:

1. ✅ Registrar usuarios desde el formulario público
2. ✅ Crear pantallas para Raspberry Pi
3. ✅ Publicar contenidos (texto, HTML, imágenes, videos)
4. ✅ Sincronizar contenidos desde Raspberry Pi
5. ✅ Recuperar contraseñas por email

**Desarrollado con ❤️ para InnoAd**
**Versión:** 2.0.0
**Fecha:** 4 de Noviembre de 2025
