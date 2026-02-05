-- Script de inicialización para PostgreSQL
-- Solo ejecuta el contenido de la base de datos sin intentar borrar

\c innoad_db;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- El resto del script continúa igual que DATABASE-SCRIPT.sql
-- (Tablas, índices, funciones, etc.)
