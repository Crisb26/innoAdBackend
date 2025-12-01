@echo off
REM Script para verificar la conexión a Azure PostgreSQL
REM Requiere: psql (PostgreSQL Client)

echo ========================================
echo VERIFICACION DE CONEXION A BD AZURE
echo ========================================
echo.

REM Variables de conexión
set PGHOST=innoad-postgres.postgres.database.azure.com
set PGPORT=5432
set PGUSER=admininnoad
set PGDATABASE=innoad_db
set PGSSLMODE=require

echo Configuración de conexión:
echo - Host: %PGHOST%
echo - Puerto: %PGPORT%
echo - Usuario: %PGUSER%
echo - Base de datos: %PGDATABASE%
echo - SSL: Requerido
echo.

REM Verificar si psql está instalado
echo [1/3] Verificando si PostgreSQL Client está instalado...
psql --version > nul 2>&1
if errorlevel 1 (
    echo ERROR: psql no está instalado
    echo Descárgalo desde: https://www.postgresql.org/download/
    echo.
    pause
    exit /b 1
)
echo ✓ PostgreSQL Client está instalado
echo.

REM Solicitar contraseña
echo [2/3] Ingresa la contraseña de la BD:
set /p PGPASSWORD="Contraseña: "
echo.

REM Intentar conexión
echo [3/3] Intentando conectarse a la base de datos...
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d %PGDATABASE% -c "SELECT version();" 2>&1
if errorlevel 1 (
    echo.
    echo ERROR: No se pudo conectar a la base de datos
    echo Verifica:
    echo - La contraseña sea correcta
    echo - La base de datos esté en "Ready" state en Azure
    echo - Tu firewall permita conexiones a PostgreSQL
    echo.
) else (
    echo.
    echo ✓ CONEXION EXITOSA A LA BASE DE DATOS
    echo.
    echo Comandos útiles:
    echo   \dt              - Listar tablas
    echo   \d [tabla]       - Describir tabla
    echo   SELECT COUNT(*) FROM usuarios;  - Contar registros
    echo   \q              - Salir
    echo.
)

pause
