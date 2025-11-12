# InnoAd - Guía de Despliegue

## 🚀 Despliegue Rápido

### Opción 1: Despliegue Completo (Recomendado)
```bash
# Ejecutar script de despliegue
deploy.bat

# Seleccionar opción 1 (Despliegue COMPLETO)
```

Esto iniciará:
- ✅ PostgreSQL (puerto 5432)
- ✅ Backend API (puerto 8080)
- ✅ Frontend (puerto 80)

**Acceso:**
- Frontend: http://localhost
- Backend: http://localhost:8080
- API Docs: http://localhost:8080/swagger-ui.html

---

## 📋 Prerequisitos

- **Docker Desktop** instalado y corriendo
- **Puertos disponibles:** 80, 8080, 5432

---

## 🔧 Configuración

### Variables de Entorno (Opcional)

Crea un archivo `.env` en la raíz del proyecto:

```env
# Base de Datos
DB_PASSWORD=Cris930226**

# Seguridad
JWT_SECRET=MiClaveSecretaSuperSeguraParaInnoAdQueDebeSerMuyLarga2024!
MAINTENANCE_CODE=93022611184

# Email (Opcional)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=tu-email@gmail.com
MAIL_PASSWORD=tu-password-de-aplicacion

# IA (Opcional)
AI_API_KEY=tu-api-key-openai
```

---

## 🧪 Probar Conexión

Después del despliegue, ejecuta:

```bash
test-connection.bat
```

Este script verifica:
- ✅ Backend activo
- ✅ Base de datos conectada
- ✅ API endpoints disponibles
- ✅ CORS configurado
- ✅ Swagger UI accesible

---

## 📱 Iniciar Solo el Frontend

Si ya tienes el backend corriendo:

```bash
cd ../../FRONTEND/innoadFrontend
npm start
```

O con proxy configurado:

```bash
npm run iniciar:proxy
```

El frontend estará disponible en: http://localhost:4200

---

## 🐳 Comandos Docker Útiles

### Ver logs en tiempo real
```bash
docker-compose -f docker-compose.full.yml logs -f
```

### Ver logs de un servicio específico
```bash
docker-compose -f docker-compose.full.yml logs -f backend
docker-compose -f docker-compose.full.yml logs -f postgres
docker-compose -f docker-compose.full.yml logs -f frontend
```

### Reiniciar servicios
```bash
docker-compose -f docker-compose.full.yml restart
```

### Detener servicios
```bash
docker-compose -f docker-compose.full.yml down
```

### Detener y eliminar volúmenes (CUIDADO: borra la BD)
```bash
docker-compose -f docker-compose.full.yml down -v
```

### Ver estado de servicios
```bash
docker-compose -f docker-compose.full.yml ps
```

---

## 🔌 Endpoints de la API

### Autenticación
- `POST /api/v1/auth/registro` - Registro de usuario
- `POST /api/v1/auth/login` - Inicio de sesión
- `POST /api/v1/auth/refresh` - Refrescar token
- `POST /api/v1/auth/logout` - Cerrar sesión

### Usuarios
- `GET /api/v1/users` - Listar usuarios (Admin)
- `GET /api/v1/users/{id}` - Obtener usuario
- `PUT /api/v1/users/{id}` - Actualizar usuario
- `DELETE /api/v1/users/{id}` - Eliminar usuario

### Contenidos
- `GET /api/v1/contents` - Listar contenidos
- `POST /api/v1/contents` - Crear contenido
- `PUT /api/v1/contents/{id}` - Actualizar contenido
- `DELETE /api/v1/contents/{id}` - Eliminar contenido

### Pantallas
- `GET /api/v1/screens` - Listar pantallas
- `POST /api/v1/screens` - Registrar pantalla
- `GET /api/v1/screens/{id}` - Obtener pantalla
- `PUT /api/v1/screens/{id}` - Actualizar pantalla

### Administración
- `GET /api/admin/stats` - Estadísticas del sistema
- `GET /api/admin/users` - Gestión de usuarios
- `POST /api/admin/maintenance` - Modo mantenimiento

### Monitoreo
- `GET /actuator/health` - Estado del sistema
- `GET /actuator/metrics` - Métricas
- `GET /actuator/info` - Información de la app

### Documentación
- `GET /swagger-ui.html` - Swagger UI
- `GET /api-docs` - OpenAPI JSON

---

## 🏗️ Arquitectura

```
┌─────────────────────────────────────────────┐
│           Frontend (Angular 18)              │
│         http://localhost:4200 / :80          │
└────────────────┬────────────────────────────┘
                 │
                 │ HTTP/REST + WebSocket
                 │
┌────────────────▼────────────────────────────┐
│         Backend API (Spring Boot)            │
│            http://localhost:8080             │
│                                              │
│  Módulos:                                    │
│  ├── auth/      (Autenticación)             │
│  ├── content/   (Contenidos)                │
│  ├── screens/   (Pantallas)                 │
│  ├── admin/     (Administración)            │
│  ├── ia/        (Inteligencia Artificial)   │
│  └── shared/    (Compartido)                │
└────────────────┬────────────────────────────┘
                 │
                 │ JDBC
                 │
┌────────────────▼────────────────────────────┐
│       PostgreSQL Database 16                 │
│           localhost:5432                     │
│             innoad_db                        │
└──────────────────────────────────────────────┘
```

---

## 🔐 Usuarios por Defecto

**Administrador:**
- Usuario: `admin@innoad.com`
- Password: `Admin123!`

**Usuario de Prueba:**
- Usuario: `usuario@innoad.com`
- Password: `Usuario123!`

---

## ⚠️ Troubleshooting

### El backend no inicia
1. Verifica que PostgreSQL esté corriendo
2. Revisa los logs: `docker-compose -f docker-compose.full.yml logs backend`
3. Verifica que el puerto 8080 no esté ocupado

### El frontend no conecta con el backend
1. Ejecuta `test-connection.bat` para verificar la conexión
2. Verifica el archivo `proxy.conf.json`
3. Asegúrate de que CORS esté configurado en el backend

### Error de base de datos
1. Verifica que PostgreSQL esté activo
2. Revisa las credenciales en docker-compose.full.yml
3. Verifica los logs de PostgreSQL: `docker-compose -f docker-compose.full.yml logs postgres`

### Puerto ocupado
```bash
# Windows - Liberar puerto 8080
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

---

## 📊 Monitoreo

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

### Métricas
```bash
curl http://localhost:8080/actuator/metrics
```

### Prometheus
```bash
curl http://localhost:8080/actuator/prometheus
```

---

## 🎯 Próximos Pasos

1. ✅ Backend desplegado con arquitectura modular
2. ✅ Frontend configurado y listo
3. ✅ Base de datos PostgreSQL
4. ⏳ Configurar email (opcional)
5. ⏳ Configurar API de IA (opcional)
6. ⏳ Configurar SSL/HTTPS para producción
7. ⏳ Configurar dominio personalizado

---

## 📞 Soporte

Para más información, consulta la documentación completa o revisa los logs del sistema.
