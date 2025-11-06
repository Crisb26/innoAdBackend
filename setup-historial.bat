@echo off
REM Script para configurar historial persistente en CMD

echo Configurando historial de comandos mejorado...

REM Crear directorio para historial si no existe
if not exist "%USERPROFILE%\.cmd_history" mkdir "%USERPROFILE%\.cmd_history"

REM Configurar variables de entorno para historial
setx DOSKEY_HISTORY_SIZE "9999"
setx DOSKEY_HISTORY_FILE "%USERPROFILE%\.cmd_history\cmd_history.txt"

REM Crear macro para guardar historial
doskey /history > "%USERPROFILE%\.cmd_history\cmd_history.txt"

REM Crear aliases utiles para InnoAd
doskey proj=cd /d "c:\Users\bueno\Desktop\PROYECTO FINAL INNOAD\innoadBackend"
doskey back=cd /d "c:\Users\bueno\Desktop\PROYECTO FINAL INNOAD\innoadBackend"
doskey gs=git status
doskey ga=git add -A
doskey gc=git commit -m $*
doskey gp=git push origin main
doskey gl=git log --oneline -10
doskey mrun=mvn spring-boot:run
doskey mcompile=mvn compile
doskey mpackage=mvn clean package -DskipTests
doskey swagger=start http://localhost:8080/swagger-ui.html
doskey h2=start http://localhost:8080/h2-console
doskey deploy=mvn clean package -DskipTests $T java -jar target/innoad-backend-2.0.0.jar
doskey cls-history=del /q "%USERPROFILE%\.cmd_history\cmd_history.txt"
doskey save-history=doskey /history $G$G "%USERPROFILE%\.cmd_history\cmd_history.txt"

echo.
echo ✅ Configuracion completada!
echo.
echo ALIASES DISPONIBLES:
echo   proj        - Ir al directorio del proyecto
echo   back        - Ir al directorio del backend
echo   gs          - git status
echo   ga          - git add -A
echo   gc [msg]    - git commit -m "mensaje"
echo   gp          - git push origin main
echo   gl          - git log --oneline -10
echo   mrun        - mvn spring-boot:run
echo   mcompile    - mvn compile
echo   mpackage    - mvn clean package -DskipTests
echo   swagger     - Abrir Swagger UI
echo   h2          - Abrir H2 Console
echo   deploy      - Compilar y ejecutar JAR
echo   save-history - Guardar historial actual
echo.
echo Para usar estos aliases, reinicia la terminal o ejecuta este script nuevamente.
echo.
pause