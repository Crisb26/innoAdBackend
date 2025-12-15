# 🔐 GUÍA RÁPIDA: Implementación de Roles y Permisos

## 📋 Resumen Ejecutivo

Este documento es una **guía de referencia rápida** para implementar el sistema de roles y permisos en InnoAd. Para detalles completos, consulta los archivos de documentación:
- `CONSTANTES_ROLES_PERMISOS.java` - Enumeraciones y mapeos
- `GUIA_IMPLEMENTACION_BACKEND.java` - Paso a paso completo
- `SCRIPT_PRUEBAS_ROLES_PERMISOS.sql` - Inicialización de base de datos

---

## 🚀 Quick Start (5 minutos)

### 1. Copiar Constantes
```bash
# Crear archivo en src/main/java/com/innoad/core/
cp CONSTANTES_ROLES_PERMISOS.java src/main/java/com/innoad/core/constants/
```

### 2. Crear Entidades JPA
Copiar las clases desde `GUIA_IMPLEMENTACION_BACKEND.java`:
- `Usuario.java` - Actualizar con relación `@ManyToOne Rol`
- `Rol.java` - Nueva entidad
- `Permiso.java` - Nueva entidad

### 3. Inicializar Base de Datos
```bash
# Ejecutar script SQL en tu PostgreSQL
psql -U usuario -d innoad < SCRIPT_PRUEBAS_ROLES_PERMISOS.sql

# Verificar creación
psql -U usuario -d innoad -c "SELECT COUNT(*) FROM rol; SELECT COUNT(*) FROM permiso;"
```

### 4. Crear Servicios
- `RolService.java` - CRUD de roles y permisos
- `PermissionService.java` - Validación de permisos

### 5. Configurar Spring Security
- Actualizar `SecurityConfig.java`
- Habilitar `@EnableGlobalMethodSecurity`
- Configurar rutas protegidas por rol

### 6. Crear Controladores
- `RolController.java` - Endpoints `/api/roles`
- `PermissionController.java` - Endpoints `/api/permisos`

---

## 📊 Estructura de Roles

```
┌─ SUPER_ADMIN (Nivel 5)
│  └─ Todos los permisos ✓
├─ ADMIN (Nivel 4)
│  └─ Sistema, Usuarios, Campañas, Contenidos, Pantallas, Reportes
├─ GERENTE (Nivel 3)
│  └─ Campañas, Reportes, Estadísticas, Monitoreo
├─ OPERADOR (Nivel 2)
│  └─ Contenidos, Pantallas, Monitoreo
└─ USUARIO (Nivel 1)
   └─ Lectura: Campañas propias, Contenidos propios, Reportes propios
```

---

## 🔑 Permisos Principales (47 total)

### Sistema (8)
- `MODO_MANTENIMIENTO_VER`
- `MODO_MANTENIMIENTO_ACTIVAR`
- `CONFIGURACION_SISTEMA`
- `LOGS_AUDITORIA_VER`
- `LOGS_AUDITORIA_EXPORTAR`
- `BACKUPS_VER`, `BACKUPS_CREAR`, `BACKUPS_RESTAURAR`

### Usuarios (11)
- `USUARIOS_VER`, `USUARIOS_CREAR`, `USUARIOS_EDITAR`, `USUARIOS_ELIMINAR`
- `USUARIOS_CAMBIAR_ROL`, `USUARIOS_DESACTIVAR`, `USUARIOS_EXPORTAR`
- `PERFIL_VER_PROPIO`, `PERFIL_EDITAR_PROPIO`, `PERFIL_CAMBIAR_CONTRASENA`, `PERFIL_VER_OTROS`

### Campañas (9)
- `CAMPANAS_VER`, `CAMPANAS_VER_PROPIAS`, `CAMPANAS_CREAR`, `CAMPANAS_EDITAR`
- `CAMPANAS_ELIMINAR`, `CAMPANAS_PUBLICAR`, `CAMPANAS_DESPUBLICAR`
- `CAMPANAS_PROGRAMAR`, `CAMPANAS_CLONAR`

### Contenidos (10)
- `CONTENIDOS_VER`, `CONTENIDOS_VER_PROPIOS`, `CONTENIDOS_CREAR`, `CONTENIDOS_EDITAR`
- `CONTENIDOS_ELIMINAR`, `CONTENIDOS_SUBIR_MULTIMEDIA`, `CONTENIDOS_APROBAR`, `CONTENIDOS_RECHAZAR`
- `CONTENIDOS_VER_VERSIONES`, `CONTENIDOS_EXPORTAR`

### Pantallas (8)
- `PANTALLAS_VER`, `PANTALLAS_CREAR`, `PANTALLAS_EDITAR`, `PANTALLAS_ELIMINAR`
- `PANTALLAS_ASIGNAR_CONTENIDO`, `PANTALLAS_PROGRAMAR`, `PANTALLAS_MONITOREAR`, `PANTALLAS_CONTROL_REMOTO`

### Reportes (9)
- `REPORTES_VER`, `REPORTES_VER_PROPIOS`, `REPORTES_CREAR`, `REPORTES_PERSONALIZADO`
- `REPORTES_EXPORTAR_PDF`, `REPORTES_EXPORTAR_CSV`, `REPORTES_PROGRAMAR_ENVIO`
- `ESTADISTICAS_VER`, `ESTADISTICAS_TIEMPO_REAL`

### Integraciones (7)
- `INTEGRACIONES_VER`, `INTEGRACIONES_CREAR`, `INTEGRACIONES_EDITAR`, `INTEGRACIONES_ELIMINAR`
- `API_KEYS_VER`, `API_KEYS_CREAR`, `API_KEYS_REGENERAR`

---

## 🛠️ Checklist de Implementación

### Backend

- [ ] **Entidades JPA**
  - [ ] `Usuario.java` - Actualizar con `@ManyToOne Rol rol`
  - [ ] `Rol.java` - Crear nueva entidad
  - [ ] `Permiso.java` - Crear nueva entidad

- [ ] **Repositorios**
  - [ ] `RolRepository` - Métodos de búsqueda
  - [ ] `PermisoRepository` - Métodos de búsqueda
  - [ ] `UsuarioRepository` - Actualizar con JPA Specifications

- [ ] **Servicios**
  - [ ] `RolService` - CRUD completo (crear, actualizar, agregar/remover permisos)
  - [ ] `PermissionService` - Validación de permisos

- [ ] **Controladores**
  - [ ] `RolController` - Endpoints `/api/roles/**`
  - [ ] `PermissionController` - Endpoints `/api/permisos/**`

- [ ] **Seguridad**
  - [ ] `SecurityConfig.java` - Actualizar reglas de autorización
  - [ ] Anotación personalizada `@RequierePermiso`
  - [ ] `AuditoriaAspect.java` - Registrar cambios de permisos

- [ ] **Base de Datos**
  - [ ] Ejecutar `SCRIPT_PRUEBAS_ROLES_PERMISOS.sql`
  - [ ] Verificar 5 roles y 47 permisos creados
  - [ ] Crear usuarios de prueba

- [ ] **DTOs y Requests**
  - [ ] `RolDTO`
  - [ ] `PermisoDTO`
  - [ ] `CrearRolRequest`
  - [ ] `ActualizarRolRequest`
  - [ ] `ValidarPermisosRequest`

### Frontend

- [ ] **Servicios**
  - [ ] `ServicioPermisos` - Carga y validación de permisos

- [ ] **Guards**
  - [ ] `guardPermisos` - Protección de rutas por permiso

- [ ] **Directivas**
  - [ ] `*appPermiso` - Control condicional en templates

- [ ] **Pipes**
  - [ ] `tienePermiso` - Validación en templates

- [ ] **Interceptadores**
  - [ ] `AuditoriaInterceptor` - Logging de operaciones

- [ ] **Componentes**
  - [ ] `GestionRolesComponent` - CRUD de roles
  - [ ] `GestionPermisosComponent` - Asignación de permisos
  - [ ] Actualizar componentes existentes con validación de permisos

### Testing

- [ ] **Unit Tests**
  - [ ] Pruebas de `RolService` (crear, actualizar, agregar/remover permisos)
  - [ ] Pruebas de `PermissionService` (validación de permisos)
  - [ ] Pruebas de `guardPermisos` (rutas protegidas)

- [ ] **Pruebas Manuales**
  - [ ] Login con cada rol (SUPER_ADMIN, ADMIN, GERENTE, OPERADOR, USUARIO)
  - [ ] Verificar que cada rol ve solo sus opciones
  - [ ] Intentar acceder a rutas no autorizadas
  - [ ] Cambiar rol de usuario y verificar cambios inmediatos

- [ ] **Verificación de BD**
  - [ ] Query: ver todos los roles con permisos
  - [ ] Query: verificar permisos de usuario específico
  - [ ] Query: detectar usuarios sin rol asignado

---

## 🧪 Pruebas Rápidas

### Backend - Verificar Setup

```bash
# 1. Verificar entidades creadas
curl -X GET http://localhost:8080/api/roles \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"

# Respuesta esperada:
{
  "id": 1,
  "nombre": "SUPER_ADMIN",
  "descripcion": "Super Administrador",
  "nivelAcceso": 5,
  "permisos": [47 permisos]
}

# 2. Obtener permisos del usuario actual
curl -X GET http://localhost:8080/api/permisos/mis-permisos \
  -H "Authorization: Bearer $TOKEN"

# 3. Verificar permiso específico
curl -X GET http://localhost:8080/api/permisos/verificar/USUARIOS_VER \
  -H "Authorization: Bearer $TOKEN"
```

### Base de Datos - Verificar Datos

```sql
-- Ver estructura de roles
SELECT r.nombre, COUNT(p.id) as permisos 
FROM rol r 
LEFT JOIN rol_permiso rp ON r.id = rp.rol_id 
LEFT JOIN permiso p ON rp.permiso_id = p.id 
GROUP BY r.nombre 
ORDER BY r.nivel_acceso DESC;

-- Ver permisos de usuario
SELECT DISTINCT p.codigo 
FROM usuario u 
JOIN rol r ON u.rol_id = r.id 
JOIN rol_permiso rp ON r.id = rp.rol_id 
JOIN permiso p ON rp.permiso_id = p.id 
WHERE u.email = 'admin@innoad.com' 
ORDER BY p.codigo;
```

---

## ⚠️ Troubleshooting Común

### Problema: "Usuario sin rol asignado"

**Causa**: `rol_id` NULL en tabla usuario

**Solución**:
```sql
UPDATE usuario 
SET rol_id = (SELECT id FROM rol WHERE nombre = 'USUARIO')
WHERE rol_id IS NULL;
```

---

### Problema: "Permiso no reconocido"

**Causa**: Permiso no existe en tabla `permiso` o está inactivo

**Solución**:
```sql
-- Verificar permiso
SELECT * FROM permiso WHERE codigo = 'CODIGO_PERMISO';

-- Si no existe, insertar:
INSERT INTO permiso (codigo, descripcion, categoria, activo)
VALUES ('NUEVO_PERMISO', 'Descripción', 'Categoría', true);
```

---

### Problema: "AccessDeniedException al cambiar rol"

**Causa**: Usuario actual no tiene `USUARIOS_CAMBIAR_ROL`

**Solución**: 
- Verificar que usuario actual es SUPER_ADMIN o ADMIN
- O agregar permiso manualmente:
```sql
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id FROM rol r, permiso p
WHERE r.nombre = 'GERENTE' AND p.codigo = 'USUARIOS_CAMBIAR_ROL';
```

---

### Problema: "Query devuelve NULL para permisos"

**Causa**: Relaciones JPA no cargadas correctamente

**Solución**: Verificar `fetch = FetchType.EAGER` en:
```java
@ManyToOne(fetch = FetchType.EAGER)
private Rol rol;

@ManyToMany(fetch = FetchType.EAGER)
private Set<Permiso> permisos;
```

---

## 📚 Archivos de Referencia

| Archivo | Propósito | Líneas |
|---------|-----------|---------|
| `CONSTANTES_ROLES_PERMISOS.java` | Enumeraciones Java/TS y mapeo de permisos | 400+ |
| `GUIA_IMPLEMENTACION_BACKEND.java` | 9 pasos con código listo | 1200+ |
| `SCRIPT_PRUEBAS_ROLES_PERMISOS.sql` | Inicializar BD con datos de prueba | 500+ |
| `ARQUITECTURA_ROLES_PERMISOS.java` | Arquitectura estratégica completa | 627 |
| `GUIA_IMPLEMENTACION_PERMISOS.ts` | Frontend implementation patterns | 512 |

---

## 📞 Próximos Pasos

1. **Implementar Paso a Paso**
   - Seguir checklist anterior
   - Usar código de `GUIA_IMPLEMENTACION_BACKEND.java`
   - Ejecutar `SCRIPT_PRUEBAS_ROLES_PERMISOS.sql`

2. **Pruebas Completas**
   - Verificar cada rol × permiso
   - Test de rutas protegidas
   - Load test con múltiples usuarios

3. **Documentación de Producción**
   - Crear documentación de API (Swagger)
   - Documentar cambios de permisos
   - Crear guía de administración

4. **Monitoreo y Auditoría**
   - Implementar logs de cambios de rol
   - Monitorear accesos denegados
   - Auditoría de permisos críticos

---

## 🎯 Contacto y Soporte

- **Arquitecto del Sistema**: Ver `ARQUITECTURA_ROLES_PERMISOS.java`
- **Issues Comunes**: Ver sección Troubleshooting
- **Código Completo**: Ver `GUIA_IMPLEMENTACION_BACKEND.java`

**Status**: ✅ Documentación Fase 4 Completa

