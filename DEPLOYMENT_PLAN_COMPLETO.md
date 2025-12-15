# PLAN DEPLOYMENT COMPLETO - INNOAD FASE 4

## ¿POSTMAN O DEPLOYMENT DIRECTO?

**RESPUESTA:** No necesitas Postman. Puedes ir directo a deployment con Azure + Netlify.

**POSTMAN es opcional**, solo para:
- Testing manual local antes de desplegar
- Debugging si algo no funciona
- Documentación de API

Pero el código ya está pronto para production.

---

## 📋 QUÉ FALTA PARA "PROYECTO FULL"

### ✅ YA TIENEN (95%)
```
[✓] Backend Spring Boot (Java 21)
[✓] Frontend Angular 18
[✓] Base de datos (SQL Server)
[✓] Modelos JPA creados
[✓] API REST endpoints
[✓] DTOs configurados
[✓] CORS habilitado
[✓] Dockerfiles listos
[✓] Docker-compose configurado
[✓] Azure setup scripts
[✓] Netlify configuration
[✓] Environment files
```

### ❌ FALTA POCO (5%)
```
[ ] Script SQL para crear tablas de Fase 4 (ciudades, lugares, pisos, publicaciones)
[ ] Datos iniciales en BD
[ ] Verificar rutas 404 en frontend (nginx config)
[ ] Build de producción en ambos
[ ] Deploy a Azure (backend)
[ ] Deploy a Netlify (frontend)
[ ] Testing de endpoints en producción
[ ] SSL/HTTPS configurado
```

---

## 🚀 PLAN DE DEPLOYMENT (PASO A PASO)

### PASO 1: Crear Scripts SQL (30 minutos)

**Archivo:** `database-schema-fase4.sql`

```sql
-- Tabla Ciudades
CREATE TABLE ciudades (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    nombre NVARCHAR(100) NOT NULL UNIQUE,
    codigo NVARCHAR(10) NOT NULL UNIQUE,
    cantidad_lugares INT NOT NULL DEFAULT 0,
    activa BIT NOT NULL DEFAULT 1,
    fecha_creacion DATETIME DEFAULT GETDATE()
);

-- Tabla Lugares
CREATE TABLE lugares (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    ciudad_id BIGINT NOT NULL FOREIGN KEY REFERENCES ciudades(id),
    nombre NVARCHAR(200) NOT NULL,
    pisos INT NOT NULL DEFAULT 1,
    costo_base DECIMAL(10, 2) NOT NULL DEFAULT 0,
    disponible BIT NOT NULL DEFAULT 1,
    fecha_creacion DATETIME DEFAULT GETDATE()
);

-- Tabla Pisos
CREATE TABLE pisos (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    lugar_id BIGINT NOT NULL FOREIGN KEY REFERENCES lugares(id),
    numero INT NOT NULL,
    disponible BIT NOT NULL DEFAULT 1,
    costo_por_dia DECIMAL(10, 2) NOT NULL DEFAULT 0,
    fecha_creacion DATETIME DEFAULT GETDATE()
);

-- Tabla Publicaciones
CREATE TABLE publicaciones (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    titulo NVARCHAR(200) NOT NULL,
    descripcion NVARCHAR(MAX),
    tipo_contenido NVARCHAR(20) NOT NULL, -- IMAGEN, VIDEO
    archivo_url NVARCHAR(500),
    duracion_dias INT NOT NULL DEFAULT 30,
    estado NVARCHAR(20) NOT NULL DEFAULT 'PENDIENTE', -- PENDIENTE, APROBADO, RECHAZADO, PUBLICADO
    usuario_id BIGINT NOT NULL,
    costo_total DECIMAL(10, 2) NOT NULL DEFAULT 0,
    ubicaciones_json NVARCHAR(MAX), -- JSON array
    fecha_creacion DATETIME DEFAULT GETDATE(),
    fecha_aprobacion DATETIME,
    fecha_rechazo DATETIME,
    motivo_rechazo NVARCHAR(MAX),
    fecha_publicacion DATETIME,
    fecha_finalizacion DATETIME
);

-- Índices para optimización
CREATE INDEX idx_publicaciones_usuario ON publicaciones(usuario_id);
CREATE INDEX idx_publicaciones_estado ON publicaciones(estado);
CREATE INDEX idx_lugares_ciudad ON lugares(ciudad_id);
CREATE INDEX idx_pisos_lugar ON pisos(lugar_id);

-- Datos iniciales: Ciudades
INSERT INTO ciudades (nombre, codigo, cantidad_lugares, activa) VALUES
('Bogotá', 'BOG', 15, 1),
('Medellín', 'MDE', 12, 1),
('Cali', 'CAL', 8, 1),
('Barranquilla', 'BAQ', 6, 1),
('Bucaramanga', 'BUC', 5, 1);

-- Datos iniciales: Lugares Bogotá
INSERT INTO lugares (ciudad_id, nombre, pisos, costo_base, disponible) VALUES
(1, 'Centro Comercial Andino', 5, 500.00, 1),
(1, 'Centro Comercial Titán', 6, 550.00, 1),
(1, 'Centro Comercial Unicentro', 4, 450.00, 1);

-- Datos iniciales: Pisos Centro Comercial Andino
INSERT INTO pisos (lugar_id, numero, disponible, costo_por_dia) VALUES
(1, 1, 1, 50.00),
(1, 2, 1, 50.00),
(1, 3, 1, 55.00),
(1, 4, 1, 55.00),
(1, 5, 1, 60.00);
```

### PASO 2: Ejecutar Script en Base de Datos (15 minutos)

**En Azure SQL Database:**
```bash
# Opción A: Usando connect-azure.bat existente
cd backend
connect-azure.bat
# Copiar/pegar el SQL

# Opción B: Query Editor en Azure Portal
# Azure → SQL Databases → innoad-db → Query Editor
# Copiar/pegar el SQL
```

### PASO 3: Verificar Backend Compila (5 minutos)

```bash
cd backend
mvn clean install -DskipTests

# Debe terminar con:
# BUILD SUCCESS
```

### PASO 4: Build Docker Backend (10 minutos)

```bash
cd backend
docker build -t innoad-backend:latest .
docker run -p 8080:8080 innoad-backend:latest
# Verificar http://localhost:8080/api/ubicaciones/ciudades
```

### PASO 5: Build Frontend Production (10 minutos)

```bash
cd frontend
npm run construir

# Debe crear dist/ sin errores
# Verificar tamaño: dist/ debe ser < 5MB
```

### PASO 6: Build Docker Frontend (5 minutos)

```bash
cd frontend
npm run docker:build:prod

# docker run -p 80:80 innoad-frontend:prod
# Verificar http://localhost:80
```

### PASO 7: Deploy a Azure (30 minutos)

**Backend:**
```bash
# Opción A: Usando Docker desde Azure Container Registry
az containerregistry build --registry <tu-registry> \
  --image innoad-backend:latest \
  --file Dockerfile .

# Opción B: Usando App Service
az appservice plan create --name innoad-plan \
  --resource-group innoad-rg \
  --sku B2 --is-linux

az webapp create --resource-group innoad-rg \
  --plan innoad-plan \
  --name innoad-backend \
  --deployment-container-image-name innoad-backend:latest

# Configurar variables de entorno:
# - DATABASE_URL
# - DATABASE_USER
# - DATABASE_PASSWORD
# - CORS_ORIGINS=https://tu-frontend-url.netlify.app
```

### PASO 8: Deploy a Netlify (15 minutos)

**Frontend:**
```bash
# Build local primero
npm run construir

# Opción A: Netlify CLI
netlify deploy --prod --dir=dist/innoad-frontend/browser

# Opción B: GitHub integration
# 1. Git push a main
# 2. Netlify conecta con GitHub
# 3. Auto-deploy en cada push

# Configurar environment variables en Netlify:
# - API_URL=https://tu-backend-azure.azurewebsites.net
# - ENV=production
```

### PASO 9: Verificación en Producción (15 minutos)

```bash
# Test backend endpoints
curl https://tu-backend.azurewebsites.net/api/ubicaciones/ciudades

# Test frontend
curl https://tu-frontend.netlify.app

# Test flujo completo en navegador
# 1. Navegar a https://tu-frontend.netlify.app
# 2. Login como USUARIO
# 3. Ir a /usuario
# 4. Crear publicación
# 5. Verificar que se carga ciudades
# 6. Verificar que se puede subir archivo
# 7. Verificar que se guarda en BD
```

### PASO 10: SSL/HTTPS (Automático en Azure + Netlify)

- ✓ Azure App Service: HTTPS automático
- ✓ Netlify: HTTPS automático
- ✓ Certificados SSL: Automáticos (Let's Encrypt)

---

## 🔄 FLUJO SIMPLIFICADO PARA "FULL DEPLOYMENT"

```
1. Crear Script SQL (ciudades, lugares, pisos, publicaciones)
   ↓
2. Ejecutar en Azure SQL Database
   ↓
3. Build Backend Docker
   ↓
4. Push Docker a Azure Container Registry
   ↓
5. Deploy a Azure App Service
   ↓
6. Build Frontend
   ↓
7. Deploy a Netlify
   ↓
8. Configurar variables de entorno (BD, API URLs)
   ↓
9. Probar endpoints en producción
   ↓
10. Actualizar DNS si es necesario
    ↓
✓ PROYECTO FULL EN PRODUCCIÓN
```

---

## 📊 ARQUITECTURA FINAL

```
                    ┌─────────────────┐
                    │  Navegador      │
                    │  Usuario        │
                    └────────┬────────┘
                             │ HTTPS
                    ┌────────▼────────┐
                    │   Netlify CDN   │
                    │ innoad.netlify  │
                    │    .app         │
                    └────────┬────────┘
                             │ HTTPS API calls
                    ┌────────▼────────────────┐
                    │  Azure App Service      │
                    │  innoad-backend         │
                    │  :443 (HTTPS)           │
                    └────────┬────────────────┘
                             │ TLS connection
                    ┌────────▼────────────────┐
                    │  Azure SQL Database     │
                    │  innoad-db              │
                    │  (encrypted)            │
                    └─────────────────────────┘
```

---

## 📈 TIEMPO TOTAL ESTIMADO

| Tarea | Tiempo | Total Acumulado |
|-------|--------|-----------------|
| Script SQL | 30 min | 30 min |
| Ejecutar SQL | 15 min | 45 min |
| Build Backend | 10 min | 55 min |
| Build Frontend | 15 min | 1h 10m |
| Deploy Backend | 30 min | 1h 40m |
| Deploy Frontend | 20 min | 2h |
| Testing | 20 min | 2h 20m |
| **TOTAL** | - | **2h 20 minutos** |

---

## ✅ CHECKLIST FINAL

```
Backend:
  [ ] Script SQL creado
  [ ] Tables en Azure SQL
  [ ] Datos iniciales insertados
  [ ] Maven build SUCCESS
  [ ] Docker build OK
  [ ] Imagen pusheada a Azure Container Registry
  [ ] App Service configurado
  [ ] Environment variables seteadas
  [ ] CORS permite Netlify domain
  [ ] GET /api/ubicaciones/ciudades retorna 200

Frontend:
  [ ] npm run construir sin errores
  [ ] dist/ < 5MB
  [ ] Docker build OK
  [ ] Netlify deploy completado
  [ ] Environment variables configuradas
  [ ] API_URL apunta a backend Azure

Integration:
  [ ] Frontend carga datos de backend
  [ ] Cascading dropdowns funcionan
  [ ] Upload file funciona
  [ ] Publicación se guarda en BD
  [ ] Estados se actualizan correctamente
  [ ] Técnico puede aprobar/rechazar
  [ ] Feed muestra publicaciones activas

Production:
  [ ] HTTPS funciona
  [ ] Certificados válidos
  [ ] Performance aceptable (< 3s)
  [ ] Logs accesibles
  [ ] Backups configurados
  [ ] Monitoring activo
```

---

## 🎯 ¿POR QUÉ NO NECESITAS POSTMAN?

### Razones:
1. **Browser DevTools es suficiente** (F12 → Network tab)
2. **Frontend ya testea los endpoints** (al hacer clic)
3. **Logs de Spring Boot muestran errores** (en Azure)
4. **Puedes ver request/response en Network tab**

### Si alguna vez necesitas Postman:
- Testing de endpoints sin UI
- Performance testing (carga)
- Documentación de API
- Testing de error handling

---

## 🔥 PARA DEPLOYMENT DIRECTO (SIN POSTMAN)

### Opción A: Más rápido (TODO AUTOMATIZADO)
```bash
# 1. Crear SQL script
# 2. Ejecutar en Azure
# 3. Git push (activa GitHub Actions)
# 4. GitHub Actions: Build Docker → Push Container Registry
# 5. Redeploy Azure App Service
# 6. Netlify automáticamente redeploy en cada push
```

### Opción B: Manual (tienes control total)
```bash
# 1. Crear SQL script
# 2. Ejecutar en Azure
# 3. Local: mvn clean install
# 4. Local: docker build
# 5. Local: docker tag / docker push
# 6. Azure Portal: redeploy container
# 7. Frontend: npm run construir
# 8. Netlify CLI: netlify deploy --prod
```

---

## 📝 ARCHIVOS A CREAR/MODIFICAR

### Archivos a crear:
```
backend/
├── database-schema-fase4.sql (NUEVO - 80 líneas)
├── src/main/resources/
│   └── db/migration/
│       └── V4_1__Create_Fase4_Tables.sql (Flyway - opcional)
└── scripts/
    └── setup-azure-db.sh (Script de setup)

frontend/
├── src/environments/
│   ├── environment.prod.ts (ACTUALIZAR)
│   └── environment.azure.ts (NUEVO)
└── nginx.conf (ACTUALIZAR para rutas 404)
```

### Archivos ya correctos:
```
docker-compose.yml       ✓
Dockerfile              ✓
.env.example            ✓
pom.xml                 ✓
package.json            ✓
Azure deploy scripts    ✓
Netlify config          ✓
```

---

## 🚀 RESUMEN: PRÓXIMOS 5 PASOS

### 1. SQL Script (Ahora)
```bash
# Crear: database-schema-fase4.sql
# Contiene: ciudades, lugares, pisos, publicaciones
```

### 2. Ejecutar en Azure (15 min)
```bash
# connect-azure.bat
# Pegar SQL y ejecutar
```

### 3. Build Local (15 min)
```bash
cd backend && mvn clean install -DskipTests
cd frontend && npm run construir
```

### 4. Deploy Backend (30 min)
```bash
# docker build → push Container Registry → redeploy App Service
```

### 5. Deploy Frontend (20 min)
```bash
# netlify deploy --prod
```

---

**RESULTADO:** Proyecto FULL en producción en ~2 horas sin Postman.

¿Empezamos por crear el SQL script?
