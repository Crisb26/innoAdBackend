@echo off
cd /d "c:\Users\bueno\Desktop\PROYECTO FINAL INNOAD\innoadBackend"
echo Compilando backend...
call mvn clean package -DskipTests -q
if errorlevel 1 (
    echo Error en compilacion
    exit /b 1
)
echo.
echo Iniciando backend en puerto 8080...
java -jar target\innoad-backend-2.0.0.jar
