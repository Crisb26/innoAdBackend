@echo off
REM Script para verificar el despliegue en Azure
REM Requiere tener Azure CLI instalado: https://learn.microsoft.com/es-es/cli/azure/install-azure-cli

echo ========================================
echo VERIFICACION DE DESPLIEGUE EN AZURE
echo ========================================
echo.

REM Verificar conexión a Azure
echo [1/6] Verificando conexión a Azure CLI...
az version > nul 2>&1
if errorlevel 1 (
    echo ERROR: Azure CLI no está instalado. Instálalo desde: https://learn.microsoft.com/es-es/cli/azure/install-azure-cli
    pause
    exit /b 1
)
echo ✓ Azure CLI instalado correctamente
echo.

REM Verificar si está autenticado
echo [2/6] Verificando autenticación en Azure...
az account show > nul 2>&1
if errorlevel 1 (
    echo ERROR: No estás autenticado en Azure. Ejecuta: az login
    pause
    exit /b 1
)
echo ✓ Autenticado en Azure
echo.

REM Obtener información del Container App
echo [3/6] Obteniendo información del Container App...
az containerapp show --name innoad-backend --resource-group rg-adso-app --query "{nombre:name, estado:properties.provisioningState, region:location}" --output table
echo.

REM Obtener variables de entorno
echo [4/6] Verificando variables de entorno del Container App...
echo IMPORTANTE: Las siguientes variables deben estar configuradas:
echo - SPRING_PROFILES_ACTIVE
echo - AZURE_DB_HOST
echo - AZURE_DB_PORT
echo - AZURE_DB_NAME
echo - AZURE_DB_USER
echo - AZURE_DB_PASSWORD
echo.
az containerapp secrets list --name innoad-backend --resource-group rg-adso-app --output table 2>&1 || echo "No hay secretos configurados"
echo.

REM Verificar la base de datos
echo [5/6] Verificando Base de Datos PostgreSQL...
az postgres flexible-server show --name innoad-postgres --resource-group innoad --query "{nombre:name, estado:state, version:version}" --output table
echo.

REM Obtener la URL del backend
echo [6/6] URL del Backend desplegado:
echo.
az containerapp show --name innoad-backend --resource-group rg-adso-app --query "properties.configuration.ingress.fqdn" --output tsv
echo.

echo ========================================
echo VERIFICACION COMPLETADA
echo ========================================
echo.
echo Próximos pasos:
echo 1. Abre en tu navegador la URL mostrada arriba
echo 2. Accede a /swagger-ui.html para ver la documentación de la API
echo 3. Verifica que la base de datos está conectada
echo.
pause
