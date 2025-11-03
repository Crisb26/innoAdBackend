@echo off
echo =========================================
echo InnoAd Backend - Deploy Script
echo =========================================
echo.

echo [1/3] Limpiando y compilando el proyecto...
call mvn clean package -DskipTests -q
if %errorlevel% neq 0 (
    echo ERROR: La compilacion fallo
    pause
    exit /b 1
)
echo Compilacion exitosa!
echo.

echo [2/3] Verificando JAR generado...
if not exist target\innoad-backend-2.0.0.jar (
    echo ERROR: No se encontro el JAR generado
    pause
    exit /b 1
)
echo JAR encontrado: target\innoad-backend-2.0.0.jar
echo.

echo [3/3] Iniciando el backend en puerto 8081...
echo.
echo =========================================
echo Backend InnoAd - ARRANCANDO
echo =========================================
echo Puerto: 8081
echo Health: http://localhost:8081/actuator/health
echo Swagger: http://localhost:8081/swagger-ui.html
echo =========================================
echo.
echo Presiona Ctrl+C para detener el servidor
echo.

java -jar target\innoad-backend-2.0.0.jar

pause
