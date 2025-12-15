# EJECUCIÓN RÁPIDA - INNOAD FASE 4 COMPLETA

## 🚀 RESUMEN EJECUTIVO

**5 pasos simples (90 minutos):**

```
01-build-docker-local.bat       → Compilar Docker localmente (20 min)
02-push-to-azure.bat            → Enviar a Azure Registry (15 min)  
03-execute-sql.bat              → Actualizar base de datos (10 min)
04-deploy-to-azure-app-service  → Deploy backend (30 min)
05-deploy-frontend-netlify.bat  → Deploy frontend (15 min)
```

---

## 📋 ANTES DE EMPEZAR - VERIFICA QUE TENGAS:

### 1. Docker instalado
```bash
docker --version
# Debe mostrar: Docker version 29.x o superior
```

### 2. Java instalado
```bash
java -version
# Debe mostrar: Java 21
```

### 3. Maven instalado
```bash
mvn --version
# Debe mostrar: Apache Maven
```

### 4. Node.js instalado
```bash
node --version
npm --version
# Deben mostrar: v18+ y npm 10+
```

### 5. Azure CLI instalado
```bash
az --version
# Debe mostrar versión de Azure CLI
```

### 6. Credenciales Azure disponibles
- Nombre de servidor SQL
- Nombre base de datos
- Usuario y contraseña SQL
- Nombre del Container Registry
- Nombre del App Service
- Resource Group

---

## 🔧 PASO 1: COMPILAR DOCKER LOCALMENTE (20 min)

**Ejecuta:**
```bash
01-build-docker-local.bat
```

**Qué hace:**
- Compila backend con Maven
- Compila frontend con npm
- Construye imagen Docker del backend
- Construye imagen Docker del frontend
- Crea: `innoad-backend:v2.0` y `innoad-frontend:v2.0`

**Salida esperada:**
```
[OK] Docker está presente
[OK] Backend compilado exitosamente
[OK] Imagen backend creada: innoad-backend:v2.0
[OK] Imagen frontend creada: innoad-frontend:v2.0
```

---

## 🔧 PASO 2: ENVIAR A AZURE CONTAINER REGISTRY (15 min)

**Ejecuta:**
```bash
02-push-to-azure.bat
```

**Qué hace:**
- Te pide login en Azure Container Registry
- Etiqueta imágenes con URL de registry
- Envía backend a Azure
- Envía frontend a Azure

**Salida esperada:**
```
[OK] Login exitoso en innoadregistry.azurecr.io
[OK] Backend etiquetado
[OK] Frontend etiquetado
[OK] Backend enviado a innoadregistry.azurecr.io
[OK] Frontend enviado a innoadregistry.azurecr.io
```

---

## 🔧 PASO 3: EJECUTAR SQL EN AZURE (10 min)

**Ejecuta:**
```bash
03-execute-sql.bat
```

**Qué hace:**
- Ejecuta `database-schema-fase4.sql` en tu Azure SQL Database
- Crea tablas: ciudades, lugares, pisos, publicaciones
- Carga datos iniciales

**Opciones:**
1. **Automática:** sqlcmd (requiere SQL Server tools)
2. **Manual (RECOMENDADO):**
   - Ir a https://portal.azure.com
   - Buscar tu SQL Database
   - Abrir "Query Editor"
   - Copiar/pegar contenido de `database-schema-fase4.sql`
   - Ejecutar

**Salida esperada:**
```
[OK] Script SQL ejecutado exitosamente
Tablas creadas:
  - ciudades (5 registros)
  - lugares (11 registros)
  - pisos (49 registros)
  - publicaciones (4 registros)
```

---

## 🔧 PASO 4: DESPLEGAR BACKEND EN AZURE APP SERVICE (30 min)

**Ejecuta:**
```bash
04-deploy-to-azure-app-service.bat
```

**Qué hace:**
- Realiza login en Azure
- Actualiza tu App Service con nueva imagen Docker
- Reinicia el servicio

**Configuración necesaria en App Service:**

Después de ejecutar, ve a Azure Portal y agrega variables de entorno:

```
SPRING_PROFILES_ACTIVE=prod

SPRING_DATASOURCE_URL=
jdbc:sqlserver://[servidor].database.windows.net:1433;database=[basedatos];encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;

SPRING_DATASOURCE_USERNAME=[usuario]
SPRING_DATASOURCE_PASSWORD=[contraseña]

SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.microsoft.sqlserver.jdbc.SQLServerDriver

SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.SQLServer2012Dialect
SPRING_JPA_HIBERNATE_DDL_AUTO=validate

OPENAI_API_KEY=[tu-api-key]
OPENAI_MODEL=gpt-4-turbo
```

**Salida esperada:**
```
[OK] Azure CLI disponible
[OK] Login de Azure realizado
[OK] App Service actualizado
[OK] App Service reiniciado
Backend desplegado en: https://innoadadmin.azurewebsites.net
```

---

## 🔧 PASO 5: DESPLEGAR FRONTEND EN NETLIFY (15 min)

**Ejecuta:**
```bash
05-deploy-frontend-netlify.bat
```

**Qué hace:**
- Compila frontend para producción
- Deploy automático en Netlify

**Requisitos:**
- Tener token de Netlify
- Obtén en: https://app.netlify.com/user/applications/personal

**Salida esperada:**
```
[OK] npm disponible
[OK] Frontend compilado
[OK] Netlify CLI disponible
[OK] Frontend desplegado en Netlify
```

---

## ✅ VERIFICACIÓN FINAL

Después de todo, verifica que funcione:

### Backend funcionando:
```bash
curl https://[innoadadmin].azurewebsites.net/api/ubicaciones/ciudades
# Debe devolver JSON con ciudades
```

### Frontend funcionando:
```
Abre: https://[tu-sitio-netlify].netlify.app
Debe cargar sin errores de CORS
```

---

## 🆘 PROBLEMAS COMUNES

### Error: "Docker image not found"
- Ejecuta `01-build-docker-local.bat` nuevamente
- Verifica con `docker images`

### Error: "Login failed to Azure"
- Ejecuta `az login` manualmente
- Verifica credenciales

### Error: "SQL timeout"
- Verifica que App Service tenga firewall abierto para Azure SQL
- En Azure Portal > SQL Database > Firewall settings > "Allow Azure services"

### Error: CORS en frontend
- Verifica que backend tenga `@CrossOrigin` configurado
- En backend: `@CrossOrigin(origins = {"https://[netlify-url]", "http://localhost:4200"})`

---

## 📞 SOPORTE

Si tienes problemas:

1. Revisa los logs del App Service en Azure Portal
2. Revisa los logs de Netlify en https://app.netlify.com
3. Ejecuta `docker logs [container-id]` localmente

---

## 🎉 COMPLETADO

Una vez todos los pasos ejecutados exitosamente:

✅ Backend en Azure  
✅ Frontend en Netlify  
✅ Base de datos en Azure SQL  
✅ Docker images en Container Registry  

**¡INNOAD FASE 4 COMPLETADA!**
