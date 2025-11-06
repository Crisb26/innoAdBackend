-- BASE DE DATOS INNOAD - POSTGRESQL
-- Sistema de Gestion de Publicidad Digital con Inteligencia Artificial
-- Plataforma Remota, Segura y Sostenible
-- Autor: Cristopher Bueno
-- Fecha: Enero 2025
-- Version: 2.0.0

-- PASO 1: Crear la base de datos
CREATE DATABASE innoad_db
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    CONNECTION LIMIT = -1;

-- PASO 2: Conectarse a la base de datos
-- \c innoad_db

-- PASO 3: Crear extension para UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- TABLA: usuarios
-- Almacena todos los usuarios del sistema con diferentes roles
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
    CONSTRAINT chk_usuarios_rol CHECK (rol IN ('ADMINISTRADOR', 'CLIENTE', 'TECNICO', 'DESARROLLADOR', 'USUARIO', 'VISITANTE'))
);

CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_nombre_usuario ON usuarios(nombre_usuario);
CREATE INDEX idx_usuarios_rol ON usuarios(rol);
CREATE INDEX idx_usuarios_activo ON usuarios(activo);
CREATE INDEX idx_usuarios_verificado ON usuarios(verificado);
CREATE INDEX idx_usuarios_fecha_registro ON usuarios(fecha_registro);

COMMENT ON TABLE usuarios IS 'Tabla principal de usuarios del sistema InnoAd';
COMMENT ON COLUMN usuarios.rol IS 'Rol del usuario: ADMINISTRADOR, CLIENTE, TECNICO, DESARROLLADOR, USUARIO, VISITANTE';
COMMENT ON COLUMN usuarios.activo IS 'Indica si la cuenta del usuario esta activa';
COMMENT ON COLUMN usuarios.verificado IS 'Indica si el email del usuario ha sido verificado';

-- TABLA: tokens_verificacion
-- Almacena tokens para verificacion de email y recuperacion de contraseña
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

COMMENT ON TABLE tokens_verificacion IS 'Tokens de verificacion de email y recuperacion de contraseña';

-- TABLA: pantallas
-- Pantallas fisicas Raspberry Pi conectadas al sistema
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
    usuario_id BIGINT NOT NULL,
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultima_conexion TIMESTAMP,
    ultima_sincronizacion TIMESTAMP,
    direccion_ip VARCHAR(45),
    version_software VARCHAR(20),
    informacion_sistema VARCHAR(500),
    notas TEXT,
    CONSTRAINT fk_pantalla_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id) ON UPDATE CASCADE,
    CONSTRAINT chk_pantallas_estado CHECK (estado IN ('ACTIVA', 'INACTIVA', 'MANTENIMIENTO', 'DESCONECTADA')),
    CONSTRAINT chk_pantallas_orientacion CHECK (orientacion IN ('HORIZONTAL', 'VERTICAL'))
);

CREATE INDEX idx_pantallas_nombre ON pantallas(nombre);
CREATE INDEX idx_pantallas_codigo ON pantallas(codigo_identificacion);
CREATE INDEX idx_pantallas_estado ON pantallas(estado);
CREATE INDEX idx_pantallas_usuario_id ON pantallas(usuario_id);
CREATE INDEX idx_pantallas_ultima_conexion ON pantallas(ultima_conexion);

COMMENT ON TABLE pantallas IS 'Pantallas fisicas Raspberry Pi conectadas al sistema';
COMMENT ON COLUMN pantallas.codigo_identificacion IS 'Codigo unico para conectar la Raspberry Pi';
COMMENT ON COLUMN pantallas.token_autenticacion IS 'Token JWT para autenticacion de la pantalla';

-- TABLA: contenidos
-- Contenidos publicitarios que se muestran en las pantallas
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
    CONSTRAINT chk_contenidos_tipo CHECK (tipo IN ('IMAGEN', 'VIDEO', 'TEXTO', 'HTML')),
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

COMMENT ON TABLE contenidos IS 'Contenidos publicitarios para mostrar en pantallas';
COMMENT ON COLUMN contenidos.tipo IS 'Tipo de contenido: IMAGEN, VIDEO, TEXTO, HTML';
COMMENT ON COLUMN contenidos.duracion_segundos IS 'Tiempo de visualizacion en segundos';

-- TABLA: publicidades
-- Campañas publicitarias y anuncios gestionados por el sistema
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

COMMENT ON TABLE publicidades IS 'Campañas publicitarias y anuncios del sistema';
COMMENT ON COLUMN publicidades.generada_por_ia IS 'Indica si la publicidad fue creada con ayuda de IA';
COMMENT ON COLUMN publicidades.calificacion_ia IS 'Calificacion de efectividad por IA 1-100';

-- TABLA: conversaciones_ia
-- Historial de conversaciones entre usuarios y el agente de inteligencia artificial
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

COMMENT ON TABLE conversaciones_ia IS 'Historial de conversaciones con el agente de IA';
COMMENT ON COLUMN conversaciones_ia.contexto IS 'Contexto: creacion_publicidad, consulta_general, analisis_campana';
COMMENT ON COLUMN conversaciones_ia.calificacion IS 'Calificacion del usuario 1-5 estrellas';

-- TABLA: configuracion_sistema
-- Configuracion general del sistema y parametros de administracion
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

COMMENT ON TABLE configuracion_sistema IS 'Configuracion general del sistema InnoAd';
COMMENT ON COLUMN configuracion_sistema.modo_mantenimiento_activo IS 'Indica si el sistema esta en modo mantenimiento';

-- DATOS INICIALES
-- Usuarios por defecto para el sistema
-- Contraseña para todos los usuarios: Admin123!
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
     'TECNICO', TRUE, TRUE)
ON CONFLICT (nombre_usuario) DO NOTHING;

-- Configuracion inicial del sistema
INSERT INTO configuracion_sistema (clave, valor, descripcion)
VALUES
    ('sistema.version', '2.0.0', 'Version actual del sistema InnoAd'),
    ('sistema.nombre', 'InnoAd - Sistema de Publicidad Digital', 'Nombre del sistema'),
    ('sistema.max_usuarios', '1000', 'Capacidad maxima de usuarios'),
    ('sistema.modo_demo', 'false', 'Indica si el sistema esta en modo demo'),
    ('ai.modelo_default', 'gpt-4', 'Modelo de IA por defecto'),
    ('notificaciones.email_habilitado', 'true', 'Habilitar envio de emails'),
    ('seguridad.intentos_login_max', '5', 'Intentos maximos de login antes de bloqueo'),
    ('seguridad.tiempo_bloqueo_horas', '24', 'Horas de bloqueo tras intentos fallidos'),
    ('contenido.duracion_default_segundos', '10', 'Duracion por defecto de contenidos'),
    ('pantallas.tiempo_sincronizacion_minutos', '5', 'Tiempo de sincronizacion de pantallas')
ON CONFLICT (clave) DO NOTHING;

-- TRIGGERS
-- Trigger para actualizar fecha_actualizacion automaticamente en contenidos
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

-- VISTAS
-- Vista de estadisticas de usuarios
CREATE OR REPLACE VIEW vista_estadisticas_usuarios AS
SELECT
    rol,
    COUNT(*) as total_usuarios,
    COUNT(*) FILTER (WHERE activo = TRUE) as usuarios_activos,
    COUNT(*) FILTER (WHERE verificado = TRUE) as usuarios_verificados,
    COUNT(*) FILTER (WHERE ultimo_acceso > CURRENT_TIMESTAMP - INTERVAL '7 days') as usuarios_activos_semana
FROM usuarios
GROUP BY rol;

-- Vista de pantallas activas con informacion relevante
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

-- Vista de contenidos activos programados por pantalla
CREATE OR REPLACE VIEW vista_contenidos_programados AS
SELECT
    c.id,
    c.titulo,
    c.tipo,
    c.duracion_segundos,
    c.orden,
    c.prioridad,
    c.fecha_inicio,
    c.fecha_fin,
    p.nombre as pantalla_nombre,
    p.codigo_identificacion as pantalla_codigo,
    u.nombre_usuario as creador
FROM contenidos c
JOIN pantallas p ON c.pantalla_id = p.id
JOIN usuarios u ON c.usuario_id = u.id
WHERE c.estado = 'ACTIVO'
  AND (c.fecha_inicio IS NULL OR c.fecha_inicio <= CURRENT_TIMESTAMP)
  AND (c.fecha_fin IS NULL OR c.fecha_fin >= CURRENT_TIMESTAMP)
ORDER BY p.id, c.orden, c.prioridad DESC;

-- Vista de metricas de publicidades con calculos
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

-- Informacion de base de datos
COMMENT ON DATABASE innoad_db IS 'Base de datos del sistema InnoAd - Plataforma de gestion de publicidad digital con IA integrada';

-- FIN DEL SCRIPT
-- Para ejecutar este script:
-- 1. Abrir psql o pgAdmin
-- 2. Ejecutar: psql -U postgres -f innoad_postgresql_schema.sql
--
-- Credenciales de usuarios por defecto:
-- Admin: admin / Admin123!
-- Cliente: cliente_demo / Admin123!
-- Tecnico: tecnico_demo / Admin123!
