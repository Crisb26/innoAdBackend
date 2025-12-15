-- ============================================================================
-- INNOAD FASE 4 - SCRIPT DE CREACIÓN DE TABLAS Y DATOS INICIALES
-- ============================================================================
-- Este script crea todas las tablas necesarias para la Fase 4 (Sistema de 
-- Ubicaciones y Publicaciones) en SQL Server / Azure SQL Database
-- ============================================================================

-- ============================================================================
-- 1. TABLA: CIUDADES
-- ============================================================================
CREATE TABLE ciudades (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    nombre NVARCHAR(100) NOT NULL UNIQUE,
    codigo NVARCHAR(10) NOT NULL UNIQUE,
    cantidad_lugares INT NOT NULL DEFAULT 0,
    activa BIT NOT NULL DEFAULT 1,
    fecha_creacion DATETIME DEFAULT GETDATE()
);

PRINT 'Tabla CIUDADES creada exitosamente.';

-- ============================================================================
-- 2. TABLA: LUGARES
-- ============================================================================
CREATE TABLE lugares (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    ciudad_id BIGINT NOT NULL FOREIGN KEY REFERENCES ciudades(id) ON DELETE CASCADE,
    nombre NVARCHAR(200) NOT NULL,
    pisos INT NOT NULL DEFAULT 1,
    costo_base DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    disponible BIT NOT NULL DEFAULT 1,
    fecha_creacion DATETIME DEFAULT GETDATE(),
    CONSTRAINT uk_lugares_ciudad_nombre UNIQUE (ciudad_id, nombre)
);

PRINT 'Tabla LUGARES creada exitosamente.';

-- ============================================================================
-- 3. TABLA: PISOS
-- ============================================================================
CREATE TABLE pisos (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    lugar_id BIGINT NOT NULL FOREIGN KEY REFERENCES lugares(id) ON DELETE CASCADE,
    numero INT NOT NULL,
    disponible BIT NOT NULL DEFAULT 1,
    costo_por_dia DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    fecha_creacion DATETIME DEFAULT GETDATE(),
    CONSTRAINT uk_pisos_lugar_numero UNIQUE (lugar_id, numero)
);

PRINT 'Tabla PISOS creada exitosamente.';

-- ============================================================================
-- 4. TABLA: PUBLICACIONES
-- ============================================================================
CREATE TABLE publicaciones (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    titulo NVARCHAR(200) NOT NULL,
    descripcion NVARCHAR(MAX),
    tipo_contenido NVARCHAR(20) NOT NULL CHECK (tipo_contenido IN ('IMAGEN', 'VIDEO')),
    archivo_url NVARCHAR(500),
    duracion_dias INT NOT NULL DEFAULT 30,
    estado NVARCHAR(20) NOT NULL DEFAULT 'PENDIENTE' 
        CHECK (estado IN ('PENDIENTE', 'APROBADO', 'RECHAZADO', 'PUBLICADO')),
    usuario_id BIGINT NOT NULL,
    costo_total DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    ubicaciones_json NVARCHAR(MAX), -- JSON array
    fecha_creacion DATETIME DEFAULT GETDATE(),
    fecha_aprobacion DATETIME,
    fecha_rechazo DATETIME,
    motivo_rechazo NVARCHAR(MAX),
    fecha_publicacion DATETIME,
    fecha_finalizacion DATETIME
);

PRINT 'Tabla PUBLICACIONES creada exitosamente.';

-- ============================================================================
-- 5. ÍNDICES PARA OPTIMIZACIÓN
-- ============================================================================

-- Índices de CIUDADES
CREATE INDEX idx_ciudades_codigo ON ciudades(codigo);
CREATE INDEX idx_ciudades_activa ON ciudades(activa);

-- Índices de LUGARES
CREATE INDEX idx_lugares_ciudad ON lugares(ciudad_id);
CREATE INDEX idx_lugares_disponible ON lugares(disponible);
CREATE INDEX idx_lugares_codigo ON lugares(nombre);

-- Índices de PISOS
CREATE INDEX idx_pisos_lugar ON pisos(lugar_id);
CREATE INDEX idx_pisos_disponible ON pisos(disponible);

-- Índices de PUBLICACIONES
CREATE INDEX idx_publicaciones_usuario ON publicaciones(usuario_id);
CREATE INDEX idx_publicaciones_estado ON publicaciones(estado);
CREATE INDEX idx_publicaciones_fecha_creacion ON publicaciones(fecha_creacion DESC);
CREATE INDEX idx_publicaciones_usuario_estado ON publicaciones(usuario_id, estado);
CREATE INDEX idx_publicaciones_estado_fecha ON publicaciones(estado, fecha_creacion DESC);

PRINT 'Índices creados exitosamente.';

-- ============================================================================
-- 6. DATOS INICIALES: CIUDADES
-- ============================================================================
INSERT INTO ciudades (nombre, codigo, cantidad_lugares, activa) VALUES
(N'Bogotá', N'BOG', 3, 1),
(N'Medellín', N'MDE', 3, 1),
(N'Cali', N'CAL', 2, 1),
(N'Barranquilla', N'BAQ', 2, 1),
(N'Bucaramanga', N'BUC', 1, 1);

PRINT 'Datos iniciales de CIUDADES insertados: 5 ciudades.';

-- ============================================================================
-- 7. DATOS INICIALES: LUGARES (BOGOTÁ)
-- ============================================================================
INSERT INTO lugares (ciudad_id, nombre, pisos, costo_base, disponible) VALUES
-- Bogotá
(1, N'Centro Comercial Andino', 5, 500.00, 1),
(1, N'Centro Comercial Titán', 6, 550.00, 1),
(1, N'Centro Comercial Unicentro', 4, 450.00, 1),

-- Medellín
(2, N'Centro Comercial Mayorca', 5, 480.00, 1),
(2, N'Centro Comercial Éxito', 6, 520.00, 1),
(2, N'Centro Comercial Paseo Peatonal', 4, 400.00, 1),

-- Cali
(3, N'Centro Comercial Chipichape', 5, 450.00, 1),
(3, N'Centro Comercial Premium', 4, 500.00, 1),

-- Barranquilla
(4, N'Centro Comercial Buenavista', 4, 420.00, 1),
(4, N'Centro Comercial Paseo', 3, 380.00, 1),

-- Bucaramanga
(5, N'Centro Comercial Megamall', 4, 440.00, 1);

PRINT 'Datos iniciales de LUGARES insertados: 11 centros comerciales.';

-- ============================================================================
-- 8. DATOS INICIALES: PISOS (Centro Comercial Andino)
-- ============================================================================
INSERT INTO pisos (lugar_id, numero, disponible, costo_por_dia) VALUES
-- Centro Comercial Andino (lugar_id = 1)
(1, 1, 1, 50.00),
(1, 2, 1, 50.00),
(1, 3, 1, 55.00),
(1, 4, 1, 55.00),
(1, 5, 1, 60.00),

-- Centro Comercial Titán (lugar_id = 2)
(2, 1, 1, 55.00),
(2, 2, 1, 55.00),
(2, 3, 1, 60.00),
(2, 4, 1, 60.00),
(2, 5, 1, 65.00),
(2, 6, 1, 65.00),

-- Centro Comercial Unicentro (lugar_id = 3)
(3, 1, 1, 45.00),
(3, 2, 1, 45.00),
(3, 3, 1, 50.00),
(3, 4, 1, 50.00),

-- Centro Comercial Mayorca (lugar_id = 4)
(4, 1, 1, 48.00),
(4, 2, 1, 48.00),
(4, 3, 1, 52.00),
(4, 4, 1, 52.00),
(4, 5, 1, 58.00),

-- Centro Comercial Éxito (lugar_id = 5)
(5, 1, 1, 52.00),
(5, 2, 1, 52.00),
(5, 3, 1, 58.00),
(5, 4, 1, 58.00),
(5, 5, 1, 62.00),
(5, 6, 1, 62.00),

-- Centro Comercial Paseo Peatonal (lugar_id = 6)
(6, 1, 1, 40.00),
(6, 2, 1, 40.00),
(6, 3, 1, 45.00),
(6, 4, 1, 45.00),

-- Centro Comercial Chipichape (lugar_id = 7)
(7, 1, 1, 45.00),
(7, 2, 1, 45.00),
(7, 3, 1, 50.00),
(7, 4, 1, 50.00),
(7, 5, 1, 55.00),

-- Centro Comercial Premium (lugar_id = 8)
(8, 1, 1, 50.00),
(8, 2, 1, 50.00),
(8, 3, 1, 55.00),
(8, 4, 1, 55.00),

-- Centro Comercial Buenavista (lugar_id = 9)
(9, 1, 1, 42.00),
(9, 2, 1, 42.00),
(9, 3, 1, 48.00),
(9, 4, 1, 48.00),

-- Centro Comercial Paseo (lugar_id = 10)
(10, 1, 1, 38.00),
(10, 2, 1, 38.00),
(10, 3, 1, 42.00),

-- Centro Comercial Megamall (lugar_id = 11)
(11, 1, 1, 44.00),
(11, 2, 1, 44.00),
(11, 3, 1, 50.00),
(11, 4, 1, 50.00);

PRINT 'Datos iniciales de PISOS insertados: 49 pisos.';

-- ============================================================================
-- 9. DATOS INICIALES: PUBLICACIONES (Ejemplos)
-- ============================================================================
INSERT INTO publicaciones 
(titulo, descripcion, tipo_contenido, archivo_url, duracion_dias, estado, usuario_id, costo_total, ubicaciones_json, fecha_creacion)
VALUES
(
    N'Promoción Especial Enero',
    N'Gran descuento en nuestros productos durante todo enero. Aprovecha esta oportunidad.',
    N'VIDEO',
    N'/uploads/video_20240115_143022_123.mp4',
    30,
    N'PUBLICADO',
    1,
    1500.00,
    N'[{"ciudadId":1,"lugarId":1,"pisoId":1}]',
    DATEADD(DAY, -5, GETDATE())
),
(
    N'Lanzamiento Nuevo Producto',
    N'Presentamos el nuevo producto de la temporada. No te lo pierdas.',
    N'VIDEO',
    N'/uploads/video_20240110_100000_456.mp4',
    60,
    N'PUBLICADO',
    2,
    2500.00,
    N'[{"ciudadId":1,"lugarId":2,"pisoId":7},{"ciudadId":1,"lugarId":2,"pisoId":8}]',
    DATEADD(DAY, -3, GETDATE())
),
(
    N'Evento Especial - Únicamente este fin de semana',
    N'Ven y disfruta con tu familia de nuestro evento especial.',
    N'IMAGEN',
    N'/uploads/imagen_20240112_150000_789.jpg',
    7,
    N'APROBADO',
    3,
    500.00,
    N'[{"ciudadId":2,"lugarId":4,"pisoId":15}]',
    DATEADD(DAY, -2, GETDATE())
),
(
    N'Oferta por Tiempo Limitado',
    N'Apenas quedan 50 productos con descuento. Corre y compra ahora.',
    N'IMAGEN',
    N'/uploads/imagen_20240114_090000_321.jpg',
    15,
    N'PENDIENTE',
    4,
    750.00,
    N'[{"ciudadId":3,"lugarId":7,"pisoId":33}]',
    GETDATE()
);

PRINT 'Datos iniciales de PUBLICACIONES insertados: 4 publicaciones de ejemplo.';

-- ============================================================================
-- 10. VERIFICACIÓN FINAL
-- ============================================================================

PRINT '';
PRINT '=============================================================';
PRINT 'RESUMEN DE DATOS INSERTADOS';
PRINT '=============================================================';

SELECT 'Ciudades' AS [Tabla], COUNT(*) AS [Cantidad] FROM ciudades
UNION ALL
SELECT 'Lugares', COUNT(*) FROM lugares
UNION ALL
SELECT 'Pisos', COUNT(*) FROM pisos
UNION ALL
SELECT 'Publicaciones', COUNT(*) FROM publicaciones;

PRINT '';
PRINT 'Script completado exitosamente.';
PRINT '=============================================================';

-- ============================================================================
-- FIN DEL SCRIPT
-- ============================================================================
