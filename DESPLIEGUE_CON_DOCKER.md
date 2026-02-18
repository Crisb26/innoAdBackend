# 🚀 Guía de Despliegue con Docker - InnoAd Backend

**Versión:** 2.0.0  
**Última Actualización:** Febrero 4, 2026  
**Objetivo:** Permitir a los compañeros desplegar el backend de InnoAd usando Docker de manera simple y estructurada.

---

## 📋 Requisitos Previos

Antes de iniciar, asegúrate de tener instalado:

- **Docker Desktop** (v20.10 o superior)
- **Docker Compose** (v2.0 o superior) - Viene incluido en Docker Desktop
- **Git** para clonar y actualizar el repositorio
- **Acceso a internet** para descargar dependencias

### Verificar instalación:

```bash
# Verificar Docker
docker --version

# Verificar Docker Compose
docker-compose --version

# Verificar Git
git --version
```

---

## 🔄 Paso 1: Descargar el Código

Si aún no tienes el proyecto:

```bash
# Clonar el repositorio del backend
git clone https://github.com/Crisb26/innoAdBackend.git
cd innoAdBackend/innoadBackend

# O si ya lo tienes, actualizar cambios
git pull origin main
```

---

## ⚙️ Paso 2: Configurar Variables de Entorno

El archivo `docker-compose.yml` incluye variables por defecto, pero **es recomendable personalizarlas**:

### Opción A: Crear un archivo `.env` (RECOMENDADO)

Crea un archivo llamado `.env` en la carpeta `innoadBackend/`:

```env
# Base de Datos
DB_URL=jdbc:mysql://mysql:3306/innoad_db?createDatabaseIfNotExist=true&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=innoad_password_segura_2024

# Seguridad (JWT)
JWT_SECRET=MiClaveSecretaSuperSeguraParaInnoAdQueDebeSerMuyLargaYCompleja2024!@#$%^&*()

# Email (para recuperación de contraseña)
MAIL_USERNAME=tu-email@gmail.com
MAIL_PASSWORD=tu-password-app-gmail

# URLs del Sistema
FRONTEND_URL=http://localhost:3000

# Puerto
SERVER_PORT=8080
```

> **⚠️ IMPORTANTE:** 
> - Nunca compartir el archivo `.env` en git (ya está en `.gitignore`)
> - Cambiar `JWT_SECRET` por algo único y seguro en producción
> - Cambiar credenciales de email y BD por las correctas

### Opción B: Usar valores por defecto

Si usas los valores por defecto del `docker-compose.yml`, estos se aplicarán automáticamente.

---

## 🐳 Paso 3: Construcción de la Imagen Docker

Antes de ejecutar, debemos construir la imagen:

```bash
# Navegar a la carpeta del backend
cd innoadBackend

# Construir la imagen
docker-compose build

# O construir la imagen manualmente sin docker-compose
docker build -t innoad-backend:latest .
```

**Esto puede tomar varios minutos** (15-30 min en la primera ejecución) porque:
1. Descarga Maven y las dependencias de Java
2. Compila todo el código
3. Empaqueta el JAR

---

## ✅ Paso 4: Ejecutar el Backend con Docker

### Opción A: Usar Docker Compose (RECOMENDADO)

```bash
# Iniciar el contenedor en modo foreground (puedes ver los logs)
docker-compose up

# O en modo background (sin ver logs)
docker-compose up -d

# Ver los logs si está en background
docker-compose logs -f backend
```

### Opción B: Ejecutar contenedor manualmente

```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e JWT_SECRET="tu-secreto-aqui" \
  -e DB_URL="jdbc:h2:mem:innoad_db" \
  innoad-backend:latest
```

---

## 🛑 Paso 5: Detener el Backend

Si deseas detener los contenedores:

```bash
# Detener sin eliminar (puedes volver a iniciar después)
docker-compose stop

# Detener y eliminar contenedores
docker-compose down

# Eliminar también volúmenes (datos persistidos)
docker-compose down -v
```

---

## ✔️ Verificar que el Backend está funcionando

Una vez iniciado, verifica que está disponible:

### 1. Chequear via HTTP (en tu navegador o Postman):

```
GET http://localhost:8080/actuator/health
```

Deberías obtener una respuesta como:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    }
  }
}
```

### 2. Ver logs del contenedor:

```bash
docker-compose logs backend

# O en tiempo real
docker-compose logs -f backend
```

### 3. Listar contenedores activos:

```bash
docker ps

# Deberías ver algo como:
# CONTAINER ID  IMAGE               PORTS
# abc123...     innoad-backend:latest  0.0.0.0:8080->8080/tcp
```

---

## 🔗 Integración con Frontend

**Cuando el Backend esté corriendo en Docker**, el Frontend debe apuntar a:

```typescript
// En src/environments/environment.ts (DEVELOPMENT con Docker)
api: {
  gateway: 'http://localhost:8080/api',
  baseUrl: 'http://localhost:8080/api',
  wsUrl: 'ws://localhost:8080/ws'
}
```

Si el Frontend también usa Docker, ambos comparten la red `innoad-network`.

---

## 🐛 Solucionar Problemas

### Error: "Port 8080 is already in use"

```bash
# Ver qué proceso usa el puerto 8080
netstat -ano | findstr :8080  # Windows
lsof -i :8080  # Mac/Linux

# Liberar el puerto o cambiar en docker-compose.yml:
# ports:
#   - "8081:8080"  # Puerto local 8081 -> Puerto contenedor 8080
```

### Error: "Docker daemon is not running"

```bash
# Asegúrate de que Docker Desktop está iniciado
# En Windows/Mac: Abre Docker Desktop desde inicio
# En Linux: sudo systemctl start docker
```

### El contenedor se detiene inmediatamente

Revisa los logs:
```bash
docker-compose logs backend

# Busca mensajes de error relacionados con:
# - Conexión a BD
# - Variables de entorno mal configuradas
# - Problemas de compilación
```

---

## 📊 Monitoreo del Contenedor

### Ver uso de recursos:

```bash
docker stats

# O específicamente el backend
docker stats innoad-backend
```

### Ejecutar comandos dentro del contenedor:

```bash
docker-compose exec backend sh

# Dentro del contenedor puedes hacer:
ls -la /app
java -version
```

---

## 🔄 Actualizar el Código

Si hay cambios nuevos en el repositorio:

```bash
# 1. Descargar cambios
git pull origin main

# 2. Reconstruir la imagen (importante!)
docker-compose build

# 3. Reiniciar los contenedores
docker-compose down
docker-compose up -d
```

---

## 📦 Comparación: Tu Entorno vs Entorno con Docker

### Cristóbal (Sin Docker - Desarrollo Local):

```
Tu PC
  └─ Java 21 instalado localmente
  └─ Maven ejecutándose
  └─ Backend corriendo en puerto 8080
  └─ IDE (IntelliJ IDEA / VS Code)
```

**Ventaja:** Control total, desarrollo más rápido  
**Desventaja:** Depende del SO, requiere Java/Maven instalado

### Compañeros (Con Docker - Contenedorizado):

```
Docker Desktop
  └─ Contenedor Backend
      └─ Java 21 dentro del contenedor
      └─ Maven dentro del contenedor
      └─ Backend corriendo en puerto 8080
      └─ TODO AISLADO - no afecta tu sistema
```

**Ventaja:** Mismo entorno en cualquier PC, fácil de compartir  
**Desventaja:** Pequeño overhead de recursos

---

## 🚀 Resumen Rápido

Si solo quieres los comandos principales:

```bash
# 1. Primero una sola vez: Construir
docker-compose build

# 2. Ejecutar (cada que quieras iniciar)
docker-compose up -d

# 3. Ver que está corriendo
curl http://localhost:8080/actuator/health

# 4. Ver logs
docker-compose logs -f

# 5. Detener
docker-compose down
```

---

## 📞 Preguntas Frecuentes

**P: ¿Debo tener MySQL instalado localmente si uso Docker?**  
R: No, todo está dentro del contenedor. Solo necesitas Docker.

**P: ¿Puedo modificar código y verlos cambios al instante?**  
R: No directamente. Después de cambios, debes hacer `docker-compose build` y `docker-compose up`.

**P: ¿Qué pasa con mis datos si detengo el contenedor?**  
R: Con H2 (BD en memoria), se pierden. Con MySQL, persisten en volúmenes.

**P: ¿Puedo tener múltiples versiones del backend corriendo?**  
R: Sí, en puertos diferentes. Edita `docker-compose.yml`.

---

**¡Listo! Tu backend está dockerizado y listo para compartir.** 🎉

