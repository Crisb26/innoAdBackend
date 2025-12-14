# 🎯 GUÍA RÁPIDA: USAR SWAGGER UI

## 🚀 Acceso Rápido

**En Desarrollo Local:**
```
http://localhost:8080/swagger-ui.html
```

**En Producción (Azure):**
```
https://innoad-backend.azurewebsites.net/swagger-ui.html
```

---

## 📋 Paso 1: Obtener Token JWT

1. Expande **"🔐 Autenticación"**
2. Haz clic en **POST /api/autenticacion/login**
3. Clic en **"Try it out"**
4. Ingresa en el body:
```json
{
  "email": "usuario@example.com",
  "contrasena": "Password123!"
}
```
5. Clic en **"Execute"**
6. Copia el token del response (campo `token`)

---

## 🔐 Paso 2: Autorizar Token

1. Clic en botón **"Authorize"** (arriba a la derecha)
2. En el campo de texto, pega:
```
Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```
3. Clic en **"Authorize"**
4. Clic en **"Close"**

**Ahora todos los endpoints están autorizados**

---

## ✅ Paso 3: Probar Endpoints

### Ejemplo: Listar Campañas

1. Expande **"📢 Campañas"**
2. Clic en **GET /api/campanas**
3. Clic en **"Try it out"**
4. Parámetros opcionales:
   - `page`: 0 (primera página)
   - `size`: 20 (20 items por página)
   - `estado`: ACTIVA, PAUSADA, FINALIZADA
5. Clic en **"Execute"**
6. Ver respuesta en tiempo real

### Ejemplo: Crear Campaña

1. Expande **"📢 Campañas"**
2. Clic en **POST /api/campanas**
3. Clic en **"Try it out"**
4. En el body (JSON), ingresa:
```json
{
  "nombre": "Mi Campaña",
  "descripcion": "Campaña de prueba",
  "estado": "ACTIVA",
  "fechaInicio": "2024-12-13",
  "fechaFin": "2024-12-31"
}
```
5. Clic en **"Execute"**

### Ejemplo: Monitoreo en Vivo

1. Expande **"📊 Monitoreo"**
2. Clic en **GET /api/admin/monitoreo/estadisticas**
3. Clic en **"Try it out"**
4. Clic en **"Execute"**
5. Ver usuarios conectados ahora, % capacidad, etc
6. **Recarga cada 3 segundos** para ver actualizaciones

---

## 🎯 Endpoints Frecuentes

### Gestión de Campañas
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/campanas` | Listar todas |
| GET | `/api/campanas/{id}` | Detalle de una |
| POST | `/api/campanas` | Crear nueva |
| PUT | `/api/campanas/{id}` | Actualizar |
| DELETE | `/api/campanas/{id}` | Eliminar |
| POST | `/api/campanas/{id}/pausar` | Pausar campaña |

### Monitoreo y Seguridad
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/admin/monitoreo/conexiones-activas` | Usuarios ahora |
| GET | `/api/admin/monitoreo/estadisticas` | Métricas en vivo |
| GET | `/api/admin/auditoria/registros` | Ver accesos |
| GET | `/api/admin/auditoria/sospechosos` | Accesos sospechosos |
| POST | `/api/usuario/cambiar-contrasena` | Cambiar contraseña |

### Usuario
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/usuario/perfil` | Mi perfil |
| PUT | `/api/usuario/perfil` | Editar perfil |
| GET | `/api/usuario/notificaciones` | Mis notificaciones |
| GET | `/api/usuario/historial-actividad` | Mis acciones |

---

## 🔍 Interpretar Respuestas

### Código 200 (OK)
✅ Solicitud exitosa

Respuesta de ejemplo:
```json
{
  "id": 1,
  "nombre": "Campaña de Navidad",
  "estado": "ACTIVA",
  "impresiones": 5000
}
```

### Código 201 (Created)
✅ Recurso creado exitosamente

```json
{
  "id": 123,
  "mensaje": "Campaña creada"
}
```

### Código 400 (Bad Request)
❌ Datos inválidos

```json
{
  "error": "El nombre es obligatorio"
}
```

### Código 401 (Unauthorized)
❌ Token JWT inválido o expirado

**Solución:** Repite Paso 1 y 2

### Código 403 (Forbidden)
❌ No tienes permisos para esta acción

```json
{
  "error": "Requiere rol ADMIN"
}
```

### Código 404 (Not Found)
❌ Recurso no existe

```json
{
  "error": "Campaña con ID 999 no encontrada"
}
```

### Código 429 (Too Many Requests)
❌ Excediste límite de rate limiting

- 5 logins por IP cada 15 minutos
- 100 API requests por minuto por usuario

**Solución:** Espera 15 minutos o usa otra IP

---

## 💡 Consejos Prácticos

### 1️⃣ Descargar OpenAPI JSON
```
GET /v3/api-docs
```
Copia completa en archivo JSON para usar en otras herramientas

### 2️⃣ Probar sin Autenticación
Los endpoints de autenticación **no requieren token**:
- POST `/api/autenticacion/login`
- POST `/api/autenticacion/registrar`
- GET `/api/autenticacion/salud`

### 3️⃣ Parámetros Opcionales
Si un parámetro está marcado con ⭐, es obligatorio.
Los demás son opcionales.

### 4️⃣ Búsqueda y Filtrado
```
GET /api/campanas?page=0&size=10&estado=ACTIVA
```

### 5️⃣ Ordenamiento
```
GET /api/reportes/campanas?ordenar=fecha_desc
```

---

## 🔄 Flujo Completo Ejemplo

**Objetivo:** Crear una campaña, asignarle contenido y verla en monitoreo

1. **Login:**
   - POST `/api/autenticacion/login`
   - Obtener token JWT

2. **Autorizar:**
   - Clic en "Authorize"
   - Pegar token

3. **Crear Campaña:**
   - POST `/api/campanas`
   - Body con nombre, descripción, etc
   - Guardar el `id` devuelto

4. **Subir Contenido:**
   - POST `/api/contenidos/{id}/subir-archivo`
   - Seleccionar imagen/video

5. **Asignar a Pantalla:**
   - POST `/api/pantallas/{id}/asignar-contenido`
   - Con ID de campaña

6. **Ver en Monitoreo:**
   - GET `/api/admin/monitoreo/estadisticas`
   - Verás la campaña active

---

## 🚨 Resolución de Errores Comunes

### "401 Unauthorized"
- ❌ Token expirado o inválido
- ✅ Solución: Obtén nuevo token (Paso 1)

### "403 Forbidden"
- ❌ No tienes rol ADMIN
- ✅ Solución: Usa usuario administrador

### "404 Not Found"
- ❌ ID de recurso no existe
- ✅ Solución: Copia ID correcto del listado

### "400 Bad Request"
- ❌ JSON inválido o campos faltantes
- ✅ Solución: Revisa mensaje de error y corrige

### "429 Too Many Requests"
- ❌ Excediste rate limit
- ✅ Solución: Espera o usa otra IP

---

## 📱 Desde Postman (Alternativa)

Si prefieres Postman en lugar de Swagger UI:

1. Abre Postman
2. Import → Link
3. Pega: `http://localhost:8080/v3/api-docs`
4. ✅ Todos los endpoints importados
5. Configura variables para token y URL base

---

## 🎬 Video Demostración (Pasos)

```
1. Abrir Swagger: http://localhost:8080/swagger-ui.html
2. Login: POST /api/autenticacion/login
3. Copiar token
4. Authorize: Pegar token
5. Probar: GET /api/campanas
6. Ver respuesta en tiempo real
7. Monitoreo: GET /api/admin/monitoreo/estadisticas
8. Crear: POST /api/campanas con datos
9. Actualizar: PUT /api/campanas/{id}
10. Eliminar: DELETE /api/campanas/{id}
```

---

## ✨ Características Premium de Swagger UI

- ✅ **Syntax highlighting** para JSON
- ✅ **Try it out** - Ejecutar endpoints directamente
- ✅ **Request/Response** - Ver detalles completos
- ✅ **Status codes** - Ver todos los códigos posibles
- ✅ **Schemas** - Ver estructura de datos
- ✅ **Authorization** - Guardar token automáticamente
- ✅ **Search** - Buscar endpoints por nombre
- ✅ **Download OpenAPI spec** - Para otras herramientas

---

**¿Necesitas ayuda?** Consulta SWAGGER_DOCUMENTACION_COMPLETA.md

**Última actualización:** 13 de Diciembre de 2024
