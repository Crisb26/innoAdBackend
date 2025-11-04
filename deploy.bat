@echo off
cd /d "%~dp0"

echo InnoAd Backend - Deploy
echo.

echo [1/3] Compilando proyecto...
call mvn clean package -DskipTests -q
if %errorlevel% neq 0 (
    echo ERROR: Compilacion fallida
    pause
    exit /b 1
)
echo OK

echo.
echo [2/3] Verificando JAR...
if not exist target\innoad-backend-2.0.0.jar (
    echo ERROR: JAR no encontrado
    pause
    exit /b 1
)
echo OK

echo.
echo [3/3] Iniciando backend en puerto 8081...
echo.
echo ========================================
echo Backend: http://localhost:8081
echo Health:  http://localhost:8081/actuator/health
echo API Docs: http://localhost:8081/swagger-ui.html
echo ========================================
echo.

java -jar target\innoad-backend-2.0.0.jar
pause
