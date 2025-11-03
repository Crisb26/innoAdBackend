# GUÍA COMPLETA DE DESPLIEGUE - INNOAD

## 📋 Tabla de Contenidos
1. [Prerrequisitos](#prerrequisitos)
2. [Preparación del Entorno](#preparación-del-entorno)
3. [Configuración de Base de Datos](#configuración-de-base-de-datos)
4. [Despliegue del Backend](#despliegue-del-backend)
5. [Despliegue del Frontend](#despliegue-del-frontend)
6. [Configuración de Git](#configuración-de-git)
7. [Verificación](#verificación)
8. [Solución de Problemas](#solución-de-problemas)

---

## 1. Prerrequisitos

### Software Necesario:
- ✅ Java JDK 17 o superior
- ✅ Maven 3.8 o superior
- ✅ Node.js 18+ y npm 9+
- ✅ PostgreSQL 14+
- ✅ Git
- ✅ Redis (opcional, para caché)
- ✅ Un IDE (IntelliJ IDEA, VS Code, etc.)

### Verificar Instalaciones:
```bash
# Verificar Java
java -version
# Debe mostrar: java version "17.x.x" o superior

# Verificar Maven
mvn -version
# Debe mostrar: Apache Maven 3.8.x o superior

# Verificar Node.js
node -version
# Debe mostrar: v18.x.x o superior

# Verificar npm
npm -version
# Debe mostrar: 9.x.x o superior

# Verificar PostgreSQL
psql --version
# Debe mostrar: psql (PostgreSQL) 14.x o superior

# Verificar Git
git --version
# Debe mostrar: git version 2.x.x
```

---

## 2. Preparación del Entorno

### Paso 1: Crear directorios de trabajo
```bash
# Crear directorio principal
mkdir innoad-proyecto
cd innoad-proyecto

# Descomprimir los archivos del backend
unzip innoad-backend.zip

# Descomprimir los archivos del frontend
unzip innoad-frontend.zip
```

### Paso 2: Configurar Variables de Entorno

#### En Windows:
```cmd
# Crear archivo .env en la raíz del proyecto backend
cd innoad-backend
notepad .env
```

#### En Linux/Mac:
```bash
cd innoad-backend
nano .env
```

#### Contenido del archivo .env:
```properties
# Base de Datos
DB_URL=jdbc:postgresql://localhost:5432/innoad_db
DB_USER=innoad_user
DB_PASSWORD=TU_PASSWORD_SEGURO_AQUI

# JWT - IMPORTANTE: Cambiar en producción
JWT_SECRET=MiClaveSecretaSuperSeguraParaInnoAdQueDebeSerMuyLarga2024!

# Configuración de Email (Gmail)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=tu-email@gmail.com
MAIL_PASSWORD=tu-password-de-aplicacion-gmail

# Redis (si lo usas)
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# Inteligencia Artificial (OpenAI)
AI_API_URL=https://api.openai.com/v1
AI_API_KEY=sk-tu-api-key-de-openai-aqui
AI_MODEL=gpt-4

# Modo Mantenimiento
MAINTENANCE_CODE=93022611184
MAINTENANCE_EMAIL=admin@innoad.com

# Contraseña Admin
ADMIN_PASSWORD=Admin123!

# URL del Frontend
FRONTEND_URL=http://localhost:4200

# Puerto del servidor
SERVER_PORT=8080
```

---

## 3. Configuración de Base de Datos

### Paso 1: Iniciar PostgreSQL

#### En Windows:
```cmd
# Buscar "Services" en el menú inicio
# Buscar "PostgreSQL" y asegurarse que esté corriendo
# O usar pgAdmin 4
```

#### En Linux:
```bash
sudo systemctl start postgresql
sudo systemctl enable postgresql  # Para que inicie automáticamente
```

#### En Mac:
```bash
brew services start postgresql
```

### Paso 2: Crear la Base de Datos

```bash
# Conectarse a PostgreSQL como superusuario
psql -U postgres

# En el prompt de PostgreSQL, ejecutar:
```

```sql
-- Crear base de datos
CREATE DATABASE innoad_db;

-- Crear usuario
CREATE USER innoad_user WITH PASSWORD 'TU_PASSWORD_SEGURO_AQUI';

-- Dar permisos
GRANT ALL PRIVILEGES ON DATABASE innoad_db TO innoad_user;

-- Dar permisos en el esquema public
\c innoad_db
GRANT ALL ON SCHEMA public TO innoad_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO innoad_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO innoad_user;

-- Salir
\q
```

### Paso 3: Verificar Conexión
```bash
psql -U innoad_user -d innoad_db -h localhost
# Si te conecta, ¡perfecto!
\q
```

---

## 4. Despliegue del Backend

### Paso 1: Navegar al proyecto backend
```bash
cd innoad-backend
```

### Paso 2: Compilar el proyecto
```bash
# Limpiar y compilar
mvn clean install

# Si hay errores de tests, puedes saltarlos temporalmente:
mvn clean install -DskipTests
```

### Paso 3: Ejecutar el Backend

#### Opción A: Con Maven (Desarrollo)
```bash
mvn spring-boot:run
```

#### Opción B: Con el JAR (Producción)
```bash
java -jar target/innoad-backend-2.0.0.jar
```

### Paso 4: Verificar que el Backend está corriendo
```bash
# En otra terminal/ventana de comandos:
curl http://localhost:8080/actuator/health

# Debería responder:
# {"status":"UP"}
```

### Paso 5: Acceder a la Documentación API
Abrir en el navegador:
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/api-docs

---

## 5. Despliegue del Frontend

### Paso 1: Navegar al proyecto frontend
```bash
# En una nueva terminal
cd ../innoad-frontend
```

### Paso 2: Instalar dependencias
```bash
npm install
```

Si hay errores, intentar:
```bash
npm install --legacy-peer-deps
```

### Paso 3: Configurar el entorno

Editar `src/environments/environment.ts`:
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  appName: 'InnoAd',
  version: '2.0.0'
};
```

Editar `src/environments/environment.prod.ts`:
```typescript
export const environment = {
  production: true,
  apiUrl: 'https://tu-dominio.com/api',  // Cambiar por tu dominio
  appName: 'InnoAd',
  version: '2.0.0'
};
```

### Paso 4: Ejecutar en Desarrollo
```bash
npm start
# o
ng serve
```

El frontend estará disponible en: http://localhost:4200

### Paso 5: Compilar para Producción
```bash
npm run build:prod
```

Los archivos compilados estarán en `dist/innoad-frontend/`

---

## 6. Configuración de Git

### Paso 1: Inicializar repositorio local

#### Para el Backend:
```bash
cd innoad-backend
git init
git add .
git commit -m "feat: Configuración inicial del backend InnoAd v2.0"
```

#### Para el Frontend:
```bash
cd ../innoad-frontend
git init
git add .
git commit -m "feat: Configuración inicial del frontend InnoAd v2.0"
```

### Paso 2: Crear repositorios en GitHub

1. Ve a https://github.com
2. Click en "New repository"
3. Crea dos repositorios:
   - `innoad-backend`
   - `innoad-frontend`
4. NO inicialices con README (ya lo tenemos)

### Paso 3: Conectar con GitHub y crear rama develop

#### Para el Backend:
```bash
cd innoad-backend

# Añadir remoto
git remote add origin https://github.com/tu-usuario/innoad-backend.git

# Crear rama develop
git branch develop
git checkout develop

# Subir ambas ramas
git push -u origin main
git push -u origin develop

# Trabajar siempre en develop
git checkout develop
```

#### Para el Frontend:
```bash
cd ../innoad-frontend

# Añadir remoto
git remote add origin https://github.com/tu-usuario/innoad-frontend.git

# Crear rama develop
git branch develop
git checkout develop

# Subir ambas ramas
git push -u origin main
git push -u origin develop

# Trabajar siempre en develop
git checkout develop
```

### Paso 4: Workflow de Git (MUY IMPORTANTE)

```bash
# SIEMPRE trabajar en develop
git checkout develop

# Hacer cambios...
git add .
git commit -m "tipo: descripción del cambio"

# Subir a develop
git push origin develop

# Solo cuando TODO esté probado y funcionando:
git checkout main
git merge develop
git push origin main
git checkout develop  # Volver a develop
```

#### Tipos de commits recomendados:
- `feat:` - Nueva funcionalidad
- `fix:` - Corrección de errores
- `docs:` - Cambios en documentación
- `style:` - Cambios de formato (no afectan el código)
- `refactor:` - Refactorización de código
- `test:` - Añadir o modificar tests
- `chore:` - Tareas de mantenimiento

---

## 7. Verificación

### Lista de Verificación:

#### Backend:
- [ ] PostgreSQL corriendo
- [ ] Base de datos creada
- [ ] Backend compilado sin errores
- [ ] Backend corriendo en http://localhost:8080
- [ ] Swagger UI accesible
- [ ] Health endpoint responde correctamente

---

## 9. Despliegue con Docker (opcional, recomendado)

Si quieres empaquetar el backend en una imagen Docker reproducible, puedes usar el `Dockerfile` proporcionado en la raíz del proyecto. El `Dockerfile` realiza un build multi-stage (compila con Maven y crea una imagen runtime basada en Eclipse Temurin JRE 21).

Construir la imagen (desde la raíz del backend):

```powershell
# Desde Windows / PowerShell (en la carpeta innoad-backend)
docker build -t innoad-backend:2.0.0 .
```

Ejecutar el contenedor (mapear puerto y variables de entorno):

```powershell
docker run -d --name innoad-backend \
  -p 8000:8000 \
  -e "SERVER_PORT=8000" \
  -e "JWT_SECRET=TuSecretBase64_o_plano" \
  -e "FRONTEND_URL=http://localhost:4200" \
  innoad-backend:2.0.0
```

Notas importantes:
- La imagen compila el código dentro del stage `build`; para CI es recomendable usar caches de dependencias o construir el jar previamente y usar un stage más pequeño que copie sólo el JAR.
- Si tu clúster o entorno no permite `apt-get` durante la imagen runtime, puedes adaptar el Dockerfile para no instalar `curl` y omitir `HEALTHCHECK`.
- En producción, monta secretos (JWT_SECRET, DB credentials) mediante secretos de tu orquestador (Kubernetes Secrets, Azure Key Vault, etc.) en lugar de pasarlos en la línea de comandos.

---

## Apéndice: notas rápidas

- Puerto por defecto usado en la imagen/ejecución: 8000 (coincide con nuestra configuración local en el proyecto). Ajusta `SERVER_PORT` si deseas otro puerto.
- El `Dockerfile` usa Java 21 (JRE) en runtime; asegúrate de que tu entorno de producción valide compatibilidad con Java 21.


## 8. Variables de entorno críticas y buenas prácticas

Antes de desplegar en producción asegúrate de definir estas variables de entorno (no las guardes en el repositorio):

- JWT_SECRET: secreto para firmar tokens JWT. Debe ser una cadena Base64 segura (256 bits o más). Puedes generarlo en PowerShell:

```powershell
$k = New-Object Byte[] 64; (New-Object Random).NextBytes($k); [Convert]::ToBase64String($k)
```

- DB_URL: URL JDBC de PostgreSQL (por ejemplo: jdbc:postgresql://db-host:5432/innoad_db)
- DB_USER, DB_PASSWORD: credenciales de la base de datos
- MAIL_USERNAME, MAIL_PASSWORD: credenciales para envío de correos
- FRONTEND_URL: URL donde estará servido el frontend (usado para CORS)
- AI_API_KEY: clave para el servicio de IA (si se utiliza)

## 9. Comandos útiles

Compilar y crear JAR:

```cmd
cd /d "C:\Users\bueno\Desktop\PROYECTO FINAL INNOAD\innoadBackend"
mvn clean package -DskipTests
```

Ejecutar la aplicación:

```cmd
set JWT_SECRET=TU_JWT_SECRET_BASE64
java -jar target\innoad-backend-2.0.0.jar
```

Comprobar logs de arranque y endpoints:

```cmd
# Ver logs (Windows)
type logs\innoad-backend.log
```

## 10. Notas finales

- He añadido una comprobación de arranque que valida la presencia de variables críticas (JWT_SECRET, FRONTEND_URL, y para perfil `prod` la DB y credenciales de email). Si falta alguna, la aplicación fallará al iniciar con un mensaje claro.
- Actualiza `JWT_SECRET` a una clave Base64 fuerte para producción.
- Si la compilación o arranque muestra errores, comparte el log detallado (`mvn -X`) y lo reviso paso a paso.

#### Frontend:
- [ ] Dependencias instaladas sin errores
- [ ] Frontend corriendo en http://localhost:4200
- [ ] Puede hacer login/registro
- [ ] Se conecta correctamente al backend

#### Git:
- [ ] Repositorios creados en GitHub
- [ ] Rama main existe
- [ ] Rama develop existe y es la activa
- [ ] Primer commit realizado

### Prueba End-to-End:

1. **Abrir el frontend:** http://localhost:4200
2. **Registrar un nuevo usuario**
3. **Verificar que llegó el email** (si configuraste SMTP)
4. **Hacer login**
5. **Crear una publicidad**
6. **Probar el agente de IA**
7. **Si eres admin, probar el modo mantenimiento**

---

## 8. Solución de Problemas

### Problema: Backend no inicia

**Error:** "Could not connect to database"
```bash
# Verificar que PostgreSQL esté corriendo
# En Windows: Services > PostgreSQL
# En Linux: sudo systemctl status postgresql

# Verificar credenciales en application.yml
# Verificar que la base de datos exista
psql -U postgres -l
```

**Error:** "Port 8080 already in use"
```bash
# Cambiar el puerto en application.yml:
server:
  port: 8081

# O detener el proceso que usa el puerto 8080
# Windows: netstat -ano | findstr :8080
# Linux/Mac: lsof -i :8080
```

### Problema: Frontend no compila

**Error:** "Cannot find module"
```bash
# Eliminar node_modules y reinstalar
rm -rf node_modules
rm package-lock.json
npm install
```

**Error:** "Angular CLI not found"
```bash
# Instalar Angular CLI globalmente
npm install -g @angular/cli@19
```

### Problema: No llegan los emails

1. Verificar configuración SMTP
2. Si usas Gmail, activar "Contraseñas de aplicación":
   - Ve a Google Account > Security
   - Activar 2-Step Verification
   - Crear App Password
   - Usar esa password en MAIL_PASSWORD

### Problema: Error de CORS

```java
// Verificar que el frontend URL esté en la configuración
// En application.yml:
innoad:
  frontend:
    allowed-origins:
      - http://localhost:4200
      - tu-dominio.com
```

---

## 📞 Soporte

Si encuentras algún problema:
1. Revisa los logs del backend: `logs/innoad-backend.log`
2. Revisa la consola del navegador (F12)
3. Verifica que todas las variables de entorno estén configuradas
4. Asegúrate de estar en la rama `develop` para desarrollo

---

## 🎉 ¡Felicitaciones!

Si llegaste hasta aquí, ¡tu sistema InnoAd debería estar funcionando correctamente!

### Próximos Pasos:
1. Personalizar la configuración para tu caso de uso
2. Configurar tu API key de OpenAI para el agente de IA
3. Configurar el SMTP para envío de emails
4. Revisar y ajustar los permisos por rol
5. Personalizar el diseño del frontend

### Usuarios por Defecto:
El sistema permite registrar hasta 1000 usuarios. Los roles disponibles son:
- **ADMINISTRADOR** - Acceso completo
- **TECNICO** - Acceso técnico y mantenimiento
- **DESARROLLADOR** - Herramientas de desarrollo
- **USUARIO** - Funcionalidades estándar
- **VISITANTE** - Solo lectura

¡Buena suerte con tu proyecto! 🚀
