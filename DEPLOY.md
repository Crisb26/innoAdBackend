# 🚀 Guía de Despliegue InnoAd

## 📋 Índice
- [Backend (Railway)](#backend-railway)
- [Frontend (Vercel/Netlify)](#frontend-vercelnetlify)
- [Base de Datos (Railway PostgreSQL)](#base-de-datos)

---

## 🗄️ Base de Datos (Railway PostgreSQL)

### 1. Crear Base de Datos en Railway

1. Ir a [Railway.app](https://railway.app)
2. Crear nuevo proyecto → **Provision PostgreSQL**
3. Railway generará automáticamente:
   - `DATABASE_URL`: URL completa de conexión
   - `PGDATABASE`: Nombre de la base de datos
   - `PGHOST`: Host del servidor
   - `PGPASSWORD`: Contraseña
   - `PGPORT`: Puerto (generalmente 5432)
   - `PGUSER`: Usuario

4. **Copiar las variables** para usarlas en el backend

### 2. Inicializar Base de Datos

Railway ejecutará automáticamente las migraciones de Hibernate (`ddl-auto: update`).

Si necesitas ejecutar scripts SQL manualmente:
```bash
# Conectarse desde Railway CLI
railway connect postgres

# O usar el script init-database.sql
psql $DATABASE_URL < init-database.sql
```

---

## 🔧 Backend (Railway)

### 1. Preparar Repositorio

Asegúrate de tener estos archivos:
- ✅ `Dockerfile`
- ✅ `railway.json`
- ✅ `.railwayignore`
- ✅ `pom.xml`
- ✅ `src/main/resources/application-prod.yml`

### 2. Desplegar en Railway

**Opción A: Desde GitHub (Recomendado)**

1. Push tu código a GitHub:
```bash
git add .
git commit -m "Preparar para despliegue"
git push origin main
```

2. En Railway:
   - New Project → **Deploy from GitHub repo**
   - Seleccionar tu repositorio
   - Railway detectará automáticamente el Dockerfile

3. Configurar Variables de Entorno:
```
# Base de Datos (copiar desde PostgreSQL service)
DATABASE_URL=postgresql://...
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=...

# JWT
JWT_SECRET=MiClaveSecretaSuperSeguraParaInnoAd2024!
JWT_EXPIRATION=86400000

# Correo (Gmail con App Password)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=tu-email@gmail.com
MAIL_PASSWORD=tu-app-password

# Mantenimiento
MAINTENANCE_CODE=93022611184
MAINTENANCE_EMAIL=admin@innoad.com

# IA (OpenAI)
AI_API_KEY=tu-api-key-de-openai
AI_MODEL=gpt-4

# Perfil de Spring
SPRING_PROFILES_ACTIVE=prod
```

**Opción B: Railway CLI**

```bash
# Instalar Railway CLI
npm install -g @railway/cli

# Login
railway login

# Enlazar proyecto
railway link

# Desplegar
railway up
```

### 3. Verificar Despliegue

Railway generará una URL pública: `https://innoad-backend-production.up.railway.app`

Probar health check:
```bash
curl https://tu-app.up.railway.app/actuator/health
```

---

## 🌐 Frontend (Vercel/Netlify)

### Preparar para Despliegue

#### 1. Configurar Environment Variables

Crear archivo `src/environments/environment.prod.ts`:
```typescript
export const environment = {
  production: true,
  api: {
    baseUrl: 'https://tu-backend.up.railway.app/api'
  }
};
```

#### 2. Actualizar `angular.json`

Asegurarse de que la configuración de producción use el environment correcto:
```json
"configurations": {
  "production": {
    "fileReplacements": [
      {
        "replace": "src/environments/environment.ts",
        "with": "src/environments/environment.prod.ts"
      }
    ]
  }
}
```

### Opción A: Vercel (Recomendado)

#### 1. Instalar Vercel CLI
```bash
npm install -g vercel
```

#### 2. Desplegar
```bash
cd FRONTEND/innoadFrontend
vercel --prod
```

O desde la web:
1. Ir a [Vercel.com](https://vercel.com)
2. Import Git Repository
3. Framework Preset: **Angular**
4. Build Command: `npm run build -- --configuration production`
5. Output Directory: `dist/innoad-frontend/browser`

#### 3. Variables de Entorno en Vercel
```
NODE_ENV=production
```

### Opción B: Netlify

#### 1. Crear `netlify.toml`
```toml
[build]
  command = "npm run build -- --configuration production"
  publish = "dist/innoad-frontend/browser"

[[redirects]]
  from = "/*"
  to = "/index.html"
  status = 200
```

#### 2. Desplegar
```bash
npm install -g netlify-cli
netlify deploy --prod
```

O desde la web: [Netlify.com](https://netlify.com) → Import from Git

---

## 🔐 Configuración de CORS

Verificar en el backend que el frontend esté permitido:

```java
@Configuration
public class CorsConfiguration {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(
            "http://localhost:4200",
            "https://tu-app.vercel.app",
            "https://tu-app.netlify.app"
        ));
        // ...
    }
}
```

---

## ✅ Checklist Final

### Backend
- [ ] Base de datos PostgreSQL creada en Railway
- [ ] Variables de entorno configuradas
- [ ] Backend desplegado en Railway
- [ ] Health check responde OK
- [ ] CORS configurado para frontend

### Frontend
- [ ] `environment.prod.ts` apunta al backend Railway
- [ ] Build de producción exitoso
- [ ] Frontend desplegado en Vercel/Netlify
- [ ] Navegación funciona correctamente
- [ ] Login/registro funcionan

---

## 🐛 Troubleshooting

### Backend no inicia
```bash
# Ver logs en Railway
railway logs

# Verificar variables de entorno
railway variables
```

### Frontend no conecta al backend
- Verificar `environment.prod.ts` tiene la URL correcta
- Verificar CORS en el backend
- Verificar Network tab en DevTools

### Base de datos no conecta
- Verificar que `DATABASE_URL` esté correctamente formateada
- Railway PostgreSQL debe estar en el mismo proyecto
- Verificar que el servicio PostgreSQL esté activo

---

## 📞 Soporte

Para más información:
- Railway Docs: https://docs.railway.app
- Vercel Docs: https://vercel.com/docs
- Angular Deployment: https://angular.io/guide/deployment
