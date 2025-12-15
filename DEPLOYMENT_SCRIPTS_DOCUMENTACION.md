# 📦 DEPLOYMENT SCRIPTS - DOCUMENTACIÓN COMPLETA

## 🎯 QUÉ HEMOS CREADO

Se han creado **6 scripts** + **1 guía** para automatizar el deployment completo:

### Scripts de Deployment

| Script | Tiempo | Propósito |
|--------|--------|-----------|
| `00-verificar-todo.bat` | 5 min | Verifica que tienes todas las herramientas |
| `01-build-docker-local.bat` | 20 min | Compila y construye imágenes Docker locales |
| `02-push-to-azure.bat` | 15 min | Envía imágenes a Azure Container Registry |
| `03-execute-sql.bat` | 10 min | Ejecuta script SQL en Azure |
| `04-deploy-to-azure-app-service.bat` | 30 min | Despliega backend en App Service |
| `05-deploy-frontend-netlify.bat` | 15 min | Despliega frontend en Netlify |
| `DEPLOYMENT_AUTOMATICO_COMPLETO.bat` | 90 min | **TODO DE UNA VEZ** (compilación local) |

### Documentación

| Archivo | Propósito |
|---------|-----------|
| `GUIA_EJECUCION_RAPIDA.md` | Guía paso a paso con ejemplos |

---

## 🚀 CÓMO USAR

### OPCIÓN 1: Automatizado TODO (RECOMENDADO)

```bash
DEPLOYMENT_AUTOMATICO_COMPLETO.bat
```

**Qué hace:**
- Compila backend (Maven)
- Compila frontend (npm)
- Construye imágenes Docker
- Te muestra pasos manuales para Azure

**Tiempo:** 90 minutos (mitad automático, mitad manual)

---

### OPCIÓN 2: Paso a Paso Manual

```bash
REM Paso 1: Verificar herramientas
00-verificar-todo.bat

REM Paso 2: Compilar y construir Docker
01-build-docker-local.bat

REM Paso 3: Enviar a Azure
02-push-to-azure.bat

REM Paso 4: Ejecutar SQL
03-execute-sql.bat

REM Paso 5: Deploy backend
04-deploy-to-azure-app-service.bat

REM Paso 6: Deploy frontend
05-deploy-frontend-netlify.bat
```

**Tiempo:** 90 minutos (separado en pasos)

---

## 📋 CHECKLIST DE EJECUCIÓN

### Antes de empezar

- [ ] Tienes Docker instalado (`docker --version`)
- [ ] Tienes Java 21 instalado (`java -version`)
- [ ] Tienes Maven instalado (`mvn --version`)
- [ ] Tienes Node.js instalado (`node --version`)
- [ ] Tienes Azure CLI instalado (`az --version`)
- [ ] Tienes credenciales de Azure
- [ ] Tienes credenciales de Netlify

### Ejecución

- [ ] Ejecutar `00-verificar-todo.bat`
- [ ] Ejecutar `01-build-docker-local.bat` (esperar 20 min)
- [ ] Ejecutar `02-push-to-azure.bat` (esperar 15 min)
- [ ] Ejecutar `03-execute-sql.bat` (esperar 10 min)
- [ ] Ejecutar `04-deploy-to-azure-app-service.bat` (esperar 30 min)
- [ ] Ejecutar `05-deploy-frontend-netlify.bat` (esperar 15 min)

### Después de completar

- [ ] Backend responde: `curl https://[app].azurewebsites.net/api/ubicaciones/ciudades`
- [ ] Frontend carga sin errores: `https://[sitio].netlify.app`
- [ ] CORS funciona correctamente
- [ ] Base de datos tiene datos (ejecutar query)

---

## 🛠️ CONFIGURACIONES NECESARIAS

### 1. Azure Container Registry

**Necesitas:**
- Nombre: `innoadregistry`
- URL: `innoadregistry.azurecr.io`
- Usuario: admin user
- Contraseña: access token

**Obtener credenciales:**
```bash
az acr credential show --name innoadregistry --query passwords[0].value
```

### 2. Azure SQL Database

**Necesitas:**
- Servidor: `innoadserver.database.windows.net`
- Base de datos: `innoaddb`
- Usuario: `[tu-usuario]`
- Contraseña: `[tu-contraseña]`

**Script de bases de datos:**
```sql
-- Ejecutar: database-schema-fase4.sql
-- Crea: ciudades, lugares, pisos, publicaciones
-- Con datos iniciales
```

### 3. Azure App Service

**Necesitas:**
- Nombre: `innoadadmin`
- Resource Group: `[tu-resource-group]`
- Región: `[tu-región]`

**Variables de entorno:**
```env
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:sqlserver://...
SPRING_DATASOURCE_USERNAME=...
SPRING_DATASOURCE_PASSWORD=...
```

### 4. Netlify

**Necesitas:**
- Token: Obtén en https://app.netlify.com/user/applications/personal
- Directorio de deploy: `dist/innoadFrontend/browser`

---

## 🐛 SOLUCIÓN DE PROBLEMAS

### Error: "Docker image not found"
```bash
# Solución:
docker images  # Ver qué imágenes existen
01-build-docker-local.bat  # Reconstruir
```

### Error: "Login failed to Azure"
```bash
# Solución:
az login  # Login manual
az acr login --name innoadregistry  # Login a registry
```

### Error: "Azure App Service not found"
```bash
# Solución:
az webapp list --resource-group tu-resource-group
# Verifica que el nombre del App Service sea correcto
```

### Error: "SQL connection timeout"
```sql
-- Solución:
-- En Azure Portal > SQL Database > Firewall settings
-- Habilitar "Allow Azure services and resources to access this server"
```

### Error: CORS en frontend
```java
// Backend debe tener:
@CrossOrigin(origins = {
  "https://[tu-sitio-netlify].netlify.app",
  "http://localhost:4200"
})
```

---

## 📊 ESTRUCTURA DE ARCHIVOS GENERADOS

```
innoadBackend/
├── 00-verificar-todo.bat
├── 01-build-docker-local.bat
├── 02-push-to-azure.bat
├── 03-execute-sql.bat
├── 04-deploy-to-azure-app-service.bat
├── 05-deploy-frontend-netlify.bat
├── DEPLOYMENT_AUTOMATICO_COMPLETO.bat
├── GUIA_EJECUCION_RAPIDA.md
├── DEPLOYMENT_SCRIPTS_DOCUMENTACION.md  ← Este archivo
├── database-schema-fase4.sql
├── Dockerfile
├── pom.xml
├── docker-compose.yml
└── src/
    ├── main/
    │   ├── java/com/innoad/...
    │   └── resources/
    └── test/

innoadFrontend/
├── Dockerfile
├── package.json
├── docker-compose.yml
├── src/
│   ├── app/
│   │   ├── modulos/
│   │   │   ├── admin/
│   │   │   ├── asistente-ia/
│   │   │   ├── autenticacion/
│   │   │   ├── campanas/
│   │   │   ├── chat/
│   │   │   ├── contenidos/
│   │   │   ├── dashboard/
│   │   │   ├── publicacion/  ← NUEVO MÓDULO
│   │   │   └── ...
│   │   └── core/
│   └── assets/
└── dist/innoadFrontend/browser/  ← Se genera tras npm build
```

---

## 🎯 FLUJO COMPLETO

```
INICIO
  ↓
00-verificar-todo.bat (Verificar herramientas)
  ↓
01-build-docker-local.bat (Compilar + Docker)
  ├─ Maven clean install
  ├─ npm build
  ├─ Docker build backend
  └─ Docker build frontend
  ↓
02-push-to-azure.bat (Enviar imágenes)
  ├─ docker login
  ├─ docker tag
  └─ docker push
  ↓
03-execute-sql.bat (BD)
  └─ Ejecutar database-schema-fase4.sql
  ↓
04-deploy-to-azure-app-service.bat (Backend)
  ├─ az login
  ├─ az webapp config container set
  └─ az webapp restart
  ↓
05-deploy-frontend-netlify.bat (Frontend)
  ├─ npm build (si no lo hizo)
  └─ netlify deploy --prod
  ↓
✅ INNOAD COMPLETO
```

---

## 🔐 SEGURIDAD

⚠️ **IMPORTANTE:**

- **NO guardes credenciales** en los scripts
- Los scripts solicitan credenciales interactivamente
- Usa variables de entorno o Azure Key Vault para producción
- Revisa los scripts antes de ejecutar si modificas algo

---

## 📞 NEXT STEPS

Después de completar el deployment:

1. **Verificar que funcione:**
   - Backend API: `https://[app].azurewebsites.net/api/ubicaciones/ciudades`
   - Frontend: `https://[sitio].netlify.app`

2. **Testing:**
   - Crear usuario en frontend
   - Crear publicación
   - Verificar que se guarde en BD
   - Verificar que se vea en feed

3. **Monitoreo:**
   - Revisar logs en Azure Portal
   - Revisar deploy log en Netlify
   - Configurar alertas en Azure

4. **Documentación:**
   - Actualizar README.md con URLs finales
   - Documentar variables de entorno
   - Crear guía para otros desarrolladores

---

## 🏁 RESUMEN

✅ **COMPILACIÓN:** 25-30 minutos (automático)  
✅ **DOCKER:** 20-30 minutos (automático)  
⏳ **AZURE:** 40-50 minutos (manual en Portal)  
✅ **TOTAL:** 90 minutos para deployment completo  

**Estado:** 🟢 LISTO PARA EJECUTAR

Ejecuta: `DEPLOYMENT_AUTOMATICO_COMPLETO.bat`
