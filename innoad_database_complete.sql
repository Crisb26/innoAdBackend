-- BASE DE DATOS INNOAD - POSTGRESQL
-- Sistema Integral de Gestion de Publicidad Digital
-- Incluye: Usuarios, Campañas, Pantallas, Contenidos, Reuniones, Tareas y Auditoria
-- Autor: Cristopher Bueno
-- Fecha: Enero 2025
-- Version: 2.0.0

-- PASO 1: Eliminar base de datos si existe y crearla de nuevo
DROP DATABASE IF EXISTS innoad_db;

CREATE DATABASE innoad_db
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    CONNECTION LIMIT = -1;

-- PASO 2: Conectarse a la base de datos
-- En psql: \c innoad_db
-- En pgAdmin: Seleccionar innoad_db

-- PASO 3: Crear extensiones
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- MODULO DE USUARIOS Y ROLES

CREATE TABLE roles (
    id_rol SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_roles_nombre ON roles(nombre);
CREATE INDEX idx_roles_activo ON roles(activo);

COMMENT ON TABLE roles IS 'Roles del sistema con permisos diferenciados';

CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    nombre_usuario VARCHAR(50) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL,
    telefono VARCHAR(20),
    empresa VARCHAR(200),
    cargo VARCHAR(100),
    foto_perfil TEXT,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    verificado BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultimo_acceso TIMESTAMP,
    token_verificacion VARCHAR(255),
    token_verificacion_expiracion TIMESTAMP,
    token_recuperacion VARCHAR(255),
    token_recuperacion_expiracion TIMESTAMP,
    intentos_fallidos INTEGER DEFAULT 0,
    fecha_bloqueo TIMESTAMP,
    preferencia_notificaciones TEXT,
    CONSTRAINT chk_usuarios_rol CHECK (rol IN ('ADMINISTRADOR', 'CLIENTE', 'TECNICO', 'DESARROLLADOR', 'USUARIO', 'VISITANTE', 'MANAGER', 'EDITOR', 'VIEWER'))
);

CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_nombre_usuario ON usuarios(nombre_usuario);
CREATE INDEX idx_usuarios_rol ON usuarios(rol);
CREATE INDEX idx_usuarios_activo ON usuarios(activo);
CREATE INDEX idx_usuarios_verificado ON usuarios(verificado);
CREATE INDEX idx_usuarios_fecha_registro ON usuarios(fecha_registro);

COMMENT ON TABLE usuarios IS 'Usuarios del sistema con autenticacion y roles';

CREATE TABLE tokens_verificacion (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    usuario_id BIGINT NOT NULL,
    expira_en TIMESTAMP NOT NULL,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    verificado_en TIMESTAMP,
    CONSTRAINT fk_token_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE INDEX idx_tokens_token ON tokens_verificacion(token);
CREATE INDEX idx_tokens_usuario_id ON tokens_verificacion(usuario_id);
CREATE INDEX idx_tokens_expira_en ON tokens_verificacion(expira_en);

COMMENT ON TABLE tokens_verificacion IS 'Tokens de verificacion de email y recuperacion';

-- MODULO DE CAMPAÑAS PUBLICITARIAS

CREATE TABLE campanas (
    id_campana SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    id_usuario BIGINT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    estado VARCHAR(20) DEFAULT 'borrador',
    presupuesto DECIMAL(10,2) DEFAULT 0,
    plataforma VARCHAR(50) DEFAULT 'Google Ads',
    tipo_objetivo VARCHAR(50) DEFAULT 'conversiones',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_campana_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id) ON UPDATE CASCADE,
    CONSTRAINT chk_campanas_estado CHECK (estado IN ('borrador', 'activa', 'pausada', 'finalizada'))
);

CREATE INDEX idx_campanas_nombre ON campanas(nombre);
CREATE INDEX idx_campanas_estado ON campanas(estado);
CREATE INDEX idx_campanas_usuario ON campanas(id_usuario);
CREATE INDEX idx_campanas_fechas ON campanas(fecha_inicio, fecha_fin);

COMMENT ON TABLE campanas IS 'Campañas publicitarias del sistema';

CREATE TABLE anuncios (
    id_anuncio SERIAL PRIMARY KEY,
    contenido TEXT NOT NULL,
    id_campana INTEGER NOT NULL,
    tipo_contenido VARCHAR(20) DEFAULT 'texto',
    fecha_publicacion TIMESTAMP,
    estado VARCHAR(20) DEFAULT 'programado',
    impresiones INTEGER DEFAULT 0,
    clics INTEGER DEFAULT 0,
    costo_total DECIMAL(10,2) DEFAULT 0,
    cpc_maximo DECIMAL(6,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_anuncio_campana FOREIGN KEY (id_campana)
        REFERENCES campanas(id_campana) ON DELETE CASCADE,
    CONSTRAINT chk_anuncios_tipo CHECK (tipo_contenido IN ('texto', 'imagen', 'video', 'html')),
    CONSTRAINT chk_anuncios_estado CHECK (estado IN ('programado', 'publicado', 'pausado'))
);

CREATE INDEX idx_anuncios_campana ON anuncios(id_campana);
CREATE INDEX idx_anuncios_estado ON anuncios(estado);

COMMENT ON TABLE anuncios IS 'Anuncios asociados a campañas publicitarias';

-- MODULO DE PANTALLAS Y CONTENIDOS

CREATE TABLE pantallas (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(500),
    codigo_identificacion VARCHAR(50) NOT NULL UNIQUE,
    token_autenticacion VARCHAR(500),
    estado VARCHAR(20) NOT NULL DEFAULT 'INACTIVA',
    ubicacion VARCHAR(200),
    resolucion VARCHAR(20),
    orientacion VARCHAR(20) DEFAULT 'HORIZONTAL',
    mac_address VARCHAR(17) UNIQUE,
    ip_address VARCHAR(45),
    usuario_id BIGINT NOT NULL,
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultima_conexion TIMESTAMP,
    ultima_sincronizacion TIMESTAMP,
    direccion_ip VARCHAR(45),
    version_software VARCHAR(20),
    informacion_sistema VARCHAR(500),
    volumen_audio INTEGER DEFAULT 50,
    brillo_pantalla INTEGER DEFAULT 80,
    notas TEXT,
    CONSTRAINT fk_pantalla_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id) ON UPDATE CASCADE,
    CONSTRAINT chk_pantallas_estado CHECK (estado IN ('ACTIVA', 'INACTIVA', 'MANTENIMIENTO', 'DESCONECTADA', 'ERROR', 'REPRODUCIENDO')),
    CONSTRAINT chk_pantallas_orientacion CHECK (orientacion IN ('HORIZONTAL', 'VERTICAL'))
);

CREATE INDEX idx_pantallas_nombre ON pantallas(nombre);
CREATE INDEX idx_pantallas_codigo ON pantallas(codigo_identificacion);
CREATE INDEX idx_pantallas_estado ON pantallas(estado);
CREATE INDEX idx_pantallas_usuario_id ON pantallas(usuario_id);
CREATE INDEX idx_pantallas_ultima_conexion ON pantallas(ultima_conexion);
CREATE INDEX idx_pantallas_mac ON pantallas(mac_address);

COMMENT ON TABLE pantallas IS 'Pantallas Raspberry Pi conectadas al sistema';

CREATE TABLE contenidos (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    descripcion TEXT,
    tipo VARCHAR(20) NOT NULL,
    url_archivo VARCHAR(500),
    nombre_archivo VARCHAR(255),
    tamano_archivo BIGINT,
    tipo_mime VARCHAR(100),
    contenido_texto TEXT,
    contenido_html TEXT,
    checksum VARCHAR(64),
    duracion_segundos INTEGER NOT NULL DEFAULT 10,
    orden INTEGER DEFAULT 0,
    prioridad VARCHAR(20) DEFAULT 'NORMAL',
    estado VARCHAR(20) NOT NULL DEFAULT 'BORRADOR',
    fecha_inicio TIMESTAMP,
    fecha_fin TIMESTAMP,
    pantalla_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    veces_reproducido INTEGER DEFAULT 0,
    ultima_reproduccion TIMESTAMP,
    tags VARCHAR(500),
    CONSTRAINT fk_contenido_pantalla FOREIGN KEY (pantalla_id)
        REFERENCES pantallas(id) ON DELETE CASCADE,
    CONSTRAINT fk_contenido_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id) ON UPDATE CASCADE,
    CONSTRAINT chk_contenidos_tipo CHECK (tipo IN ('IMAGEN', 'VIDEO', 'TEXTO', 'HTML', 'WEB')),
    CONSTRAINT chk_contenidos_prioridad CHECK (prioridad IN ('BAJA', 'NORMAL', 'ALTA', 'URGENTE')),
    CONSTRAINT chk_contenidos_estado CHECK (estado IN ('BORRADOR', 'ACTIVO', 'PAUSADO', 'FINALIZADO')),
    CONSTRAINT chk_contenidos_duracion CHECK (duracion_segundos >= 1)
);

CREATE INDEX idx_contenidos_titulo ON contenidos(titulo);
CREATE INDEX idx_contenidos_tipo ON contenidos(tipo);
CREATE INDEX idx_contenidos_estado ON contenidos(estado);
CREATE INDEX idx_contenidos_pantalla_id ON contenidos(pantalla_id);
CREATE INDEX idx_contenidos_usuario_id ON contenidos(usuario_id);
CREATE INDEX idx_contenidos_fecha_inicio ON contenidos(fecha_inicio);
CREATE INDEX idx_contenidos_fecha_fin ON contenidos(fecha_fin);
CREATE INDEX idx_contenidos_orden ON contenidos(orden);

COMMENT ON TABLE contenidos IS 'Contenidos publicitarios para pantallas';

CREATE TABLE programacion_contenido (
    id_programacion SERIAL PRIMARY KEY,
    dispositivo_id BIGINT NOT NULL,
    contenido_id BIGINT NOT NULL,
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin TIMESTAMP NOT NULL,
    repetir_diariamente BOOLEAN DEFAULT FALSE,
    repetir_semanal BOOLEAN DEFAULT FALSE,
    dias_semana VARCHAR(100),
    hora_inicio TIME,
    hora_fin TIME,
    prioridad INTEGER DEFAULT 1,
    activo BOOLEAN DEFAULT TRUE,
    programado_por BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_prog_dispositivo FOREIGN KEY (dispositivo_id)
        REFERENCES pantallas(id) ON DELETE CASCADE,
    CONSTRAINT fk_prog_contenido FOREIGN KEY (contenido_id)
        REFERENCES contenidos(id) ON DELETE CASCADE,
    CONSTRAINT fk_prog_usuario FOREIGN KEY (programado_por)
        REFERENCES usuarios(id) ON UPDATE CASCADE
);

CREATE INDEX idx_prog_dispositivo ON programacion_contenido(dispositivo_id);
CREATE INDEX idx_prog_contenido ON programacion_contenido(contenido_id);
CREATE INDEX idx_prog_activo ON programacion_contenido(activo);

COMMENT ON TABLE programacion_contenido IS 'Programacion de contenidos en pantallas';

-- MODULO DE PUBLICIDADES CON IA

CREATE TABLE publicidades (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    descripcion TEXT NOT NULL,
    url_imagen VARCHAR(500),
    url_video VARCHAR(500),
    url_destino VARCHAR(500),
    usuario_creador_id BIGINT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP,
    fecha_inicio TIMESTAMP,
    fecha_fin TIMESTAMP,
    estado VARCHAR(50) DEFAULT 'BORRADOR',
    impresiones INTEGER DEFAULT 0,
    clics INTEGER DEFAULT 0,
    conversiones INTEGER DEFAULT 0,
    presupuesto DECIMAL(10,2),
    gastado DECIMAL(10,2) DEFAULT 0.0,
    audiencia_objetivo VARCHAR(100),
    categoria VARCHAR(100),
    palabras_clave TEXT,
    generada_por_ia BOOLEAN DEFAULT FALSE,
    sugerencias_ia TEXT,
    calificacion_ia INTEGER,
    CONSTRAINT fk_publicidad_usuario FOREIGN KEY (usuario_creador_id)
        REFERENCES usuarios(id) ON UPDATE CASCADE,
    CONSTRAINT chk_publicidades_estado CHECK (estado IN ('BORRADOR', 'ACTIVA', 'PAUSADA', 'FINALIZADA')),
    CONSTRAINT chk_publicidades_calificacion CHECK (calificacion_ia IS NULL OR (calificacion_ia >= 1 AND calificacion_ia <= 100))
);

CREATE INDEX idx_publicidades_titulo ON publicidades(titulo);
CREATE INDEX idx_publicidades_estado ON publicidades(estado);
CREATE INDEX idx_publicidades_usuario_creador_id ON publicidades(usuario_creador_id);
CREATE INDEX idx_publicidades_fecha_inicio ON publicidades(fecha_inicio);
CREATE INDEX idx_publicidades_fecha_fin ON publicidades(fecha_fin);
CREATE INDEX idx_publicidades_categoria ON publicidades(categoria);

COMMENT ON TABLE publicidades IS 'Publicidades optimizadas con IA';

-- MODULO DE INTELIGENCIA ARTIFICIAL

CREATE TABLE conversaciones_ia (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    mensaje_usuario TEXT NOT NULL,
    respuesta_ia TEXT NOT NULL,
    contexto VARCHAR(100),
    fecha_conversacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tokens_usados INTEGER,
    costo_aproximado DECIMAL(10,4),
    tiempo_respuesta_ms INTEGER,
    modelo_utilizado VARCHAR(50),
    calificacion INTEGER,
    retroalimentacion TEXT,
    exitoso BOOLEAN DEFAULT TRUE,
    mensaje_error TEXT,
    CONSTRAINT fk_conversacion_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT chk_conversaciones_calificacion CHECK (calificacion IS NULL OR (calificacion >= 1 AND calificacion <= 5))
);

CREATE INDEX idx_conversaciones_usuario_id ON conversaciones_ia(usuario_id);
CREATE INDEX idx_conversaciones_fecha ON conversaciones_ia(fecha_conversacion);
CREATE INDEX idx_conversaciones_contexto ON conversaciones_ia(contexto);
CREATE INDEX idx_conversaciones_exitoso ON conversaciones_ia(exitoso);

COMMENT ON TABLE conversaciones_ia IS 'Historial de interacciones con IA';

-- MODULO DE REUNIONES

CREATE TABLE reuniones (
    id_reunion SERIAL PRIMARY KEY,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    motivo VARCHAR(250) NOT NULL,
    descripcion TEXT,
    ubicacion VARCHAR(200),
    estado VARCHAR(20) DEFAULT 'programada',
    organizador_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reunion_organizador FOREIGN KEY (organizador_id)
        REFERENCES usuarios(id) ON UPDATE CASCADE,
    CONSTRAINT chk_reuniones_estado CHECK (estado IN ('programada', 'en_curso', 'finalizada', 'cancelada'))
);

CREATE INDEX idx_reunion_fecha ON reuniones(fecha);
CREATE INDEX idx_reunion_estado ON reuniones(estado);
CREATE INDEX idx_reunion_organizador ON reuniones(organizador_id);

COMMENT ON TABLE reuniones IS 'Reuniones del equipo';

CREATE TABLE reuniones_usuarios (
    id_reunion INTEGER NOT NULL,
    id_usuario BIGINT NOT NULL,
    PRIMARY KEY (id_reunion, id_usuario),
    asistencia VARCHAR(20) DEFAULT 'pendiente',
    comentarios TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ru_reunion FOREIGN KEY (id_reunion)
        REFERENCES reuniones(id_reunion) ON DELETE CASCADE,
    CONSTRAINT fk_ru_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT chk_asistencia CHECK (asistencia IN ('pendiente', 'confirmada', 'rechazada', 'ausente'))
);

COMMENT ON TABLE reuniones_usuarios IS 'Asistentes y confirmaciones de reuniones';

-- MODULO DE TAREAS KANBAN

CREATE TABLE tareas (
    id_tarea SERIAL PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    descripcion TEXT NOT NULL,
    responsable BIGINT NOT NULL,
    asignado_por BIGINT NOT NULL,
    prioridad VARCHAR(20) DEFAULT 'media',
    estado VARCHAR(20) DEFAULT 'pendiente',
    fecha_entrega DATE,
    horas_estimadas DECIMAL(5,2),
    horas_trabajadas DECIMAL(5,2) DEFAULT 0,
    porcentaje_completado INTEGER DEFAULT 0,
    categoria VARCHAR(50),
    etiquetas JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tarea_responsable FOREIGN KEY (responsable)
        REFERENCES usuarios(id) ON UPDATE CASCADE,
    CONSTRAINT fk_tarea_asignado_por FOREIGN KEY (asignado_por)
        REFERENCES usuarios(id) ON UPDATE CASCADE,
    CONSTRAINT chk_tareas_prioridad CHECK (prioridad IN ('baja', 'media', 'alta', 'critica')),
    CONSTRAINT chk_tareas_estado CHECK (estado IN ('pendiente', 'en_proceso', 'en_revision', 'finalizada', 'cancelada'))
);

CREATE INDEX idx_tarea_responsable ON tareas(responsable);
CREATE INDEX idx_tarea_estado ON tareas(estado);
CREATE INDEX idx_tarea_fecha_entrega ON tareas(fecha_entrega);
CREATE INDEX idx_tarea_prioridad ON tareas(prioridad);

COMMENT ON TABLE tareas IS 'Sistema de gestion de tareas Kanban';

-- MODULO DE METRICAS Y MONITOREO

CREATE TABLE metricas_dispositivos (
    id_metrica SERIAL PRIMARY KEY,
    dispositivo_id BIGINT NOT NULL,
    cpu_usage DECIMAL(5,2),
    memoria_total BIGINT,
    memoria_usada BIGINT,
    disco_total BIGINT,
    disco_usado BIGINT,
    temperatura_cpu DECIMAL(4,1),
    estado_red VARCHAR(20),
    velocidad_descarga DECIMAL(8,2),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_metrica_dispositivo FOREIGN KEY (dispositivo_id)
        REFERENCES pantallas(id) ON DELETE CASCADE,
    CONSTRAINT chk_estado_red CHECK (estado_red IN ('excelente', 'buena', 'regular', 'mala'))
);

CREATE INDEX idx_metricas_dispositivo ON metricas_dispositivos(dispositivo_id);
CREATE INDEX idx_metricas_timestamp ON metricas_dispositivos(timestamp);

COMMENT ON TABLE metricas_dispositivos IS 'Metricas de rendimiento de pantallas';

-- MODULO DE AUDITORIA

CREATE TABLE auditoria (
    id_log SERIAL PRIMARY KEY,
    tabla_afectada VARCHAR(50) NOT NULL,
    accion VARCHAR(50) NOT NULL,
    registro_id INTEGER,
    detalles JSONB,
    usuario_id BIGINT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_auditoria_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id) ON DELETE SET NULL
);

CREATE INDEX idx_auditoria_tabla ON auditoria(tabla_afectada);
CREATE INDEX idx_auditoria_fecha ON auditoria(fecha);
CREATE INDEX idx_auditoria_usuario ON auditoria(usuario_id);
CREATE INDEX idx_auditoria_accion ON auditoria(accion);

COMMENT ON TABLE auditoria IS 'Log de auditoria del sistema';

-- MODULO DE CONFIGURACION

CREATE TABLE configuracion_sistema (
    id BIGSERIAL PRIMARY KEY,
    clave VARCHAR(100) NOT NULL UNIQUE,
    valor TEXT,
    descripcion VARCHAR(500),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion_id BIGINT,
    modo_mantenimiento_activo BOOLEAN DEFAULT FALSE,
    codigo_seguridad_mantenimiento VARCHAR(20),
    email_recuperacion_codigo VARCHAR(150),
    fecha_inicio_mantenimiento TIMESTAMP,
    fecha_fin_estimada_mantenimiento TIMESTAMP,
    mensaje_mantenimiento TEXT
);

CREATE INDEX idx_configuracion_clave ON configuracion_sistema(clave);
CREATE INDEX idx_configuracion_modo_mantenimiento ON configuracion_sistema(modo_mantenimiento_activo);

COMMENT ON TABLE configuracion_sistema IS 'Configuracion global del sistema';

-- DATOS INICIALES

-- Roles del sistema
INSERT INTO roles (nombre, descripcion) VALUES
('ADMIN', 'Administrador del sistema con todos los permisos'),
('MANAGER', 'Gerente con permisos de gestion de campañas y equipos'),
('EDITOR', 'Editor de contenido y campañas'),
('VIEWER', 'Solo visualizacion de informacion'),
('TECNICO', 'Soporte tecnico del sistema');

-- Usuarios por defecto
-- Contraseña para todos: Admin123!
INSERT INTO usuarios (nombre, apellido, email, nombre_usuario, contrasena, rol, activo, verificado)
VALUES
    ('Administrador', 'Sistema', 'admin@innoad.com', 'admin',
     '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi.',
     'ADMINISTRADOR', TRUE, TRUE),

    ('Cliente', 'Demo', 'cliente@innoad.com', 'cliente_demo',
     '$2a$12$K2zKWjliBRjgEO9HdxMoWuDAKm.TJdEJRj.hBa0ZOB2TJdHzx8y.e',
     'CLIENTE', TRUE, TRUE),

    ('Tecnico', 'Soporte', 'tecnico@innoad.com', 'tecnico_demo',
     '$2a$12$XQwE3sdV2FcGkDdqXJT7BeTYEpbB5XjPB.VPMxYz7QXrE8K9lPdMG',
     'TECNICO', TRUE, TRUE),

    ('Manager', 'Demo', 'manager@innoad.com', 'manager_demo',
     '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi.',
     'MANAGER', TRUE, TRUE)
ON CONFLICT (nombre_usuario) DO NOTHING;

-- Configuracion inicial
INSERT INTO configuracion_sistema (clave, valor, descripcion)
VALUES
    ('sistema.version', '2.0.0', 'Version actual del sistema InnoAd'),
    ('sistema.nombre', 'InnoAd - Sistema de Publicidad Digital', 'Nombre del sistema'),
    ('sistema.max_usuarios', '1500', 'Capacidad maxima de usuarios'),
    ('sistema.modo_demo', 'false', 'Modo demostracion'),
    ('ai.modelo_default', 'gpt-4', 'Modelo de IA por defecto'),
    ('notificaciones.email_habilitado', 'true', 'Habilitar emails'),
    ('seguridad.intentos_login_max', '5', 'Intentos maximos de login'),
    ('seguridad.tiempo_bloqueo_horas', '24', 'Horas de bloqueo'),
    ('contenido.duracion_default_segundos', '10', 'Duracion por defecto'),
    ('pantallas.tiempo_sincronizacion_minutos', '5', 'Tiempo de sincronizacion')
ON CONFLICT (clave) DO NOTHING;

-- TRIGGERS

CREATE OR REPLACE FUNCTION actualizar_fecha_modificacion()
RETURNS TRIGGER AS $$
BEGIN
    NEW.fecha_actualizacion = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_contenidos_fecha_actualizacion
    BEFORE UPDATE ON contenidos
    FOR EACH ROW
    EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trigger_publicidades_fecha_actualizacion
    BEFORE UPDATE ON publicidades
    FOR EACH ROW
    EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trigger_configuracion_fecha_actualizacion
    BEFORE UPDATE ON configuracion_sistema
    FOR EACH ROW
    EXECUTE FUNCTION actualizar_fecha_modificacion();

-- VISTAS UTILES

CREATE OR REPLACE VIEW vista_estadisticas_usuarios AS
SELECT
    rol,
    COUNT(*) as total_usuarios,
    COUNT(*) FILTER (WHERE activo = TRUE) as usuarios_activos,
    COUNT(*) FILTER (WHERE verificado = TRUE) as usuarios_verificados,
    COUNT(*) FILTER (WHERE ultimo_acceso > CURRENT_TIMESTAMP - INTERVAL '7 days') as usuarios_activos_semana
FROM usuarios
GROUP BY rol;

CREATE OR REPLACE VIEW vista_pantallas_activas AS
SELECT
    p.id,
    p.nombre,
    p.ubicacion,
    p.estado,
    p.ultima_conexion,
    u.nombre_usuario as propietario,
    COUNT(c.id) as total_contenidos,
    COUNT(c.id) FILTER (WHERE c.estado = 'ACTIVO') as contenidos_activos
FROM pantallas p
LEFT JOIN usuarios u ON p.usuario_id = u.id
LEFT JOIN contenidos c ON p.id = c.pantalla_id
WHERE p.estado = 'ACTIVA'
GROUP BY p.id, p.nombre, p.ubicacion, p.estado, p.ultima_conexion, u.nombre_usuario;

CREATE OR REPLACE VIEW vista_metricas_publicidades AS
SELECT
    p.id,
    p.titulo,
    p.estado,
    p.impresiones,
    p.clics,
    p.conversiones,
    CASE
        WHEN p.impresiones > 0 THEN ROUND((p.clics::NUMERIC / p.impresiones::NUMERIC) * 100, 2)
        ELSE 0
    END as ctr_porcentaje,
    CASE
        WHEN p.clics > 0 THEN ROUND((p.conversiones::NUMERIC / p.clics::NUMERIC) * 100, 2)
        ELSE 0
    END as tasa_conversion_porcentaje,
    p.presupuesto,
    p.gastado,
    u.nombre_usuario as creador
FROM publicidades p
JOIN usuarios u ON p.usuario_creador_id = u.id;

-- PERMISOS
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON ALL TABLES IN SCHEMA public TO postgres;
GRANT ALL ON ALL SEQUENCES IN SCHEMA public TO postgres;

-- INFORMACION
COMMENT ON DATABASE innoad_db IS 'Base de datos InnoAd - Sistema integral de publicidad digital';

-- FIN DEL SCRIPT
