@echo off
echo.
echo ========================================
echo   COMANDOS RAPIDOS INNOAD BACKEND
echo ========================================
echo.
echo [1] Iniciar Backend
echo [2] Compilar Backend
echo [3] Git Status
echo [4] Git Add y Commit
echo [5] Git Push
echo [6] Ver Logs
echo [7] Limpiar y Compilar
echo [8] Swagger UI (abrir)
echo [9] H2 Console (abrir)
echo [0] Salir
echo.
set /p choice="Selecciona una opcion (0-9): "

if "%choice%"=="1" goto iniciar
if "%choice%"=="2" goto compilar
if "%choice%"=="3" goto status
if "%choice%"=="4" goto commit
if "%choice%"=="5" goto push
if "%choice%"=="6" goto logs
if "%choice%"=="7" goto clean
if "%choice%"=="8" goto swagger
if "%choice%"=="9" goto h2
if "%choice%"=="0" goto fin

:iniciar
echo.
echo Iniciando backend en puerto 8080...
mvn spring-boot:run
goto fin

:compilar
echo.
echo Compilando proyecto...
mvn clean package -DskipTests
echo.
echo Compilacion completada!
pause
goto menu

:status
echo.
echo Estado de Git:
git status
echo.
pause
goto menu

:commit
echo.
set /p mensaje="Ingresa mensaje del commit: "
git add -A
git commit -m "%mensaje%"
echo.
echo Commit realizado!
pause
goto menu

:push
echo.
echo Haciendo push a GitHub...
git push origin main
echo.
echo Push completado!
pause
goto menu

:logs
echo.
echo Ultimos 10 commits:
git log --oneline -10
echo.
pause
goto menu

:clean
echo.
echo Limpiando y compilando...
mvn clean
mvn compile
echo.
echo Proceso completado!
pause
goto menu

:swagger
echo.
echo Abriendo Swagger UI...
start http://localhost:8080/swagger-ui.html
goto menu

:h2
echo.
echo Abriendo H2 Console...
start http://localhost:8080/h2-console
goto menu

:menu
echo.
goto inicio

:fin
echo.
echo Hasta luego!
pause
exit