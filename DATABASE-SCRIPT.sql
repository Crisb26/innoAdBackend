-- Base: innoad_db
-- Usuario: postgres
-- Password: Cris930226**
-- Puerto: 5432

DROP DATABASE IF EXISTS innoad_db;
CREATE DATABASE innoad_db
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'C'
    LC_CTYPE = 'C'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

\c innoad_db

-- Extensiones
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Tablas

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    nombre_usuario VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    avatar_url VARCHAR(500),
    activo BOOLEAN DEFAULT TRUE,
    verificado BOOLEAN DEFAULT FALSE,
    ultimo_acceso TIMESTAMP,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    creado_por BIGINT,
    actualizado_por BIGINT
);

CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL,
    descripcion VARCHAR(255),
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS permisos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL,
    descripcion VARCHAR(255),
    modulo VARCHAR(50) NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS usuario_roles (
    usuario_id BIGINT NOT NULL,
    rol_id BIGINT NOT NULL,
    fecha_asignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (usuario_id, rol_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (rol_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS rol_permisos (
    rol_id BIGINT NOT NULL,
    permiso_id BIGINT NOT NULL,
    fecha_asignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (rol_id, permiso_id),
    FOREIGN KEY (rol_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permiso_id) REFERENCES permisos(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS campanas (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin TIMESTAMP NOT NULL,
    presupuesto DECIMAL(15,2),
    estado VARCHAR(20) DEFAULT 'borrador',
    prioridad INTEGER DEFAULT 1,
    activa BOOLEAN DEFAULT TRUE,
    usuario_id BIGINT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS contenidos (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    descripcion TEXT,
    tipo VARCHAR(50) NOT NULL,
    url VARCHAR(500) NOT NULL,
    duracion INTEGER DEFAULT 10,
    tamano BIGINT,
    resolucion VARCHAR(20),
    formato VARCHAR(20),
    activo BOOLEAN DEFAULT TRUE,
    aprobado BOOLEAN DEFAULT FALSE,
    usuario_id BIGINT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS campana_contenidos (
    campana_id BIGINT NOT NULL,
    contenido_id BIGINT NOT NULL,
    orden INTEGER DEFAULT 1,
    duracion_segundos INTEGER DEFAULT 10,
    fecha_asignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (campana_id, contenido_id),
    FOREIGN KEY (campana_id) REFERENCES campanas(id) ON DELETE CASCADE,
    FOREIGN KEY (contenido_id) REFERENCES contenidos(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS pantallas (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    codigo_unico VARCHAR(50) UNIQUE NOT NULL,
    ubicacion VARCHAR(255),
    latitud DECIMAL(10,8),
    longitud DECIMAL(11,8),
    resolucion VARCHAR(20),
    orientacion VARCHAR(20) DEFAULT 'horizontal',
    estado VARCHAR(20) DEFAULT 'inactiva',
    activa BOOLEAN DEFAULT TRUE,
    ultima_conexion TIMESTAMP,
    usuario_id BIGINT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS campana_pantallas (
    campana_id BIGINT NOT NULL,
    pantalla_id BIGINT NOT NULL,
    fecha_asignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (campana_id, pantalla_id),
    FOREIGN KEY (campana_id) REFERENCES campanas(id) ON DELETE CASCADE,
    FOREIGN KEY (pantalla_id) REFERENCES pantallas(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS estadisticas (
    id BIGSERIAL PRIMARY KEY,
    campana_id BIGINT NOT NULL,
    contenido_id BIGINT,
    pantalla_id BIGINT,
    impresiones INTEGER DEFAULT 0,
    duracion_total INTEGER DEFAULT 0,
    fecha_estadistica DATE NOT NULL,
    hora_inicio TIME,
    hora_fin TIME,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (campana_id) REFERENCES campanas(id) ON DELETE CASCADE,
    FOREIGN KEY (contenido_id) REFERENCES contenidos(id) ON DELETE SET NULL,
    FOREIGN KEY (pantalla_id) REFERENCES pantallas(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS logs_auditoria (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT,
    accion VARCHAR(100) NOT NULL,
    entidad VARCHAR(100),
    entidad_id BIGINT,
    descripcion TEXT,
    ip VARCHAR(50),
    user_agent TEXT,
    metodo_http VARCHAR(10),
    url VARCHAR(500),
    parametros TEXT,
    resultado VARCHAR(20),
    duracion_ms INTEGER,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS sesiones (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    token VARCHAR(500) UNIQUE NOT NULL,
    refresh_token VARCHAR(500) UNIQUE,
    ip VARCHAR(50),
    user_agent TEXT,
    activa BOOLEAN DEFAULT TRUE,
    fecha_expiracion TIMESTAMP NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS notificaciones (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    mensaje TEXT NOT NULL,
    tipo VARCHAR(50) DEFAULT 'info',
    leida BOOLEAN DEFAULT FALSE,
    url VARCHAR(500),
    fecha_lectura TIMESTAMP,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS agentes_ia (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    prompt_sistema TEXT,
    configuracion JSONB,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS conversaciones_ia (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    agente_ia_id BIGINT NOT NULL,
    titulo VARCHAR(200),
    activa BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (agente_ia_id) REFERENCES agentes_ia(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS mensajes_ia (
    id BIGSERIAL PRIMARY KEY,
    conversacion_id BIGINT NOT NULL,
    rol VARCHAR(20) NOT NULL,
    contenido TEXT NOT NULL,
    tokens_usados INTEGER,
    costo DECIMAL(10,4),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conversacion_id) REFERENCES conversaciones_ia(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS configuracion_sistema (
    id BIGSERIAL PRIMARY KEY,
    clave VARCHAR(100) UNIQUE NOT NULL,
    valor TEXT,
    tipo VARCHAR(50) DEFAULT 'string',
    descripcion TEXT,
    categoria VARCHAR(50),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices

CREATE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios(email);
CREATE INDEX IF NOT EXISTS idx_usuarios_nombre_usuario ON usuarios(nombre_usuario);
CREATE INDEX IF NOT EXISTS idx_usuarios_activo ON usuarios(activo);
CREATE INDEX IF NOT EXISTS idx_campanas_usuario_id ON campanas(usuario_id);
CREATE INDEX IF NOT EXISTS idx_campanas_estado ON campanas(estado);
CREATE INDEX IF NOT EXISTS idx_campanas_fecha_inicio ON campanas(fecha_inicio);
CREATE INDEX IF NOT EXISTS idx_campanas_fecha_fin ON campanas(fecha_fin);
CREATE INDEX IF NOT EXISTS idx_contenidos_usuario_id ON contenidos(usuario_id);
CREATE INDEX IF NOT EXISTS idx_contenidos_tipo ON contenidos(tipo);
CREATE INDEX IF NOT EXISTS idx_contenidos_activo ON contenidos(activo);
CREATE INDEX IF NOT EXISTS idx_pantallas_codigo_unico ON pantallas(codigo_unico);
CREATE INDEX IF NOT EXISTS idx_pantallas_estado ON pantallas(estado);
CREATE INDEX IF NOT EXISTS idx_pantallas_usuario_id ON pantallas(usuario_id);
CREATE INDEX IF NOT EXISTS idx_estadisticas_campana_id ON estadisticas(campana_id);
CREATE INDEX IF NOT EXISTS idx_estadisticas_fecha ON estadisticas(fecha_estadistica);
CREATE INDEX IF NOT EXISTS idx_logs_usuario_id ON logs_auditoria(usuario_id);
CREATE INDEX IF NOT EXISTS idx_logs_accion ON logs_auditoria(accion);
CREATE INDEX IF NOT EXISTS idx_logs_fecha ON logs_auditoria(fecha_creacion);
CREATE INDEX IF NOT EXISTS idx_sesiones_usuario_id ON sesiones(usuario_id);
CREATE INDEX IF NOT EXISTS idx_sesiones_token ON sesiones(token);
CREATE INDEX IF NOT EXISTS idx_sesiones_activa ON sesiones(activa);
CREATE INDEX IF NOT EXISTS idx_notificaciones_usuario_id ON notificaciones(usuario_id);
CREATE INDEX IF NOT EXISTS idx_notificaciones_leida ON notificaciones(leida);

-- Función y triggers

CREATE OR REPLACE FUNCTION actualizar_fecha_actualizacion()
RETURNS TRIGGER AS $$
BEGIN
    NEW.fecha_actualizacion = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DO $$
DECLARE
    t RECORD;
BEGIN
    FOR t IN
        SELECT table_name FROM information_schema.columns
        WHERE column_name = 'fecha_actualizacion'
          AND table_schema = 'public'
    LOOP
        EXECUTE format(
            'CREATE TRIGGER trigger_actualizar_%I BEFORE UPDATE ON %I
             FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_actualizacion();',
            t.table_name, t.table_name
        );
    END LOOP;
END $$;

-- Datos iniciales

-- Roles

INSERT INTO roles (nombre, descripcion) VALUES
('ROLE_ADMIN', 'Administrador del sistema con acceso completo'),
('ROLE_TECNICO', 'Técnico con permisos de configuración'),
('ROLE_DEVELOPER', 'Desarrollador con acceso a herramientas'),
('ROLE_USUARIO', 'Usuario estándar')
ON CONFLICT (nombre) DO NOTHING;

-- Permisos

INSERT INTO permisos (nombre, descripcion, modulo) VALUES
('usuarios.ver', 'Ver usuarios', 'usuarios'),
('usuarios.crear', 'Crear usuarios', 'usuarios'),
('usuarios.editar', 'Editar usuarios', 'usuarios'),
('usuarios.eliminar', 'Eliminar usuarios', 'usuarios'),
('campanas.ver', 'Ver campañas', 'campanas'),
('campanas.crear', 'Crear campañas', 'campanas'),
('campanas.editar', 'Editar campañas', 'campanas'),
('campanas.eliminar', 'Eliminar campañas', 'campanas'),
('contenidos.ver', 'Ver contenidos', 'contenidos'),
('contenidos.crear', 'Crear contenidos', 'contenidos'),
('contenidos.editar', 'Editar contenidos', 'contenidos'),
('contenidos.eliminar', 'Eliminar contenidos', 'contenidos'),
('contenidos.aprobar', 'Aprobar contenidos', 'contenidos'),
('pantallas.ver', 'Ver pantallas', 'pantallas'),
('pantallas.crear', 'Crear pantallas', 'pantallas'),
('pantallas.editar', 'Editar pantallas', 'pantallas'),
('pantallas.eliminar', 'Eliminar pantallas', 'pantallas'),
('estadisticas.ver', 'Ver estadísticas', 'estadisticas'),
('estadisticas.exportar', 'Exportar estadísticas', 'estadisticas'),
('sistema.configurar', 'Configurar sistema', 'sistema'),
('sistema.logs', 'Ver logs del sistema', 'sistema'),
('sistema.monitoreo', 'Acceder a monitoreo', 'sistema'),
('ia.usar', 'Usar asistente IA', 'ia'),
('ia.configurar', 'Configurar agentes IA', 'ia')
ON CONFLICT (nombre) DO NOTHING;

-- Asignar permisos a roles

INSERT INTO rol_permisos (rol_id, permiso_id)
SELECT r.id, p.id FROM roles r, permisos p WHERE r.nombre = 'ROLE_ADMIN'
ON CONFLICT DO NOTHING;

INSERT INTO rol_permisos (rol_id, permiso_id)
SELECT r.id, p.id FROM roles r, permisos p 
WHERE r.nombre = 'ROLE_TECNICO'
AND p.nombre IN (
    'campanas.ver', 'campanas.crear', 'campanas.editar',
    'contenidos.ver', 'contenidos.crear', 'contenidos.editar',
    'pantallas.ver', 'pantallas.editar', 'estadisticas.ver',
    'sistema.configurar', 'sistema.logs', 'ia.usar'
)
ON CONFLICT DO NOTHING;

INSERT INTO rol_permisos (rol_id, permiso_id)
SELECT r.id, p.id FROM roles r, permisos p 
WHERE r.nombre = 'ROLE_DEVELOPER'
AND p.nombre IN (
    'campanas.ver', 'contenidos.ver', 'contenidos.crear',
    'pantallas.ver', 'estadisticas.ver', 'sistema.logs',
    'ia.usar', 'ia.configurar'
)
ON CONFLICT DO NOTHING;

INSERT INTO rol_permisos (rol_id, permiso_id)
SELECT r.id, p.id FROM roles r, permisos p 
WHERE r.nombre = 'ROLE_USUARIO'
AND p.nombre IN (
    'campanas.ver', 'contenidos.ver', 'contenidos.crear',
    'estadisticas.ver', 'ia.usar'
)
ON CONFLICT DO NOTHING;

-- Usuarios (password para todos: Admin123!)
-- Hash bcrypt para Admin123!: $2a$10$N9qo8uLOiCkgX2ZMROZOmeIjZaGcfL7P92LDGxAd68lJzDL17lhwy

INSERT INTO usuarios (nombre_usuario, email, password, nombre_completo, activo, verificado) VALUES
('admin', 'admin@innoad.com', '$2a$10$N9qo8uLOiCkgX2ZMROZOmeIjZaGcfL7P92LDGxAd68lJzDL17lhwy', 'Administrador del Sistema', true, true)
ON CONFLICT (nombre_usuario) DO NOTHING;

INSERT INTO usuarios (nombre_usuario, email, password, nombre_completo, activo, verificado) VALUES
('tecnico', 'tecnico@innoad.com', '$2a$10$N9qo8uLOiCkgX2ZMROZOmeIjZaGcfL7P92LDGxAd68lJzDL17lhwy', 'Técnico del Sistema', true, true)
ON CONFLICT (nombre_usuario) DO NOTHING;

INSERT INTO usuarios (nombre_usuario, email, password, nombre_completo, activo, verificado) VALUES
('dev', 'dev@innoad.com', '$2a$10$N9qo8uLOiCkgX2ZMROZOmeIjZaGcfL7P92LDGxAd68lJzDL17lhwy', 'Desarrollador', true, true)
ON CONFLICT (nombre_usuario) DO NOTHING;

INSERT INTO usuarios (nombre_usuario, email, password, nombre_completo, activo, verificado) VALUES
('usuario', 'usuario@innoad.com', '$2a$10$N9qo8uLOiCkgX2ZMROZOmeIjZaGcfL7P92LDGxAd68lJzDL17lhwy', 'Usuario Estándar', true, true)
ON CONFLICT (nombre_usuario) DO NOTHING;

-- Asignar roles a usuarios

INSERT INTO usuario_roles (usuario_id, rol_id)
SELECT u.id, r.id FROM usuarios u, roles r WHERE u.nombre_usuario = 'admin' AND r.nombre = 'ROLE_ADMIN'
ON CONFLICT DO NOTHING;

INSERT INTO usuario_roles (usuario_id, rol_id)
SELECT u.id, r.id FROM usuarios u, roles r WHERE u.nombre_usuario = 'tecnico' AND r.nombre = 'ROLE_TECNICO'
ON CONFLICT DO NOTHING;

INSERT INTO usuario_roles (usuario_id, rol_id)
SELECT u.id, r.id FROM usuarios u, roles r WHERE u.nombre_usuario = 'dev' AND r.nombre = 'ROLE_DEVELOPER'
ON CONFLICT DO NOTHING;

INSERT INTO usuario_roles (usuario_id, rol_id)
SELECT u.id, r.id FROM usuarios u, roles r WHERE u.nombre_usuario = 'usuario' AND r.nombre = 'ROLE_USUARIO'
ON CONFLICT DO NOTHING;

-- Agente IA

INSERT INTO agentes_ia (nombre, tipo, prompt_sistema, configuracion) VALUES
('Asistente InnoAd', 'general', 'Eres un asistente virtual especializado en publicidad digital y gestión de campañas.', '{"modelo": "gpt-4", "temperatura": 0.7, "max_tokens": 2000}')
ON CONFLICT DO NOTHING;

-- Configuraciones del sistema

INSERT INTO configuracion_sistema (clave, valor, tipo, descripcion, categoria) VALUES
('sistema.nombre', 'InnoAd', 'string', 'Nombre del sistema', 'general'),
('sistema.version', '2.0.0', 'string', 'Versión del sistema', 'general'),
('sistema.mantenimiento', 'false', 'boolean', 'Modo mantenimiento', 'general'),
('usuarios.max_sesiones', '3', 'integer', 'Máximo sesiones simultáneas', 'seguridad'),
('contenidos.tamano_max_mb', '10', 'integer', 'Tamaño máximo archivo MB', 'contenidos'),
('campanas.duracion_min_dias', '1', 'integer', 'Duración mínima campaña días', 'campanas'),
('notificaciones.habilitadas', 'true', 'boolean', 'Notificaciones habilitadas', 'notificaciones')
ON CONFLICT (clave) DO NOTHING;

-- Vistas

CREATE OR REPLACE VIEW vista_usuarios_roles AS
SELECT 
    u.id,
    u.nombre_usuario,
    u.email,
    u.nombre_completo,
    u.activo,
    u.verificado,
    u.ultimo_acceso,
    r.nombre AS rol_nombre,
    r.descripcion AS rol_descripcion
FROM usuarios u
LEFT JOIN usuario_roles ur ON u.id = ur.usuario_id
LEFT JOIN roles r ON ur.rol_id = r.id;

CREATE OR REPLACE VIEW vista_campanas_estadisticas AS
SELECT 
    c.id,
    c.nombre,
    c.descripcion,
    c.fecha_inicio,
    c.fecha_fin,
    c.estado,
    c.presupuesto,
    u.nombre_usuario AS creador,
    COUNT(DISTINCT cp.pantalla_id) AS total_pantallas,
    COUNT(DISTINCT cc.contenido_id) AS total_contenidos,
    COALESCE(SUM(e.impresiones), 0) AS total_impresiones
FROM campanas c
LEFT JOIN usuarios u ON c.usuario_id = u.id
LEFT JOIN campana_pantallas cp ON c.id = cp.campana_id
LEFT JOIN campana_contenidos cc ON c.id = cc.campana_id
LEFT JOIN estadisticas e ON c.id = e.campana_id
GROUP BY c.id, c.nombre, c.descripcion, c.fecha_inicio, c.fecha_fin, 
         c.estado, c.presupuesto, u.nombre_usuario;

-- Verificación

SELECT 'roles:' AS tipo, COUNT(*) AS cantidad FROM roles
UNION ALL SELECT 'permisos:', COUNT(*) FROM permisos
UNION ALL SELECT 'usuarios:', COUNT(*) FROM usuarios
UNION ALL SELECT 'agentes_ia:', COUNT(*) FROM agentes_ia
UNION ALL SELECT 'configuraciones:', COUNT(*) FROM configuracion_sistema;

SELECT 'Base de datos innoad_db creada correctamente' AS mensaje;
SELECT 'Usuarios: admin/Admin123! | tecnico/Admin123! | dev/Admin123! | usuario/Admin123!' AS credenciales;
