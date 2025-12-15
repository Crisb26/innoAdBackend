# ⚡ GUÍA RÁPIDA: DE CÓDIGO A PRODUCCIÓN (2 HORAS)

## 🎯 OBJETIVO
Desplegar el proyecto completo en **Azure Backend + Netlify Frontend** sin Postman.

---

## ⏱️ PASO 1: Ejecutar SQL Script (10 minutos)

### Opción A: Azure Portal (MÁS FÁCIL)
```
1. Abre https://portal.azure.com
2. Busca "SQL Databases"
3. Abre "innoad-db" (o tu BD)
4. Haz click en "Query Editor"
5. Copia el contenido de: database-schema-fase4.sql
6. Pega en Query Editor
7. Click "Run"
8. Espera a que termine (1-2 minutos)
9. Verifica el output: "Script completed successfully"
```

### Opción B: SQL Server Management Studio
```bash
# Desde CMD:
sqlcmd -S <servidor>.database.windows.net \
  -U <usuario> -P <contraseña> \
  -d innoad \
  -i database-schema-fase4.sql
```

### Opción C: Batch Script (Windows)
```bash
cd backend
connect-azure.bat
# Pega el SQL manualmente
```

✅ **Resultado esperado:** 5 ciudades, 11 centros comerciales, 49 pisos, 4 publicaciones

---

## ⏱️ PASO 2: Verificar Backend Compila (5 minutos)

```bash
cd "c:\Users\bueno\Desktop\PROYECTO FINAL INNOAD\BACKEND\innoadBackend"
mvn clean install -DskipTests
```

✅ **Esperado:** `BUILD SUCCESS` al final

---

## ⏱️ PASO 3: Build Docker Backend (5 minutos)

```bash
cd backend
docker build -t innoad-backend:v2.0 .
```

✅ **Esperado:** `Successfully built [ID]`

### Verificar localmente (opcional)
```bash
docker run -p 8080:8080 innoad-backend:v2.0
# Abre http://localhost:8080/api/ubicaciones/ciudades
# Debes ver: [{"id":1,"nombre":"Bogotá",...}]
```

---

## ⏱️ PASO 4: Build Frontend (10 minutos)

```bash
cd "c:\Users\bueno\Desktop\PROYECTO FINAL INNOAD\FRONTEND\innoadFrontend"
npm run construir
```

✅ **Esperado:** Termina sin errores, crea carpeta `dist/`

### Verificar tamaño
```bash
# dist/ debe ser < 5 MB
dir dist\innoad-frontend\browser
```

---

## ⏱️ PASO 5: Build Docker Frontend (5 minutos)

```bash
cd frontend
npm run docker:build:prod
```

✅ **Esperado:** `Successfully built [ID]`

---

## ⏱️ PASO 6: Push Docker a Azure Container Registry (10 minutos)

### Primero: Login a Azure
```bash
az login
az account set --subscription "<tu-subscription-id>"
```

### Tagear imagen
```bash
docker tag innoad-backend:v2.0 \
  <tu-registry>.azurecr.io/innoad-backend:v2.0
```

### Push a Container Registry
```bash
az acr login --name <tu-registry>
docker push <tu-registry>.azurecr.io/innoad-backend:v2.0
```

✅ **Esperado:** `Pushed successfully`

---

## ⏱️ PASO 7: Deploy Backend a Azure (15 minutos)

### Si ya tienes App Service
```bash
az webapp deployment container config \
  --name innoad-backend \
  --resource-group <tu-rg> \
  --docker-custom-image-name \
    <tu-registry>.azurecr.io/innoad-backend:v2.0 \
  --docker-registry-server-url \
    https://<tu-registry>.azurecr.io \
  --docker-registry-server-user <username> \
  --docker-registry-server-password <password>

# Redeploy
az webapp deployment slot swap \
  --resource-group <tu-rg> \
  --name innoad-backend \
  --slot staging
```

### Si NO tienes App Service
```bash
# Crear App Service Plan (si no existe)
az appservice plan create \
  --name innoad-plan \
  --resource-group innoad-rg \
  --sku B2 --is-linux

# Crear Web App
az webapp create \
  --resource-group innoad-rg \
  --plan innoad-plan \
  --name innoad-backend \
  --deployment-container-image-name \
    <tu-registry>.azurecr.io/innoad-backend:v2.0
```

### Verificar deployment
```bash
curl https://innoad-backend.azurewebsites.net/api/ubicaciones/ciudades
# Debe retornar: [{"id":1,"nombre":"Bogotá",...}]
```

---

## ⏱️ PASO 8: Deploy Frontend a Netlify (10 minutos)

### Opción A: Netlify CLI (MÁS RÁPIDO)
```bash
cd frontend
npm install -g netlify-cli
netlify login  # Te abre navegador
netlify deploy --prod --dir=dist/innoad-frontend/browser
```

### Opción B: GitHub Integration (AUTOMÁTICO)
```bash
# 1. Git push a main
git add .
git commit -m "Fase 4: Deploy a producción"
git push origin main

# 2. Netlify automáticamente:
#    - Detecta cambios
#    - Build npm run construir
#    - Deploy a netlify.app
#    - Disponible en ~2 minutos
```

✅ **Resultado:** Tu URL será https://innoad-xxxx.netlify.app

---

## ⏱️ PASO 9: Configurar Variables de Entorno (5 minutos)

### En Netlify
```
1. Netlify Dashboard
2. Site settings → Environment
3. Agregar:
   - API_URL=https://innoad-backend.azurewebsites.net
   - ENVIRONMENT=production
4. Redeploy: Netlify build → publish
```

### En Azure App Service
```
1. Azure Portal → innoad-backend → Configuration
2. Application settings:
   - DATABASE_URL=jdbc:sqlserver://innoad-sql.database.windows.net:1433;database=innoad;...
   - DATABASE_USER=<tu-user>
   - DATABASE_PASSWORD=<tu-password>
   - CORS_ORIGINS=https://innoad-xxxx.netlify.app
3. Save → Restart App Service
```

---

## ⏱️ PASO 10: Testing en Producción (10 minutos)

### Test 1: Backend activo
```bash
curl https://innoad-backend.azurewebsites.net/api/ubicaciones/ciudades
# Esperado: Lista de ciudades con HTTP 200
```

### Test 2: Frontend carga
```
Abre en navegador:
https://innoad-xxxx.netlify.app

Esperado: Ve la página de login
```

### Test 3: Flujo completo
```
1. Login como USUARIO
2. Navega a /usuario
3. Click en "Crear publicación"
4. Verifica que carga ciudades (GET /api/ubicaciones/ciudades)
5. Selecciona ciudad → lugar → piso
6. Sube un archivo
7. Completa formulario
8. Click "Enviar para aprobación"
9. Verifica que se crea publicación en BD
10. Login como TECNICO
11. Ve publicaciones pendientes
12. Aprueba/rechaza
13. Ve que se actualiza estado
```

✅ **Resultado:** Todo funciona correctamente

---

## 📋 CHECKLIST FINAL

```
PREPARACIÓN:
  ☐ SQL script ejecutado
  ☐ Datos iniciales en BD (5 ciudades, etc)

BACKEND:
  ☐ mvn clean install SUCCESS
  ☐ Docker build OK
  ☐ Push a Azure Container Registry OK
  ☐ App Service configurado
  ☐ Variables de entorno seteadas
  ☐ GET /api/ubicaciones/ciudades retorna 200

FRONTEND:
  ☐ npm run construir sin errores
  ☐ Docker build OK
  ☐ Netlify deploy completado
  ☐ Variables de entorno configuradas
  ☐ Página carga en navegador

TESTING:
  ☐ Backend accesible desde internet
  ☐ Frontend accesible desde internet
  ☐ Ciudades cargan correctamente
  ☐ Upload de archivos funciona
  ☐ Publicación se guarda en BD
  ☐ Técnico puede ver pendientes
  ☐ Estados se actualizan correctamente
```

---

## 🆘 SI ALGO FALLA

### Backend no carga ciudades
```bash
# Verificar BD
curl "https://innoad-backend.azurewebsites.net/api/ubicaciones/ciudades"

# Ver logs
az webapp log tail --resource-group <rg> --name innoad-backend

# Verificar BD está arriba
# Azure Portal → SQL Databases → innoad-db → Check status
```

### Frontend no carga
```bash
# Ver build logs
netlify deploy --log-dir=build-logs

# Verificar API_URL es correcto
# Browser DevTools → Console → busca "API_URL"

# Verificar CORS
# Browser DevTools → Network → ver headers "Access-Control-Allow-Origin"
```

### CORS error en frontend
```
Error en console: "CORS policy: No 'Access-Control-Allow-Origin' header"

Solución:
1. Verifica que CORS_ORIGINS en Azure App Service incluye Netlify domain
2. Redeploy de backend
3. Limpia cache del navegador (Ctrl+Shift+Del)
```

---

## 📊 TIEMPO ESTIMADO

| Paso | Tiempo |
|------|--------|
| 1. Ejecutar SQL | 10 min |
| 2. Verificar backend | 5 min |
| 3. Build Docker backend | 5 min |
| 4. Build frontend | 10 min |
| 5. Build Docker frontend | 5 min |
| 6. Push a Container Registry | 10 min |
| 7. Deploy backend | 15 min |
| 8. Deploy frontend | 10 min |
| 9. Variables de entorno | 5 min |
| 10. Testing | 10 min |
| **TOTAL** | **85 minutos** |

---

## ✨ RESULTADO FINAL

```
┌─────────────────────────────────┐
│  Tu app está EN PRODUCCIÓN:     │
├─────────────────────────────────┤
│ Backend: Azure App Service      │
│ Frontend: Netlify CDN           │
│ BD: Azure SQL Database          │
│ HTTPS: Automático               │
│ SSL: Let's Encrypt (automático) │
│ Backups: Automáticos            │
│ CDN: Automático (Netlify)       │
└─────────────────────────────────┘
```

---

**¿LISTO? ¡Empecemos!**

Primer paso: Copia el SQL script y ejecútalo en Azure Portal.
