-- ============================================================================
-- SCRIPT DE MIGRACIÓN - MÓDULOS CHAT E IA
-- Sistema InnoAd - Diciembre 2025
-- ============================================================================

-- ============================================================================
-- MÓDULO CHAT - Tablas para mensajería entre usuarios
-- ============================================================================

CREATE TABLE IF NOT EXISTS chat_usuario (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_usuario_tecnico BIGINT NOT NULL,
    id_usuario_solicitante BIGINT NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_cierre TIMESTAMP NULL,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario_tecnico) REFERENCES usuario(id) ON DELETE RESTRICT,
    FOREIGN KEY (id_usuario_solicitante) REFERENCES usuario(id) ON DELETE RESTRICT,
    INDEX idx_chat_usuario_id_tecnico (id_usuario_tecnico),
    INDEX idx_chat_usuario_id_solicitante (id_usuario_solicitante),
    INDEX idx_chat_usuario_activo (activo),
    INDEX idx_chat_usuario_fecha (fecha_actualizacion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

COMMENT ON TABLE chat_usuario IS 'Almacena las conversaciones entre técnicos y solicitantes';

-- ============================================================================

CREATE TABLE IF NOT EXISTS mensaje_chat (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_chat_usuario BIGINT NOT NULL,
    id_usuario_remitente BIGINT NOT NULL,
    contenido LONGTEXT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    leido BOOLEAN DEFAULT FALSE,
    fecha_lectura TIMESTAMP NULL,
    FOREIGN KEY (id_chat_usuario) REFERENCES chat_usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario_remitente) REFERENCES usuario(id) ON DELETE RESTRICT,
    INDEX idx_mensaje_chat_id_chat (id_chat_usuario),
    INDEX idx_mensaje_chat_id_usuario (id_usuario_remitente),
    INDEX idx_mensaje_chat_fecha (fecha_creacion),
    INDEX idx_mensaje_chat_leido (leido)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

COMMENT ON TABLE mensaje_chat IS 'Almacena los mensajes individuales dentro de un chat';

-- ============================================================================

CREATE TABLE IF NOT EXISTS solicitud_chat_tecnico (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_usuario BIGINT NOT NULL,
    descripcion LONGTEXT NOT NULL,
    estado ENUM('PENDIENTE', 'ASIGNADA', 'EN_PROGRESO', 'RESUELTA', 'CANCELADA') DEFAULT 'PENDIENTE',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_asignacion TIMESTAMP NULL,
    id_tecnico_asignado BIGINT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE RESTRICT,
    FOREIGN KEY (id_tecnico_asignado) REFERENCES usuario(id) ON DELETE SET NULL,
    INDEX idx_solicitud_chat_usuario (id_usuario),
    INDEX idx_solicitud_chat_estado (estado),
    INDEX idx_solicitud_chat_tecnico (id_tecnico_asignado),
    INDEX idx_solicitud_chat_fecha (fecha_creacion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

COMMENT ON TABLE solicitud_chat_tecnico IS 'Sistema de tickets para solicitudes de soporte técnico';

-- ============================================================================
-- MÓDULO IA - Tablas para asistencia inteligente
-- ============================================================================

CREATE TABLE IF NOT EXISTS prompt_ia_por_rol (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rol VARCHAR(50) NOT NULL,
    instruccion LONGTEXT NOT NULL,
    contexto LONGTEXT,
    token_maximo INT DEFAULT 2000,
    temperatura FLOAT DEFAULT 0.7,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    id_usuario_creador BIGINT,
    FOREIGN KEY (id_usuario_creador) REFERENCES usuario(id) ON DELETE SET NULL,
    UNIQUE KEY unique_rol_activo (rol, activo),
    INDEX idx_prompt_rol (rol),
    INDEX idx_prompt_activo (activo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

COMMENT ON TABLE prompt_ia_por_rol IS 'Almacena instrucciones personalizadas de IA por rol de usuario';

-- ============================================================================

CREATE TABLE IF NOT EXISTS horario_atencion (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    dia_semana ENUM('LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO', 'DOMINGO') NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_dia_semana (dia_semana),
    INDEX idx_horario_activo (activo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

COMMENT ON TABLE horario_atencion IS 'Define los horarios de atención disponibles para la IA por día de semana';

-- ============================================================================

CREATE TABLE IF NOT EXISTS info_sistema_innoad (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    clave VARCHAR(100) NOT NULL UNIQUE,
    valor LONGTEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_clave (clave)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

COMMENT ON TABLE info_sistema_innoad IS 'Almacena metadatos del sistema para contexto de IA (empresa, misión, visión, etc.)';

-- ============================================================================

CREATE TABLE IF NOT EXISTS email_configurado (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    direccion_email VARCHAR(255) NOT NULL UNIQUE,
    contrasenia VARCHAR(255) NOT NULL,
    proveedor_smtp VARCHAR(100) NOT NULL,
    puerto_smtp INT DEFAULT 587,
    activo BOOLEAN DEFAULT TRUE,
    descripcion TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email_activo (activo),
    INDEX idx_email_direccion (direccion_email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

COMMENT ON TABLE email_configurado IS 'Configuración de cuentas SMTP para envío de emails desde IA';

-- ============================================================================

CREATE TABLE IF NOT EXISTS registro_email_ia (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_usuario BIGINT NOT NULL,
    direccion_destinatario VARCHAR(255) NOT NULL,
    asunto VARCHAR(255) NOT NULL,
    contenido LONGTEXT NOT NULL,
    estado ENUM('ENVIADO', 'FALLIDO', 'PENDIENTE') NOT NULL,
    mensaje_error LONGTEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_envio TIMESTAMP NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE RESTRICT,
    INDEX idx_registro_email_usuario (id_usuario),
    INDEX idx_registro_email_fecha (fecha_creacion),
    INDEX idx_registro_email_estado (estado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

COMMENT ON TABLE registro_email_ia IS 'Auditoría completa de emails enviados por la IA';

-- ============================================================================

CREATE TABLE IF NOT EXISTS registro_interaccion_ia (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_usuario BIGINT NOT NULL,
    pregunta LONGTEXT NOT NULL,
    respuesta LONGTEXT,
    estado ENUM('COMPLETADA', 'FALLIDA', 'PROCESANDO') NOT NULL,
    tokens_utilizados INT,
    tiempo_respuesta FLOAT,
    mensaje_error LONGTEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_completacion TIMESTAMP NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE RESTRICT,
    INDEX idx_interaccion_usuario (id_usuario),
    INDEX idx_interaccion_fecha (fecha_creacion),
    INDEX idx_interaccion_estado (estado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

COMMENT ON TABLE registro_interaccion_ia IS 'Historial completo de interacciones con la IA incluyendo métricas de uso';

-- ============================================================================
-- DATOS INICIALES
-- ============================================================================

-- Insertar prompts iniciales para cada rol
INSERT INTO prompt_ia_por_rol (rol, instruccion, contexto, token_maximo, temperatura, activo) VALUES
('ROLE_ADMIN', 
 'Eres un asistente IA administrativo experto. Ayuda al administrador del sistema InnoAd con preguntas sobre configuración, usuarios, reportes y control del sistema. Sé preciso, técnico y detallado en tus respuestas.',
 'Sistema de gestión de publicidad digital con módulos de campañas, contenidos, chat y IA integrada.',
 2000, 0.7, TRUE),

('ROLE_TECNICO',
 'Eres un asistente IA técnico especializado. Ayuda al técnico con solución de problemas, configuración de campañas, gestión de usuarios y consultas sobre funcionalidades del sistema.',
 'Sistema de gestión de publicidad digital con módulos de campañas, contenidos, chat y IA integrada.',
 2000, 0.7, TRUE),

('ROLE_DEVELOPER',
 'Eres un asistente IA para desarrolladores. Proporciona explicaciones técnicas detalladas sobre arquitectura, APIs, base de datos, códigos de error y mejores prácticas en el desarrollo del sistema InnoAd.',
 'Sistema de gestión de publicidad digital con módulos de campañas, contenidos, chat y IA integrada. Stack: Java, Spring Boot, PostgreSQL, Angular.',
 2000, 0.8, TRUE),

('ROLE_USUARIO',
 'Eres un asistente IA amigable y servicial. Ayuda al usuario con preguntas sobre cómo usar campañas, crear contenidos, ver estadísticas y resolver dudas generales del sistema InnoAd.',
 'Sistema de gestión de publicidad digital con módulos de campañas, contenidos, chat y IA integrada.',
 2000, 0.6, TRUE);

-- ============================================================================

-- Insertar horarios de atención (Lunes a Viernes, 8:00 AM a 6:00 PM)
INSERT INTO horario_atencion (dia_semana, hora_inicio, hora_fin, activo) VALUES
('LUNES',    '08:00:00', '18:00:00', TRUE),
('MARTES',   '08:00:00', '18:00:00', TRUE),
('MIERCOLES','08:00:00', '18:00:00', TRUE),
('JUEVES',   '08:00:00', '18:00:00', TRUE),
('VIERNES',  '08:00:00', '18:00:00', TRUE),
('SABADO',   '09:00:00', '14:00:00', TRUE),
('DOMINGO',  '10:00:00', '12:00:00', TRUE);

-- ============================================================================

-- Insertar información del sistema
INSERT INTO info_sistema_innoad (clave, valor) VALUES
('nombreEmpresa', 'InnoAd - Soluciones de Publicidad Digital'),
('misionEmpresa', 'Proporcionar soluciones innovadoras de publicidad digital que empoderen a empresas para alcanzar sus objetivos de marketing de manera eficiente y medible.'),
('visionEmpresa', 'Ser la plataforma líder de publicidad digital en Latinoamérica, conocida por su facilidad de uso, potencia de análisis y soporte excepcional.'),
('descripcionSistema', 'Sistema integral de gestión de campañas, contenidos y estadísticas con asistencia inteligente para optimizar estrategias de publicidad digital.'),
('versionActual', '2.0.0'),
('fechaLanzamiento', '2024-06-15'),
('paisPrincipal', 'Colombia');

-- ============================================================================
-- PROCEDIMIENTOS ALMACENADOS ÚTILES
-- ============================================================================

DELIMITER //

-- Obtener el último mensaje de un chat
CREATE PROCEDURE IF NOT EXISTS sp_obtener_ultimo_mensaje(IN p_id_chat BIGINT)
BEGIN
    SELECT m.* FROM mensaje_chat m
    WHERE m.id_chat_usuario = p_id_chat
    ORDER BY m.fecha_creacion DESC
    LIMIT 1;
END //

-- Contar mensajes no leídos de un usuario en un chat
CREATE PROCEDURE IF NOT EXISTS sp_contar_mensajes_no_leidos(IN p_id_chat BIGINT, OUT p_cantidad INT)
BEGIN
    SELECT COUNT(*) INTO p_cantidad FROM mensaje_chat
    WHERE id_chat_usuario = p_id_chat AND leido = FALSE;
END //

-- Obtener estadísticas de un usuario en IA
CREATE PROCEDURE IF NOT EXISTS sp_estadisticas_usuario_ia(IN p_id_usuario BIGINT)
BEGIN
    SELECT 
        COUNT(*) as total_interacciones,
        SUM(CASE WHEN estado = 'COMPLETADA' THEN 1 ELSE 0 END) as completadas,
        SUM(CASE WHEN estado = 'FALLIDA' THEN 1 ELSE 0 END) as fallidas,
        SUM(CASE WHEN estado = 'PROCESANDO' THEN 1 ELSE 0 END) as procesando,
        SUM(tokens_utilizados) as total_tokens,
        AVG(tiempo_respuesta) as tiempo_promedio
    FROM registro_interaccion_ia
    WHERE id_usuario = p_id_usuario;
END //

-- Marcar todos los mensajes de un chat como leídos
CREATE PROCEDURE IF NOT EXISTS sp_marcar_chat_como_leido(IN p_id_chat BIGINT)
BEGIN
    UPDATE mensaje_chat 
    SET leido = TRUE, fecha_lectura = NOW()
    WHERE id_chat_usuario = p_id_chat AND leido = FALSE;
END //

-- Obtener chats activos de un usuario
CREATE PROCEDURE IF NOT EXISTS sp_obtener_chats_usuario(IN p_id_usuario BIGINT)
BEGIN
    SELECT c.*, 
        COUNT(CASE WHEN m.leido = FALSE THEN 1 END) as mensajes_no_leidos
    FROM chat_usuario c
    LEFT JOIN mensaje_chat m ON c.id = m.id_chat_usuario
    WHERE (c.id_usuario_tecnico = p_id_usuario OR c.id_usuario_solicitante = p_id_usuario)
    AND c.activo = TRUE
    GROUP BY c.id
    ORDER BY c.fecha_actualizacion DESC;
END //

DELIMITER ;

-- ============================================================================
-- VISTAS ÚTILES
-- ============================================================================

CREATE OR REPLACE VIEW vw_chats_activos AS
SELECT 
    c.id,
    c.id_usuario_tecnico,
    ut.nombre as nombre_tecnico,
    c.id_usuario_solicitante,
    us.nombre as nombre_solicitante,
    c.activo,
    c.fecha_creacion,
    c.fecha_actualizacion,
    (SELECT COUNT(*) FROM mensaje_chat WHERE id_chat_usuario = c.id AND leido = FALSE) as mensajes_no_leidos,
    (SELECT MAX(fecha_creacion) FROM mensaje_chat WHERE id_chat_usuario = c.id) as fecha_ultimo_mensaje
FROM chat_usuario c
JOIN usuario ut ON c.id_usuario_tecnico = ut.id
JOIN usuario us ON c.id_usuario_solicitante = us.id
WHERE c.activo = TRUE;

-- ============================================================================

CREATE OR REPLACE VIEW vw_solicitudes_pendientes AS
SELECT 
    s.id,
    s.id_usuario,
    u.nombre as nombre_usuario,
    u.email as email_usuario,
    s.descripcion,
    s.estado,
    s.fecha_creacion,
    s.fecha_asignacion,
    s.id_tecnico_asignado,
    ut.nombre as nombre_tecnico_asignado,
    s.fecha_actualizacion,
    DATEDIFF(NOW(), s.fecha_creacion) as dias_transcurridos
FROM solicitud_chat_tecnico s
JOIN usuario u ON s.id_usuario = u.id
LEFT JOIN usuario ut ON s.id_tecnico_asignado = ut.id
WHERE s.estado IN ('PENDIENTE', 'ASIGNADA', 'EN_PROGRESO')
ORDER BY s.fecha_creacion ASC;

-- ============================================================================

CREATE OR REPLACE VIEW vw_estadisticas_ia_usuarios AS
SELECT 
    u.id,
    u.nombre,
    u.email,
    COUNT(ria.id) as total_interacciones,
    SUM(CASE WHEN ria.estado = 'COMPLETADA' THEN 1 ELSE 0 END) as interacciones_completadas,
    SUM(CASE WHEN ria.estado = 'FALLIDA' THEN 1 ELSE 0 END) as interacciones_fallidas,
    SUM(ria.tokens_utilizados) as total_tokens_utilizados,
    ROUND(AVG(ria.tiempo_respuesta), 2) as tiempo_respuesta_promedio,
    MAX(ria.fecha_creacion) as ultima_interaccion
FROM usuario u
LEFT JOIN registro_interaccion_ia ria ON u.id = ria.id_usuario
GROUP BY u.id, u.nombre, u.email
ORDER BY total_interacciones DESC;

-- ============================================================================
-- ÍNDICES ADICIONALES PARA OPTIMIZACIÓN
-- ============================================================================

CREATE INDEX IF NOT EXISTS idx_chat_usuario_composite ON chat_usuario(id_usuario_tecnico, activo, fecha_actualizacion);
CREATE INDEX IF NOT EXISTS idx_mensaje_chat_composite ON mensaje_chat(id_chat_usuario, leido, fecha_creacion);
CREATE INDEX IF NOT EXISTS idx_interaccion_ia_composite ON registro_interaccion_ia(id_usuario, estado, fecha_creacion);
CREATE INDEX IF NOT EXISTS idx_email_ia_composite ON registro_email_ia(id_usuario, estado, fecha_creacion);

-- ============================================================================
-- TRIGGERS PARA AUDITORÍA
-- ============================================================================

DELIMITER //

CREATE TRIGGER IF NOT EXISTS tr_actualizar_chat_en_nuevo_mensaje
AFTER INSERT ON mensaje_chat
FOR EACH ROW
BEGIN
    UPDATE chat_usuario 
    SET fecha_actualizacion = NOW()
    WHERE id = NEW.id_chat_usuario;
END //

CREATE TRIGGER IF NOT EXISTS tr_marcar_chat_activo_al_crear
AFTER INSERT ON mensaje_chat
FOR EACH ROW
BEGIN
    UPDATE chat_usuario
    SET activo = TRUE
    WHERE id = NEW.id_chat_usuario AND activo = FALSE;
END //

CREATE TRIGGER IF NOT EXISTS tr_validar_mensaje_en_chat_activo
BEFORE INSERT ON mensaje_chat
FOR EACH ROW
BEGIN
    IF NOT EXISTS (SELECT 1 FROM chat_usuario WHERE id = NEW.id_chat_usuario AND activo = TRUE) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No se pueden enviar mensajes en un chat cerrado';
    END IF;
END //

DELIMITER ;

-- ============================================================================
-- VERIFICACIÓN FINAL
-- ============================================================================

SELECT '✅ Migración de Chat e IA completada exitosamente' as estado;

SELECT TABLE_NAME, TABLE_ROWS 
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME IN (
    'chat_usuario',
    'mensaje_chat', 
    'solicitud_chat_tecnico',
    'prompt_ia_por_rol',
    'horario_atencion',
    'info_sistema_innoad',
    'email_configurado',
    'registro_email_ia',
    'registro_interaccion_ia'
)
ORDER BY TABLE_NAME;
