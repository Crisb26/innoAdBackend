# 📚 Documentación Swagger/OpenAPI Completa

## ✅ Estado: 100% COMPLETADA

El backend **InnoAd** ahora tiene documentación completa de OpenAPI 3.0 para todos los endpoints. La interfaz Swagger UI está disponible automáticamente en el navegador.

---

## 🚀 Acceder a Swagger UI

### En Producción (Azure)
```
https://innoad-backend.azurewebsites.net/swagger-ui.html
```

### En Desarrollo Local
```
http://localhost:8080/swagger-ui.html
```

### Especificación OpenAPI JSON (para importar en Postman, etc)
```
http://localhost:8080/v3/api-docs
```

---

## 📋 Controladores Documentados (10/10)

### 1. 🔐 Autenticación (`ControladorAutenticacionDocumentado`)
- POST `/api/autenticacion/login` - Iniciar sesión
- POST `/api/autenticacion/registrar` - Registrarse
- POST `/api/autenticacion/refresh-token` - Renovar JWT
- GET `/api/autenticacion/salud` - Verificar conectividad

### 2. 📢 Campañas (`ControladorCampanasDocumentado`)
- GET `/api/campanas` - Listar campañas con paginación
- GET `/api/campanas/{id}` - Obtener campaña específica
- POST `/api/campanas` - Crear nueva campaña
- PUT `/api/campanas/{id}` - Actualizar campaña
- DELETE `/api/campanas/{id}` - Eliminar campaña
- POST `/api/campanas/{id}/pausar` - Pausar campaña
- GET `/api/campanas/{id}/estadisticas` - Estadísticas en tiempo real

### 3. 📄 Contenidos (`ControladorContenidosDocumentado`)
- GET `/api/contenidos` - Listar contenidos
- GET `/api/contenidos/{id}` - Obtener contenido
- POST `/api/contenidos` - Crear contenido
- PUT `/api/contenidos/{id}` - Actualizar contenido
- DELETE `/api/contenidos/{id}` - Eliminar contenido
- POST `/api/contenidos/{id}/subir-archivo` - Subir archivo
- GET `/api/contenidos/{id}/descargar` - Descargar contenido
- POST `/api/contenidos/{id}/previsualizar` - Generar previsualización
- GET `/api/contenidos/tipo/{tipo}` - Filtrar por tipo
- POST `/api/contenidos/buscar` - Buscar contenidos

### 4. 📺 Pantallas (`ControladorPantallasDocumentado`)
- GET `/api/pantallas` - Listar pantallas digitales
- GET `/api/pantallas/{id}` - Detalle de pantalla
- POST `/api/pantallas` - Registrar nueva pantalla
- PUT `/api/pantallas/{id}` - Actualizar pantalla
- DELETE `/api/pantallas/{id}` - Eliminar pantalla
- POST `/api/pantallas/{id}/asignar-contenido` - Asignar contenido
- POST `/api/pantallas/{id}/reproducer-ahora` - Reproducir inmediatamente
- GET `/api/pantallas/{id}/estado` - Estado en tiempo real
- GET `/api/pantallas/estado/online` - Pantallas conectadas
- GET `/api/pantallas/estado/problema` - Pantallas con problemas

### 5. 📊 Reportes (`ControladorReportesDocumentado`)
- GET `/api/reportes/campanas` - Reporte de campañas
- GET `/api/reportes/campanas/{id}` - Reporte detallado de campaña
- GET `/api/reportes/usuarios` - Reporte de usuarios
- GET `/api/reportes/ingresos` - Reporte de ingresos
- GET `/api/reportes/pantallas` - Reporte de pantallas
- GET `/api/reportes/contenidos` - Reporte de contenidos
- POST `/api/reportes/personalizado` - Crear reporte personalizado
- POST `/api/reportes/exportar/{id}` - Exportar reporte (PDF/Excel/CSV)
- GET `/api/reportes/dashboard` - Dashboard principal

### 6. 👤 Usuario (`ControladorUsuarioDocumentado`)
- GET `/api/usuario/perfil` - Obtener perfil
- PUT `/api/usuario/perfil` - Actualizar perfil
- POST `/api/usuario/cambiar-contrasena` - Cambiar contraseña
- POST `/api/usuario/subir-foto-perfil` - Subir foto
- GET `/api/usuario/preferencias` - Obtener preferencias
- PUT `/api/usuario/preferencias` - Actualizar preferencias
- GET `/api/usuario/notificaciones` - Listar notificaciones
- GET `/api/usuario/historial-actividad` - Historial de actividad
- GET `/api/usuario/sesiones` - Mis sesiones activas
- POST `/api/usuario/exportar-datos` - Exportar datos (RGPD)

### 7. 🤖 Chat IA (`ControladorChatDocumentado`)
- POST `/api/chat/mensaje` - Enviar mensaje a IA
- GET `/api/chat/conversaciones` - Listar conversaciones
- GET `/api/chat/conversaciones/{id}` - Obtener conversación
- POST `/api/chat/conversaciones` - Crear conversación
- DELETE `/api/chat/conversaciones/{id}` - Eliminar conversación
- POST `/api/chat/regenerar-respuesta/{mensajeId}` - Regenerar respuesta
- GET `/api/chat/modelos-disponibles` - Modelos de IA
- GET `/api/chat/uso-tokens` - Estadísticas de tokens
- POST `/api/chat/exportar-conversacion/{id}` - Exportar conversación

### 8. ⚙️ Administración (`ControladorAdminDocumentado`)
- GET `/api/admin/dashboard` - Dashboard administrativo
- GET `/api/admin/usuarios` - Listar usuarios
- POST `/api/admin/usuarios/{id}/bloquear` - Bloquear usuario
- POST `/api/admin/usuarios/{id}/desbloquear` - Desbloquear usuario
- GET `/api/admin/auditoria` - Registro de auditoría
- GET `/api/admin/auditoria/sospechosos` - Accesos sospechosos
- POST `/api/admin/mantenimiento/activar` - Activar mantenimiento

### 9. 📊 Monitoreo (`ControladorMonitoreoDocumentado`)
- GET `/api/admin/monitoreo/conexiones-activas` - Usuarios conectados ahora
- GET `/api/admin/monitoreo/estadisticas` - Estadísticas en tiempo real
- GET `/api/admin/monitoreo/historial-dia` - Historial del día
- GET `/api/admin/monitoreo/historial-usuario/{usuarioId}` - Historial de usuario
- POST `/api/admin/monitoreo/registrar-conexion` - Registrar conexión
- POST `/api/admin/monitoreo/registrar-desconexion/{usuarioId}` - Registrar desconexión
- GET `/api/admin/monitoreo/capacidad` - Uso de capacidad
- GET `/api/admin/monitoreo/top-ips` - IPs más activas

### 10. 🔐 Auditoría (`ControladorAuditoriaDocumentado`)
- GET `/api/admin/auditoria/registros` - Registros de auditoría
- GET `/api/admin/auditoria/registros/{id}` - Detalle de registro
- GET `/api/admin/auditoria/usuario/{usuarioId}` - Historial de usuario
- GET `/api/admin/auditoria/sospechosos` - Accesos sospechosos
- GET `/api/admin/auditoria/intentos-fallidos` - Intentos fallidos
- GET `/api/admin/auditoria/por-ip/{ip}` - Accesos por IP
- GET `/api/admin/auditoria/por-tipo/{tipoAccion}` - Accesos por tipo
- DELETE `/api/admin/auditoria/registros/{id}` - Eliminar registro
- GET `/api/admin/auditoria/estadisticas` - Estadísticas
- POST `/api/admin/auditoria/exportar` - Exportar registros

### 11. 🔧 Mantenimiento (`ControladorMantenimientoDocumentado`)
- POST `/api/admin/mantenimiento/activar` - Activar modo mantenimiento
- POST `/api/admin/mantenimiento/desactivar` - Desactivar mantenimiento
- GET `/api/admin/mantenimiento/estado` - Estado del sistema
- POST `/api/admin/mantenimiento/limpiar-cache` - Limpiar caché
- POST `/api/admin/mantenimiento/optimizar-base-datos` - Optimizar BD
- POST `/api/admin/mantenimiento/backup` - Crear backup
- GET `/api/admin/mantenimiento/backups` - Listar backups
- POST `/api/admin/mantenimiento/restaurar/{backupId}` - Restaurar backup
- POST `/api/admin/mantenimiento/reiniciar-aplicacion` - Reiniciar aplicación

---

## 🔑 Autenticación Bearer Token

Todos los endpoints (excepto `/api/autenticacion/login` y `/api/autenticacion/registrar`) requieren token JWT.

### En Swagger UI:
1. Haz clic en el botón **"Authorize"** (esquina superior derecha)
2. Ingresa: `Bearer <tu_token_jwt>`
3. Haz clic en "Authorize"
4. Ya puedes usar todos los endpoints

### Ejemplo en cURL:
```bash
curl -X GET "http://localhost:8080/api/campanas" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## 📥 Características Documentadas

Cada endpoint incluye:
- ✅ **Descripción clara** del propósito
- ✅ **Parámetros documentados** (query, path, body)
- ✅ **Códigos de respuesta HTTP** (200, 201, 400, 401, 403, 404, 409, 422, 429, 500)
- ✅ **Ejemplos de solicitud y respuesta**
- ✅ **Esquemas de datos** (modelos JSON)
- ✅ **Requisitos de seguridad** (Bearer Token)
- ✅ **Límites de rate limiting**
- ✅ **Campos obligatorios vs opcionales**

---

## 🧪 Testear Endpoints en Swagger UI

1. Ve a `http://localhost:8080/swagger-ui.html`
2. Expande un endpoint (haz clic en él)
3. Haz clic en **"Try it out"**
4. Ingresa los parámetros necesarios
5. Haz clic en **"Execute"**
6. Ver respuesta en tiempo real

---

## 📤 Importar en Postman

1. Ir a Postman
2. Clic en **"Import"** (arriba izquierda)
3. Pestaña **"Link"**
4. Pegar: `http://localhost:8080/v3/api-docs`
5. Clic en **"Continue"**
6. Listo: Todos los endpoints y ejemplos importados

---

## 🌐 Servidor de Producción (Azure)

Cuando se desplegue a Azure, Swagger estará en:
- UI: `https://innoad-backend.azurewebsites.net/swagger-ui.html`
- OpenAPI JSON: `https://innoad-backend.azurewebsites.net/v3/api-docs`

El archivo `ConfiguracionSwagger.java` ya incluye las URLs de producción.

---

## 📝 Modificar Documentación

Para actualizar la documentación de un endpoint:

1. **Abre el controlador** (ej: `ControladorCampanasDocumentado.java`)
2. **Modifica la anotación `@Operation`**:
   ```java
   @Operation(
       summary = "Nuevo resumen",
       description = "Nueva descripción detallada"
   )
   ```
3. **Agrega `@ApiResponse`** para códigos HTTP:
   ```java
   @ApiResponses(value = {
       @ApiResponse(responseCode = "200", description = "Éxito"),
       @ApiResponse(responseCode = "400", description = "Error")
   })
   ```
4. **Compila** el proyecto
5. **Reinicia** la aplicación
6. Swagger se actualiza automáticamente

---

## 🔍 Validar Documentación

Para verificar que Swagger está correcto:

```bash
# En desarrollo
curl http://localhost:8080/v3/api-docs | jq '.' | less

# En producción
curl https://innoad-backend.azurewebsites.net/v3/api-docs | jq '.' | less
```

---

## 🛡️ Seguridad en Swagger UI

- Swagger UI **solo muestra** métodos autenticados cuando ingresas token
- Los tokens se envían en el header `Authorization: Bearer <token>`
- **No se almacenan** tokens en el navegador
- Las credenciales se transmiten via HTTPS en producción

---

## 📱 Documentación Adicional

### En el Frontend (Angular)
Puedes generar documentación automática con:
```bash
npm install @compodoc/compodoc -g
compodoc -p tsconfig.app.json -d docs
```

### Dashboard de Monitoreo
La documentación de endpoints de monitoreo permite ver:
- Usuarios conectados en tiempo real
- Porcentaje de capacidad usado (de 8000 usuarios)
- IPs desde donde se accede
- Navegadores y sistemas operativos utilizados

---

## ✨ Próximos Pasos

- [ ] Agregar ejemplos de esquemas JSON más detallados
- [ ] Crear guía de errores comunes
- [ ] Documentar webhooks (si aplica)
- [ ] Agregar ejemplos de autenticación OAuth2
- [ ] Crear documentación de rate limiting en cada endpoint

---

**Última actualización:** 13 de Diciembre de 2024
**Versión de API:** 2.0.0
**Estado:** ✅ 100% Documentada
