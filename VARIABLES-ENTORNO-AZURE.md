# 🔐 GUÍA: Configurar Variables de Entorno en Azure Portal

## Paso 1: Acceder a Azure Portal

1. Abre: https://portal.azure.com
2. Busca `innoad-backend` en la barra superior
3. Selecciona **Container Apps → innoad-backend**

---

## Paso 2: Ir a la sección de Variables de Entorno

En el menú izquierdo, bajo **Configuración**:
- Click en **Containers**
- Busca la sección **Environment variables**
- Click en **Edit and Deploy** o el botón de edición

---

## Paso 3: Variables Necesarias

Copia-pega estas variables. Reemplaza `[VALOR]` con los valores reales:

### 🗄️ Base de Datos (PostgreSQL)
```
SPRING_PROFILES_ACTIVE = prod
AZURE_DB_HOST = innoad-postgres.postgres.database.azure.com
AZURE_DB_PORT = 5432
AZURE_DB_NAME = innoad_db
AZURE_DB_USER = admininnoad
AZURE_DB_PASSWORD = [contraseña de Azure DB]
```

> 📌 Obtén la contraseña en:
> - Container Apps → innoad-backend → Secrets
> - O en: PostgreSQL → innoad-postgres → Passwords

### 📧 Correo (Gmail)
```
MAIL_HOST = smtp.gmail.com
MAIL_PORT = 587
MAIL_USERNAME = [tu-email@gmail.com]
MAIL_PASSWORD = [tu-app-password-generado-en-google]
```

> 📌 Para obtener la contraseña de app:
> 1. Abre: https://myaccount.google.com/apppasswords
> 2. Selecciona **Mail** y **Windows Computer**
> 3. Copia la contraseña generada

### 🔑 Seguridad (JWT)
```
JWT_SECRET = [tu-clave-secreta-super-segura-de-minimo-32-caracteres]
```

> 📌 Ejemplo: `MiClaveSecretaSuperSeguraParaInnoAd2024!`

### 🌐 Puerto
```
PORT = 8080
```

---

## Paso 4: Agregar Variables

Si no están todas configuradas:

1. Click en **+ Add** (arriba a la derecha)
2. En **Name**: escribe el nombre (ej: `SPRING_PROFILES_ACTIVE`)
3. En **Value**: escribe el valor (ej: `prod`)
4. Click **Add**
5. Repite para cada variable

---

## Paso 5: Guardar Cambios

1. Revisa que todas las variables estén correctas
2. Click en **Save** (arriba a la derecha)
3. Azure reiniciará el Container App automáticamente
4. Espera 2-3 minutos a que finalice el restart

---

## Verificación Rápida

Después de guardar, ejecuta en cmd:

```bash
# Ver que el Container App está running
az containerapp show --name innoad-backend --resource-group rg-adso-app --query "properties.provisioningState"

# Esperado: "Succeeded"
```

---

## 🚨 Checklist de Seguridad

- [ ] **NO** guardes contraseñas en código
- [ ] **NO** hagas push de secretos a GitHub
- [ ] **SÍ** usa variables de entorno en Azure
- [ ] **SÍ** cambia JWT_SECRET a algo único y seguro
- [ ] **SÍ** guarda las contraseñas en un lugar seguro (gestor de contraseñas)

---

## 💡 Si necesitas cambiar una variable después

1. Container Apps → innoad-backend → Containers
2. Click **Edit and Deploy**
3. Modifica la variable
4. Click **Save**
5. El Container App se reinicia automáticamente

---

## ✅ Verificación Final

Después que se reinicie (2-3 minutos):

```bash
# Test 1: Health Check
curl https://innoad-backend.wonderfuldune-d0f51e2f.eastus2.azurecontainerapps.io/actuator/health

# Esperado: {"status":"UP"}

# Test 2: Ver logs
az containerapp logs show --name innoad-backend --resource-group rg-adso-app --tail 50

# Esperado: Líneas con "Started InnoAdApplication"
```

---

## 🆘 Errores Comunes

### Error: `Connection refused`
- Variables AZURE_DB_* están mal
- Contraseña incorrecta
- BD no está en estado "Ready"

### Error: `SSL certificate verify failed`
- Falta `sslmode=require` (ya está en application-prod.yml, OK)
- BD requiere certificados específicos

### Error: `MAIL_HOST connection failed`
- Mail no está configurado (no es crítico)
- Email/password incorrectos

### Error: `JWT_SECRET not set`
- JWT_SECRET no está en variables
- Token inválido en login

---

## 📞 Soporte Rápido

Si algo no funciona:

1. Copia el nombre de la variable problemática
2. Ve a: Container Apps → innoad-backend → Logs
3. Busca líneas roja con "ERROR"
4. Comparte la línea de error

¡Listo! Con esto tu despliegue en Azure debe estar 100% funcional. 🚀
