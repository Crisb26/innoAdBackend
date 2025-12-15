/**
 * SCRIPT_PRUEBAS_ROLES_PERMISOS.sql
 * Queries para probar el sistema de roles y permisos en PostgreSQL
 */

-- ============================================================================
-- PASO 1: CREAR TABLAS (Si no existen)
-- ============================================================================

-- Tabla Permiso
CREATE TABLE IF NOT EXISTS permiso (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255) NOT NULL,
    categoria VARCHAR(50),
    activo BOOLEAN DEFAULT true,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla Rol
CREATE TABLE IF NOT EXISTS rol (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    nivel_acceso INTEGER NOT NULL,
    activo BOOLEAN DEFAULT true,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla Rol-Permiso (Relación Many-to-Many)
CREATE TABLE IF NOT EXISTS rol_permiso (
    rol_id INTEGER NOT NULL,
    permiso_id INTEGER NOT NULL,
    PRIMARY KEY (rol_id, permiso_id),
    FOREIGN KEY (rol_id) REFERENCES rol(id) ON DELETE CASCADE,
    FOREIGN KEY (permiso_id) REFERENCES permiso(id) ON DELETE CASCADE
);

-- Actualizar tabla Usuario existente
ALTER TABLE usuario ADD COLUMN IF NOT EXISTS rol_id INTEGER;
ALTER TABLE usuario ADD CONSTRAINT fk_usuario_rol 
    FOREIGN KEY (rol_id) REFERENCES rol(id);

-- ============================================================================
-- PASO 2: INSERTAR PERMISOS BÁSICOS
-- ============================================================================

-- SISTEMA
INSERT INTO permiso (codigo, descripcion, categoria, activo) VALUES
('MODO_MANTENIMIENTO_VER', 'Ver estado mantenimiento', 'Sistema', true),
('MODO_MANTENIMIENTO_ACTIVAR', 'Activar/desactivar mantenimiento', 'Sistema', true),
('CONFIGURACION_SISTEMA', 'Modificar configuración del sistema', 'Sistema', true),
('LOGS_AUDITORIA_VER', 'Ver logs de auditoría', 'Sistema', true),
('LOGS_AUDITORIA_EXPORTAR', 'Exportar logs de auditoría', 'Sistema', true),
('BACKUPS_VER', 'Ver backups', 'Sistema', true),
('BACKUPS_CREAR', 'Crear backups', 'Sistema', true),
('BACKUPS_RESTAURAR', 'Restaurar desde backup', 'Sistema', true)
ON CONFLICT (codigo) DO NOTHING;

-- ROLES Y PERMISOS
INSERT INTO permiso (codigo, descripcion, categoria, activo) VALUES
('ROLES_VER', 'Ver roles', 'Roles', true),
('ROLES_CREAR', 'Crear roles', 'Roles', true),
('ROLES_EDITAR', 'Editar roles', 'Roles', true),
('ROLES_ELIMINAR', 'Eliminar roles', 'Roles', true),
('PERMISOS_VER', 'Ver permisos disponibles', 'Roles', true),
('PERMISOS_ASIGNAR', 'Asignar permisos a roles', 'Roles', true)
ON CONFLICT (codigo) DO NOTHING;

-- USUARIOS
INSERT INTO permiso (codigo, descripcion, categoria, activo) VALUES
('USUARIOS_VER', 'Ver lista de usuarios', 'Usuarios', true),
('USUARIOS_CREAR', 'Crear nuevos usuarios', 'Usuarios', true),
('USUARIOS_EDITAR', 'Editar información de usuarios', 'Usuarios', true),
('USUARIOS_ELIMINAR', 'Eliminar usuarios', 'Usuarios', true),
('USUARIOS_CAMBIAR_ROL', 'Cambiar rol de usuario', 'Usuarios', true),
('USUARIOS_DESACTIVAR', 'Desactivar usuarios', 'Usuarios', true),
('USUARIOS_EXPORTAR', 'Exportar lista de usuarios', 'Usuarios', true),
('PERFIL_VER_PROPIO', 'Ver su propio perfil', 'Usuarios', true),
('PERFIL_EDITAR_PROPIO', 'Editar su propio perfil', 'Usuarios', true),
('PERFIL_CAMBIAR_CONTRASENA', 'Cambiar su propia contraseña', 'Usuarios', true),
('PERFIL_VER_OTROS', 'Ver perfiles de otros usuarios', 'Usuarios', true)
ON CONFLICT (codigo) DO NOTHING;

-- CAMPAÑAS
INSERT INTO permiso (codigo, descripcion, categoria, activo) VALUES
('CAMPANAS_VER', 'Ver campañas', 'Campañas', true),
('CAMPANAS_VER_PROPIAS', 'Ver sus propias campañas', 'Campañas', true),
('CAMPANAS_CREAR', 'Crear campañas', 'Campañas', true),
('CAMPANAS_EDITAR', 'Editar campañas', 'Campañas', true),
('CAMPANAS_ELIMINAR', 'Eliminar campañas', 'Campañas', true),
('CAMPANAS_PUBLICAR', 'Publicar campañas', 'Campañas', true),
('CAMPANAS_DESPUBLICAR', 'Despublicar campañas', 'Campañas', true),
('CAMPANAS_PROGRAMAR', 'Programar fechas de campañas', 'Campañas', true),
('CAMPANAS_CLONAR', 'Clonar campañas existentes', 'Campañas', true)
ON CONFLICT (codigo) DO NOTHING;

-- CONTENIDOS
INSERT INTO permiso (codigo, descripcion, categoria, activo) VALUES
('CONTENIDOS_VER', 'Ver contenidos', 'Contenidos', true),
('CONTENIDOS_VER_PROPIOS', 'Ver sus propios contenidos', 'Contenidos', true),
('CONTENIDOS_CREAR', 'Crear contenidos', 'Contenidos', true),
('CONTENIDOS_EDITAR', 'Editar contenidos', 'Contenidos', true),
('CONTENIDOS_ELIMINAR', 'Eliminar contenidos', 'Contenidos', true),
('CONTENIDOS_SUBIR_MULTIMEDIA', 'Subir archivos multimedia', 'Contenidos', true),
('CONTENIDOS_APROBAR', 'Aprobar contenidos', 'Contenidos', true),
('CONTENIDOS_RECHAZAR', 'Rechazar contenidos', 'Contenidos', true),
('CONTENIDOS_VER_VERSIONES', 'Ver versiones de contenidos', 'Contenidos', true),
('CONTENIDOS_EXPORTAR', 'Exportar contenidos', 'Contenidos', true)
ON CONFLICT (codigo) DO NOTHING;

-- PANTALLAS
INSERT INTO permiso (codigo, descripcion, categoria, activo) VALUES
('PANTALLAS_VER', 'Ver pantallas', 'Pantallas', true),
('PANTALLAS_CREAR', 'Crear pantallas', 'Pantallas', true),
('PANTALLAS_EDITAR', 'Editar pantallas', 'Pantallas', true),
('PANTALLAS_ELIMINAR', 'Eliminar pantallas', 'Pantallas', true),
('PANTALLAS_ASIGNAR_CONTENIDO', 'Asignar contenido a pantallas', 'Pantallas', true),
('PANTALLAS_PROGRAMAR', 'Programar pantallas', 'Pantallas', true),
('PANTALLAS_MONITOREAR', 'Monitorear pantallas', 'Pantallas', true),
('PANTALLAS_CONTROL_REMOTO', 'Control remoto de pantallas', 'Pantallas', true)
ON CONFLICT (codigo) DO NOTHING;

-- REPORTES
INSERT INTO permiso (codigo, descripcion, categoria, activo) VALUES
('REPORTES_VER', 'Ver reportes', 'Reportes', true),
('REPORTES_VER_PROPIOS', 'Ver sus propios reportes', 'Reportes', true),
('REPORTES_CREAR', 'Crear reportes', 'Reportes', true),
('REPORTES_PERSONALIZADO', 'Crear reportes personalizados', 'Reportes', true),
('REPORTES_EXPORTAR_PDF', 'Exportar reportes a PDF', 'Reportes', true),
('REPORTES_EXPORTAR_CSV', 'Exportar reportes a CSV', 'Reportes', true),
('REPORTES_PROGRAMAR_ENVIO', 'Programar envío de reportes', 'Reportes', true),
('ESTADISTICAS_VER', 'Ver estadísticas', 'Reportes', true),
('ESTADISTICAS_TIEMPO_REAL', 'Ver estadísticas en tiempo real', 'Reportes', true)
ON CONFLICT (codigo) DO NOTHING;

-- INTEGRACIONES
INSERT INTO permiso (codigo, descripcion, categoria, activo) VALUES
('INTEGRACIONES_VER', 'Ver integraciones', 'Integraciones', true),
('INTEGRACIONES_CREAR', 'Crear integraciones', 'Integraciones', true),
('INTEGRACIONES_EDITAR', 'Editar integraciones', 'Integraciones', true),
('INTEGRACIONES_ELIMINAR', 'Eliminar integraciones', 'Integraciones', true),
('API_KEYS_VER', 'Ver API keys', 'Integraciones', true),
('API_KEYS_CREAR', 'Crear API keys', 'Integraciones', true),
('API_KEYS_REGENERAR', 'Regenerar API keys', 'Integraciones', true)
ON CONFLICT (codigo) DO NOTHING;

-- ============================================================================
-- PASO 3: INSERTAR ROLES BÁSICOS
-- ============================================================================

-- SUPER_ADMIN (Nivel 5 - Todos los permisos)
INSERT INTO rol (nombre, descripcion, nivel_acceso, activo) VALUES
('SUPER_ADMIN', 'Super Administrador - Acceso completo', 5, true)
ON CONFLICT (nombre) DO NOTHING;

-- ADMIN (Nivel 4 - Casi todos excepto backup restaurar)
INSERT INTO rol (nombre, descripcion, nivel_acceso, activo) VALUES
('ADMIN', 'Administrador de Sistema', 4, true)
ON CONFLICT (nombre) DO NOTHING;

-- GERENTE (Nivel 3 - Campañas y Reportes)
INSERT INTO rol (nombre, descripcion, nivel_acceso, activo) VALUES
('GERENTE', 'Gerente de Campañas', 3, true)
ON CONFLICT (nombre) DO NOTHING;

-- OPERADOR (Nivel 2 - Contenidos y Pantallas)
INSERT INTO rol (nombre, descripcion, nivel_acceso, activo) VALUES
('OPERADOR', 'Operador de Contenidos', 2, true)
ON CONFLICT (nombre) DO NOTHING;

-- USUARIO (Nivel 1 - Lectura básica)
INSERT INTO rol (nombre, descripcion, nivel_acceso, activo) VALUES
('USUARIO', 'Usuario Estándar', 1, true)
ON CONFLICT (nombre) DO NOTHING;

-- ============================================================================
-- PASO 4: ASIGNAR PERMISOS A ROLES
-- ============================================================================

-- SUPER_ADMIN - Obtiene TODOS los permisos
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id 
FROM rol r, permiso p 
WHERE r.nombre = 'SUPER_ADMIN' 
  AND p.activo = true
ON CONFLICT DO NOTHING;

-- ADMIN - Todos excepto BACKUPS_RESTAURAR
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id 
FROM rol r, permiso p 
WHERE r.nombre = 'ADMIN' 
  AND p.activo = true 
  AND p.codigo != 'BACKUPS_RESTAURAR'
ON CONFLICT DO NOTHING;

-- GERENTE - Campañas, Reportes, Contenidos VER
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id 
FROM rol r, permiso p 
WHERE r.nombre = 'GERENTE' 
  AND p.activo = true 
  AND (
    p.codigo LIKE 'CAMPANAS%' 
    OR p.codigo LIKE 'REPORTES%' 
    OR p.codigo LIKE 'ESTADISTICAS%'
    OR p.codigo IN (
      'CONTENIDOS_VER', 'CONTENIDOS_VER_VERSIONES', 'CONTENIDOS_EXPORTAR',
      'PANTALLAS_VER', 'PANTALLAS_MONITOREAR',
      'PERFIL_VER_PROPIO', 'PERFIL_EDITAR_PROPIO', 'PERFIL_CAMBIAR_CONTRASENA'
    )
  )
ON CONFLICT DO NOTHING;

-- OPERADOR - Contenidos y Pantallas CRUD
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id 
FROM rol r, permiso p 
WHERE r.nombre = 'OPERADOR' 
  AND p.activo = true 
  AND (
    p.codigo LIKE 'CONTENIDOS%' 
    OR p.codigo LIKE 'PANTALLAS%'
    OR p.codigo IN (
      'PERFIL_VER_PROPIO', 'PERFIL_EDITAR_PROPIO', 'PERFIL_CAMBIAR_CONTRASENA',
      'CAMPANAS_VER',
      'REPORTES_VER_PROPIOS', 'ESTADISTICAS_VER'
    )
  )
ON CONFLICT DO NOTHING;

-- USUARIO - Permisos de lectura básicos
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id 
FROM rol r, permiso p 
WHERE r.nombre = 'USUARIO' 
  AND p.activo = true 
  AND (
    p.codigo LIKE '%_VER%' 
    OR p.codigo LIKE '%_EXPORTAR%'
    OR p.codigo IN (
      'PERFIL_VER_PROPIO', 'PERFIL_EDITAR_PROPIO', 'PERFIL_CAMBIAR_CONTRASENA',
      'CAMPANAS_VER_PROPIAS',
      'CONTENIDOS_VER_PROPIOS',
      'REPORTES_VER_PROPIOS',
      'ESTADISTICAS_TIEMPO_REAL'
    )
  )
ON CONFLICT DO NOTHING;

-- ============================================================================
-- PASO 5: CREAR USUARIOS DE PRUEBA CON ROLES
-- ============================================================================

-- Usuario Super Admin
INSERT INTO usuario (email, contrasena, nombre, apellido, activo, rol_id, fecha_creacion, fecha_actualizacion)
SELECT 
  'superadmin@innoad.com',
  '$2a$12$..hash_bcrypt_password..', -- Cambiar con hash real
  'Super',
  'Admin',
  true,
  r.id,
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP
FROM rol r
WHERE r.nombre = 'SUPER_ADMIN'
ON CONFLICT (email) DO NOTHING;

-- Usuario Admin
INSERT INTO usuario (email, contrasena, nombre, apellido, activo, rol_id, fecha_creacion, fecha_actualizacion)
SELECT 
  'admin@innoad.com',
  '$2a$12$..hash_bcrypt_password..', -- Cambiar con hash real
  'Admin',
  'System',
  true,
  r.id,
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP
FROM rol r
WHERE r.nombre = 'ADMIN'
ON CONFLICT (email) DO NOTHING;

-- Usuario Gerente
INSERT INTO usuario (email, contrasena, nombre, apellido, activo, rol_id, fecha_creacion, fecha_actualizacion)
SELECT 
  'gerente@innoad.com',
  '$2a$12$..hash_bcrypt_password..', -- Cambiar con hash real
  'Juan',
  'Gerente',
  true,
  r.id,
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP
FROM rol r
WHERE r.nombre = 'GERENTE'
ON CONFLICT (email) DO NOTHING;

-- Usuario Operador
INSERT INTO usuario (email, contrasena, nombre, apellido, activo, rol_id, fecha_creacion, fecha_actualizacion)
SELECT 
  'operador@innoad.com',
  '$2a$12$..hash_bcrypt_password..', -- Cambiar con hash real
  'Carlos',
  'Operador',
  true,
  r.id,
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP
FROM rol r
WHERE r.nombre = 'OPERADOR'
ON CONFLICT (email) DO NOTHING;

-- Usuario Estándar
INSERT INTO usuario (email, contrasena, nombre, apellido, activo, rol_id, fecha_creacion, fecha_actualizacion)
SELECT 
  'usuario@innoad.com',
  '$2a$12$..hash_bcrypt_password..', -- Cambiar con hash real
  'Pedro',
  'Usuario',
  true,
  r.id,
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP
FROM rol r
WHERE r.nombre = 'USUARIO'
ON CONFLICT (email) DO NOTHING;

-- ============================================================================
-- PASO 6: QUERIES PARA VERIFICAR DATOS
-- ============================================================================

-- Ver todos los roles con su cantidad de permisos
SELECT 
  r.nombre,
  r.descripcion,
  r.nivel_acceso,
  COUNT(p.id) as cantidad_permisos,
  r.activo
FROM rol r
LEFT JOIN rol_permiso rp ON r.id = rp.rol_id
LEFT JOIN permiso p ON rp.permiso_id = p.id
WHERE r.activo = true
GROUP BY r.id, r.nombre, r.descripcion, r.nivel_acceso, r.activo
ORDER BY r.nivel_acceso DESC;

-- Ver permisos de un rol específico
SELECT 
  p.codigo,
  p.descripcion,
  p.categoria
FROM rol r
JOIN rol_permiso rp ON r.id = rp.rol_id
JOIN permiso p ON rp.permiso_id = p.id
WHERE r.nombre = 'GERENTE'
  AND p.activo = true
ORDER BY p.categoria, p.codigo;

-- Ver usuarios y sus roles
SELECT 
  u.email,
  u.nombre,
  u.apellido,
  r.nombre as rol,
  r.nivel_acceso,
  COUNT(p.id) as total_permisos
FROM usuario u
LEFT JOIN rol r ON u.rol_id = r.id
LEFT JOIN rol_permiso rp ON r.id = rp.rol_id
LEFT JOIN permiso p ON rp.permiso_id = p.id
WHERE u.activo = true
GROUP BY u.id, u.email, u.nombre, u.apellido, r.nombre, r.nivel_acceso
ORDER BY r.nivel_acceso DESC, u.email;

-- Ver permisos de un usuario específico
SELECT 
  p.codigo,
  p.descripcion,
  p.categoria
FROM usuario u
JOIN rol r ON u.rol_id = r.id
JOIN rol_permiso rp ON r.id = rp.rol_id
JOIN permiso p ON rp.permiso_id = p.id
WHERE u.email = 'gerente@innoad.com'
  AND p.activo = true
ORDER BY p.categoria, p.codigo;

-- Verificar si usuario tiene permiso específico
SELECT 
  u.email,
  u.nombre,
  CASE 
    WHEN COUNT(p.id) > 0 THEN 'SÍ - Tiene permiso'
    ELSE 'NO - Sin permiso'
  END as tiene_permiso_campanas_crear
FROM usuario u
LEFT JOIN rol r ON u.rol_id = r.id
LEFT JOIN rol_permiso rp ON r.id = rp.rol_id
LEFT JOIN permiso p ON rp.permiso_id = p.id AND p.codigo = 'CAMPANAS_CREAR'
WHERE u.email = 'gerente@innoad.com'
GROUP BY u.id, u.email, u.nombre;

-- Ver permisos por categoría
SELECT 
  p.categoria,
  COUNT(*) as total_permisos,
  STRING_AGG(p.codigo, ', ' ORDER BY p.codigo) as permisos
FROM permiso p
WHERE p.activo = true
GROUP BY p.categoria
ORDER BY p.categoria;

-- ============================================================================
-- PASO 7: OPERACIONES DE MANTENIMIENTO
-- ============================================================================

-- Cambiar rol de usuario
UPDATE usuario 
SET rol_id = (SELECT id FROM rol WHERE nombre = 'OPERADOR')
WHERE email = 'usuario@innoad.com';

-- Asignar permiso adicional a un rol existente
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT 
  (SELECT id FROM rol WHERE nombre = 'USUARIO'),
  (SELECT id FROM permiso WHERE codigo = 'CONTENIDOS_CREAR')
WHERE NOT EXISTS (
  SELECT 1 FROM rol_permiso rp
  WHERE rp.rol_id = (SELECT id FROM rol WHERE nombre = 'USUARIO')
    AND rp.permiso_id = (SELECT id FROM permiso WHERE codigo = 'CONTENIDOS_CREAR')
);

-- Remover permiso de un rol
DELETE FROM rol_permiso
WHERE rol_id = (SELECT id FROM rol WHERE nombre = 'USUARIO')
  AND permiso_id = (SELECT id FROM permiso WHERE codigo = 'CONTENIDOS_CREAR');

-- Desactivar rol (sin eliminar)
UPDATE rol 
SET activo = false, fecha_actualizacion = CURRENT_TIMESTAMP
WHERE nombre = 'USUARIO';

-- Desactivar usuario
UPDATE usuario 
SET activo = false
WHERE email = 'usuario@innoad.com';

-- ============================================================================
-- PASO 8: QUERIES DE ANÁLISIS Y AUDITORÍA
-- ============================================================================

-- Usuarios sin rol asignado (potencial problema)
SELECT email, nombre, apellido, fecha_creacion
FROM usuario
WHERE rol_id IS NULL
  AND activo = true;

-- Roles con pocos permisos (análisis)
SELECT 
  r.nombre,
  COUNT(p.id) as cantidad_permisos
FROM rol r
LEFT JOIN rol_permiso rp ON r.id = rp.rol_id
LEFT JOIN permiso p ON rp.permiso_id = p.id AND p.activo = true
WHERE r.activo = true
GROUP BY r.id, r.nombre
HAVING COUNT(p.id) < 5
ORDER BY cantidad_permisos ASC;

-- Historial de cambios de rol (si existe tabla auditoria)
-- SELECT * FROM auditoria 
-- WHERE accion LIKE '%USUARIOS_CAMBIAR_ROL%'
-- ORDER BY timestamp DESC
-- LIMIT 20;

