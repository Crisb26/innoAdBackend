# 📋 CHECKLIST DE DESPLIEGUE EN AZURE

## ✅ Estado Actual

```
┌─────────────────────────────────────────────────────────────────┐
│                    COMPONENTES DESPLEGADOS                       │
├─────────────────────────────────────────────────────────────────┤
│ ✓ innoad-backend (Container Apps)         → EN EJECUCIÓN       │
│ ✓ innoad-postgres (PostgreSQL)            → READY              │
│ ✓ innoadFrontend (Netlify)                → CONECTADO          │
│ ✓ Dockerfile (Multi-stage)                → OPTIMIZADO         │
│ ✓ application-prod.yml                    → CONFIGURADO        │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🔧 VERIFICACIONES A REALIZAR

### 1️⃣ **Verificar Variables de Entorno en Azure Container Apps**

**En Azure Portal:**
1. Ir a: **Container Apps** → **innoad-backend**
2. Menú izquierdo: **Environment variables**
3. Verificar que estén configuradas:

```
SPRING_PROFILES_ACTIVE = prod
AZURE_DB_HOST = innoad-postgres.postgres.database.azure.com
AZURE_DB_PORT = 5432
AZURE_DB_NAME = innoad_db
AZURE_DB_USER = admininnoad
AZURE_DB_PASSWORD = [tu contraseña]
MAIL_HOST = smtp.gmail.com
MAIL_PORT = 587
MAIL_USERNAME = [tu email]
MAIL_PASSWORD = [tu app password]
JWT_SECRET = [tu clave secreta]
PORT = 8080
```

**Si faltan variables:**
1. Click en **+ Add**
2. Completar **Name** y **Value**
3. Click **Add**
4. Click **Save** (arriba a la derecha)

---

### 2️⃣ **Verificar Conexión a Base de Datos**

Ejecuta desde cmd:
```bash
verificar-bd.bat
```

**Qué esperar:**
- Solicita contraseña de la BD
- Si conecta: `✓ CONEXION EXITOSA A LA BASE DE DATOS`
- Si no conecta: Error de contraseña o firewall

---

### 3️⃣ **Revisar Logs del Container App**

**En Azure Portal:**
1. Container Apps → **innoad-backend**
2. Menú izquierdo: **Logs**
3. Buscar líneas como:
   - `Started InnoAdApplication in X.XXX seconds`
   - `Connection to db successful`
   - Errores (líneas rojas)

**Errores comunes:**
- `java.sql.SQLException`: Problema de conexión a BD
- `javax.net.ssl.SSLException`: Falta `sslmode=require`
- `ClassNotFoundException`: Falta dependencia en pom.xml

---

### 4️⃣ **Probar la API**

**URL base:**
```
https://innoad-backend.wonderfuldune-d0f51e2f.eastus2.azurecontainerapps.io/api
```

**Pruebas rápidas:**

1. **Health Check:**
```bash
curl https://innoad-backend.wonderfuldune-d0f51e2f.eastus2.azurecontainerapps.io/actuator/health
```

Respuesta esperada:
```json
{"status":"UP"}
```

2. **Documentación Swagger:**
```
https://innoad-backend.wonderfuldune-d0f51e2f.eastus2.azurecontainerapps.io/swagger-ui.html
```

3. **Info de la aplicación:**
```bash
curl https://innoad-backend.wonderfuldune-d0f51e2f.eastus2.azurecontainerapps.io/actuator/info
```

---

### 5️⃣ **Verificar Frontend está Conectado**

El frontend en Netlify debe estar usando las URLs correctas.

**Verificar en environment.prod.ts:**
```typescript
api: {
  gateway: 'https://innoad-backend.wonderfuldune-d0f51e2f.eastus2.azurecontainerapps.io/api',
  services: {
    auth: 'https://innoad-backend.wonderfuldune-d0f51e2f.eastus2.azurecontainerapps.io/api/auth',
    // ... otros servicios
  }
}
```

**En el navegador:**
1. Abre la aplicación del frontend (en Netlify)
2. Abre Developer Tools (F12)
3. Pestaña **Network**
4. Realiza login
5. Debe ver llamadas a:
   - `/api/auth/login` → Status 200/401
   - No debe haber CORS errors

---

## 🚨 TABLA DE TROUBLESHOOTING

| Problema | Causa | Solución |
|----------|-------|----------|
| **503 Service Unavailable** | BD no conecta | Verificar variables de entorno AZURE_DB_* |
| **401 Unauthorized** | JWT invalido | Verificar JWT_SECRET en Container App |
| **CORS Error** | Frontend a diferente origen | Verificar CORS en Spring Security |
| **Connection timeout** | Firewall bloquea | Agregar IP en Azure PostgreSQL firewall |
| **Swagger no carga** | Ruta incorrecta | Verificar `/swagger-ui.html` |

---

## ✅ CHECKLIST FINAL

Marca lo que ya verificaste:

- [ ] Variables de entorno están en Azure Container Apps
- [ ] Conexión a BD funciona (verificar-bd.bat exitoso)
- [ ] Logs no muestran errores
- [ ] Health check devuelve `UP`
- [ ] Swagger carga correctamente
- [ ] Frontend se conecta a la API
- [ ] Login funciona end-to-end

---

## 📞 Si algo falla

Ejecuta estos comandos y comparte el output:

**Script de diagnóstico completo:**
```bash
verificar-despliegue.bat
```

**Ver logs en tiempo real:**
```bash
az containerapp logs show --name innoad-backend --resource-group rg-adso-app --follow
```

**Reiniciar el Container App:**
```bash
az containerapp revision restart --name innoad-backend --resource-group rg-adso-app --revision [nombre-revision]
```

---

## 🎉 ¡Listo!

Si todo pasa el checklist, tu aplicación está **completamente desplegada y funcionando** en Azure. 🚀
