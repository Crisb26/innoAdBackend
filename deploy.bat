@echo off
REM ============================================
REM InnoAd - Script de Despliegue Completo
REM Backend + Frontend + PostgreSQL
REM ============================================

echo.
echo ========================================
echo   INNOAD - DESPLIEGUE COMPLETO
echo ========================================
echo.

REM Verificar Docker
docker --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker no esta instalado o no esta en el PATH
    echo Por favor instala Docker Desktop: https://www.docker.com/products/docker-desktop
    pause
    exit /b 1
)

echo [OK] Docker detectado
echo.

REM Verificar Docker Compose
docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker Compose no esta instalado
    pause
    exit /b 1
)

echo [OK] Docker Compose detectado
echo.

REM Preguntar modo de despliegue
echo Selecciona el modo de despliegue:
echo.
echo 1) Despliegue COMPLETO (Backend + Frontend + PostgreSQL)
echo 2) Solo BACKEND + PostgreSQL
echo 3) Solo BACKEND (usar BD externa)
echo.
set /p MODO="Opcion (1-3): "

if "%MODO%"=="1" goto COMPLETO
if "%MODO%"=="2" goto BACKEND_DB
if "%MODO%"=="3" goto SOLO_BACKEND

echo [ERROR] Opcion invalida
pause
exit /b 1

:COMPLETO
echo.
echo ========================================
echo   DESPLIEGUE COMPLETO
echo ========================================
echo.
echo Construyendo imagenes...
docker-compose -f docker-compose.full.yml build

echo.
echo Iniciando servicios...
docker-compose -f docker-compose.full.yml up -d

echo.
echo Esperando que los servicios esten listos...
timeout /t 30 /nobreak

echo.
echo ========================================
echo   ESTADO DE LOS SERVICIOS
echo ========================================
docker-compose -f docker-compose.full.yml ps

echo.
echo ========================================
echo   ACCESO A LA APLICACION
echo ========================================
echo.
echo Frontend:  http://localhost
echo Backend:   http://localhost:8080
echo API Docs:  http://localhost:8080/swagger-ui.html
echo Health:    http://localhost:8080/actuator/health
echo.
goto FIN

:BACKEND_DB
echo.
echo ========================================
echo   BACKEND + POSTGRESQL
echo ========================================
echo.
echo Construyendo imagenes...
docker-compose -f docker-compose.full.yml build backend postgres

echo.
echo Iniciando servicios...
docker-compose -f docker-compose.full.yml up -d postgres backend

echo.
echo Esperando que los servicios esten listos...
timeout /t 20 /nobreak

echo.
echo ========================================
echo   ESTADO DE LOS SERVICIOS
echo ========================================
docker-compose -f docker-compose.full.yml ps

echo.
echo ========================================
echo   ACCESO A LA APLICACION
echo ========================================
echo.
echo Backend:   http://localhost:8080
echo API Docs:  http://localhost:8080/swagger-ui.html
echo Health:    http://localhost:8080/actuator/health
echo Database:  localhost:5432 (innoad_db)
echo.
goto FIN

:SOLO_BACKEND
echo.
echo ========================================
echo   SOLO BACKEND
echo ========================================
echo.
echo [NOTA] Asegurate de tener PostgreSQL corriendo
echo.
echo Construyendo imagen...
docker-compose build backend

echo.
echo Iniciando backend...
docker-compose up -d backend

echo.
echo Esperando que el backend este listo...
timeout /t 15 /nobreak

echo.
echo ========================================
echo   ESTADO DEL SERVICIO
echo ========================================
docker-compose ps

echo.
echo ========================================
echo   ACCESO A LA APLICACION
echo ========================================
echo.
echo Backend:   http://localhost:8080
echo API Docs:  http://localhost:8080/swagger-ui.html
echo Health:    http://localhost:8080/actuator/health
echo.
goto FIN

:FIN
echo.
echo ========================================
echo   COMANDOS UTILES
echo ========================================
echo.
echo Ver logs:         docker-compose -f docker-compose.full.yml logs -f
echo Detener:          docker-compose -f docker-compose.full.yml down
echo Reiniciar:        docker-compose -f docker-compose.full.yml restart
echo Ver estado:       docker-compose -f docker-compose.full.yml ps
echo.
echo ========================================
echo   DESPLIEGUE COMPLETADO
echo ========================================
echo.
pause
