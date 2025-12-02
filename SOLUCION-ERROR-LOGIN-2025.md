# 🔧 SOLUCIÓN INMEDIATA: Error de Login en Azure

## 🚨 Problema Identificado

El backend está crasheando porque **las variables de entorno no están configuradas en Azure Container Apps**.

### Errores vistos:
- Frontend: `Unchecked runtime.lastError: The message port closed before a response was received`
- Backend: Container en estado **Erróneo** (Crashed)
- BD: PostgreSQL está UP pero el backend no puede conectar

---

## ✅ SOLUCIÓN PASO A PASO

### Paso 1: Ir a Azure Portal

1. Abre: https://portal.azure.com
2. Busca `innoad-backend` en la barra de búsqueda
3. Haz clic en **Container Apps → innoad-backend**

---

### Paso 2: Acceder a las Variables de Entorno

1. En el menú izquierdo, busca **Configuración** → **Containers**
2. Haz clic en el botón **Edit and Deploy** (si está disponible)
3. O ve a **Environment variables**

---

### Paso 3: Agregar/Verificar las Variables

Necesitas agregar estas variables (si no están ya):

#### 🗄️ Base de Datos PostgreSQL:
```
SPRING_PROFILES_ACTIVE = prod
AZURE_DB_HOST = innoad-postgres.postgres.database.azure.com
AZURE_DB_PORT = 5432
AZURE_DB_NAME = innoad_db
AZURE_DB_USER = admininnoad
AZURE_DB_PASSWORD = [Tu contraseña de BD]
```

#### 📧 Correo (Gmail):
```
MAIL_HOST = smtp.gmail.com
MAIL_PORT = 587
MAIL_USERNAME = [tu-email@gmail.com]
MAIL_PASSWORD = [tu-app-password-de-google]
```

#### 🔑 JWT:
```
JWT_SECRET = MiClaveSecretaSuperSeguraParaInnoAd2024!
```

#### 🌐 Puerto:
```
PORT = 8080
```

---

### Paso 4: Agregar cada Variable

**Para cada variable:**

1. Haz clic en **+ Add** (arriba a la derecha)
2. En el campo **Name**: escribe el nombre exacto (ej: `SPRING_PROFILES_ACTIVE`)
3. En el campo **Value**: escribe el valor
4. Haz clic en **Add**
5. Repite para todas las variables

---

### Paso 5: Guardar y Reiniciar

1. Haz clic en **Save** (arriba a la derecha)
2. Azure **reiniciará automáticamente** el Container App
3. **Espera 2-3 minutos** a que vuelva a estar "Running"

---

## 🔍 Verificar que Funciona

Después de guardar, espera 2-3 minutos y prueba:

### Opción 1: Desde el Frontend
1. Abre: https://friendly-lollipop-ce7d8c.netlify.app
2. Intenta hacer login con: `admin / Admin123!`
3. Deberías ver la sesión iniciada

### Opción 2: Verificar desde curl (cmd)
```bash
curl https://innoad-backend.wonderfuldune-d0f51e2f.eastus2.azurecontainerapps.io/actuator/health
```

Deberías ver: `{"status":"UP"}`

---

## 🆘 Si Sigue Fallando

### Verificar logs en Azure:
1. Container Apps → innoad-backend
2. Menú izquierdo: **Logs** (o **Console**/**Revision details**)
3. Busca mensajes de error

### Posibles problemas:
- **Connection timeout**: Falta `AZURE_DB_PASSWORD` correcta
- **SSL Error**: Variables no tienen el formato correcto
- **Container aún crasheando**: Espera más tiempo o recarga la página

---

## 📝 Notas Importantes

✅ **HECHO YA:** 
- Agregué el dominio de Netlify al CORS del backend
- Agregué `autocomplete="current-password"` al input de contraseña

⚠️ **PRÓXIMO PASO:**
- Configura las variables de entorno en Azure Portal
- El backend debería conectar a la BD y empezar a funcionar

💡 **Por si necesitas la contraseña de BD:**
- Ve a: Azure Portal → PostgreSQL → innoad-postgres → Passwords
- O revisa los Secrets del Container App

---

## ✨ Cambios Realizados en Código

### Backend (Java):
- ✅ Agregado `https://friendly-lollipop-ce7d8c.netlify.app` al CORS
- El backend ahora acepta peticiones del frontend en Netlify

### Frontend (Angular):
- ✅ Agregado `autocomplete="current-password"` al input de contraseña
- Elimina la advertencia del DOM en DevTools

---

**Cuando termines de configurar las variables, el login debería funcionar correctamente. ¡Avísame si necesitas ayuda!**
