-- Script para crear la base de datos InnoAd en PostgreSQL
-- Ejecutar este script en pgAdmin o psql

-- PASO 1: Crear la base de datos
CREATE DATABASE innoad_db
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    CONNECTION LIMIT = -1;

-- PASO 2: Conectarse a la base de datos recien creada
-- En pgAdmin: Seleccionar innoad_db de la lista de bases de datos
-- En psql: \c innoad_db

-- PASO 3: Las tablas se crean automaticamente al iniciar el backend
-- Hibernate las creara basandose en las entidades Java
