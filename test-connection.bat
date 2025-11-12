@echo off
REM ============================================
REM InnoAd - Prueba de Conexion Frontend-Backend
REM ============================================

echo.
echo ========================================
echo   PRUEBA DE CONEXION
echo   Frontend ^<-^> Backend
echo ========================================
echo.

echo [1/5] Verificando Backend (http://localhost:8080)...
curl -f -s http://localhost:8080/actuator/health >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Backend no responde en http://localhost:8080
    echo Ejecuta: deploy.bat para iniciar el backend
    goto ERROR
) else (
    echo [OK] Backend esta activo
)

echo.
echo [2/5] Verificando Base de Datos...
curl -f -s http://localhost:8080/actuator/health | find "UP" >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Base de datos no conectada
    goto ERROR
) else (
    echo [OK] Base de datos conectada
)

echo.
echo [3/5] Verificando API de Autenticacion...
curl -f -s http://localhost:8080/api/v1/auth/health >nul 2>&1
if errorlevel 1 (
    echo [WARNING] Endpoint de auth no responde (puede ser normal)
) else (
    echo [OK] API de autenticacion disponible
)

echo.
echo [4/5] Verificando CORS...
echo Probando headers CORS desde el frontend...
powershell -Command "$response = Invoke-WebRequest -Uri 'http://localhost:8080/actuator/health' -Method OPTIONS -Headers @{'Origin'='http://localhost:4200'} -UseBasicParsing; if ($response.Headers['Access-Control-Allow-Origin']) { exit 0 } else { exit 1 }" >nul 2>&1
if errorlevel 1 (
    echo [WARNING] CORS podria no estar configurado correctamente
) else (
    echo [OK] CORS configurado
)

echo.
echo [5/5] Verificando Swagger/OpenAPI...
curl -f -s http://localhost:8080/swagger-ui.html >nul 2>&1
if errorlevel 1 (
    echo [WARNING] Swagger UI no disponible
) else (
    echo [OK] Swagger UI disponible
)

echo.
echo ========================================
echo   CONFIGURACION DEL FRONTEND
echo ========================================
echo.
echo El frontend debe conectarse a:
echo - API Base URL: http://localhost:8080/api/v1
echo - WebSocket:   ws://localhost:8080/ws
echo.
echo Proxy configurado: proxy.conf.json
echo Environment:       src/environments/environment.ts
echo.

echo ========================================
echo   ENDPOINTS DISPONIBLES
echo ========================================
echo.
echo Auth:      http://localhost:8080/api/v1/auth
echo Users:     http://localhost:8080/api/v1/users
echo Content:   http://localhost:8080/api/v1/contents
echo Screens:   http://localhost:8080/api/v1/screens
echo Admin:     http://localhost:8080/api/admin
echo Health:    http://localhost:8080/actuator/health
echo Swagger:   http://localhost:8080/swagger-ui.html
echo.

echo ========================================
echo   INICIAR FRONTEND
echo ========================================
echo.
echo Para iniciar el frontend, ejecuta:
echo.
echo cd ..\..\..\FRONTEND\innoadFrontend
echo npm start
echo.
echo O con proxy:
echo npm run iniciar:proxy
echo.

echo ========================================
echo   PRUEBA COMPLETADA
echo ========================================
echo.
goto FIN

:ERROR
echo.
echo ========================================
echo   ERROR EN LA PRUEBA
echo ========================================
echo.
echo Verifica que:
echo 1. Backend este corriendo (deploy.bat)
echo 2. PostgreSQL este activo
echo 3. Puerto 8080 no este ocupado
echo.

:FIN
pause
