# 📋 Guía de Configuración - InnoAd Backend

## Tabla de Contenidos
1. [Variables de Entorno](#variables-de-entorno)
2. [OpenAI API](#openai-api)
3. [Email SMTP](#email-smtp)
4. [Base de Datos](#base-de-datos)
5. [Ejecución Local](#ejecución-local)
6. [Despliegue en Railway](#despliegue-en-railway)

---

## Variables de Entorno

### Pasos para configurar en desarrollo local:

1. **Copiar archivo de ejemplo:**
   ```bash
   cp .env.example .env
   ```

2. **Editar `.env` con tus valores:**
   ```bash
   # En Windows con VSCode
   code .env
   ```

3. **Cargar variables (en terminal de desarrollo):**
   ```bash
   # Linux/Mac
   source .env
   
   # Windows PowerShell
   Get-Content .env | ForEach-Object { if ($_ -like '*=*') { $parts = $_.split('='); [Environment]::SetEnvironmentVariable($parts[0], $parts[1]) } }
   ```

4. **Verificar que se cargaron:**
   ```bash
   echo $DATABASE_URL
   echo $OPENAI_API_KEY
   ```

### IMPORTANTE ⚠️
- **NUNCA** commitear `.env` con valores reales a Git
- Mantener `.env` en `.gitignore` (ya está configurado)
- Para Railway, configurar en el dashboard de Railway, no subir variables

---

## OpenAI API

### Obtener la API Key:

1. Ir a https://platform.openai.com/account/api-keys
2. Crear nueva API key o copiar existente
3. La clave comienza con `sk-`

### Configuración:

```env
# .env o variables de Railway
OPENAI_API_KEY=sk-tu-clave-aqui
OPENAI_MODEL=gpt-4
OPENAI_MAX_TOKENS=2000
OPENAI_TEMPERATURE=0.7
OPENAI_TIMEOUT_SECONDS=30
OPENAI_MAX_RETRIES=3
OPENAI_RETRY_DELAY_MS=1000
```

### Parámetros explicados:

| Parámetro | Rango | Recomendado | Descripción |
|-----------|-------|-------------|-------------|
| `OPENAI_MODEL` | - | `gpt-4` | Modelo a usar (gpt-4, gpt-3.5-turbo) |
| `OPENAI_MAX_TOKENS` | 1-4000 | 2000 | Máximo de tokens en respuesta |
| `OPENAI_TEMPERATURE` | 0-2 | 0.7 | Creatividad (0=determinista, 2=muy creativo) |
| `OPENAI_TIMEOUT_SECONDS` | 1+ | 30 | Tiempo máximo de espera (segundos) |
| `OPENAI_MAX_RETRIES` | 0+ | 3 | Reintentos en caso de error |

### Ejemplo de uso en código:

```java
// Los servicios automáticamente cargan la configuración
@Autowired
private ServicioOpenAI servicioOpenAI;

// Ejemplo
String respuesta = servicioOpenAI.llamarAPI(pregunta, prompt, contexto);
```

---

## Email SMTP

### Configurar con Gmail:

1. Habilitar "Acceso a apps menos seguras" o usar App Password:
   - https://myaccount.google.com/security
   - Buscar "Contraseña de aplicación"
   - Seleccionar "Mail" y "Windows Computer"
   - Copiar la contraseña de 16 caracteres

2. Configurar variables:
   ```env
   EMAIL_SMTP_HOST=smtp.gmail.com
   EMAIL_SMTP_PORT=587
   EMAIL_SMTP_USER=tu-email@gmail.com
   EMAIL_SMTP_PASSWORD=xxxx xxxx xxxx xxxx
   EMAIL_FROM=soporte@innoad.com
   EMAIL_FROM_NAME=InnoAd - Soporte
   MAIL_MAX_RETRIES=3
   MAIL_RETRY_DELAY_MS=2000
   ```

### Otros proveedores SMTP:

**Outlook/Office 365:**
```env
EMAIL_SMTP_HOST=smtp-mail.outlook.com
EMAIL_SMTP_PORT=587
```

**SendGrid:**
```env
EMAIL_SMTP_HOST=smtp.sendgrid.net
EMAIL_SMTP_PORT=587
EMAIL_SMTP_USER=apikey
EMAIL_SMTP_PASSWORD=SG.xxxxxxxxxxxxxxx
```

**Mailgun:**
```env
EMAIL_SMTP_HOST=smtp.mailgun.org
EMAIL_SMTP_PORT=587
```

### Ejemplo de uso:

```java
@Autowired
private ServicioEmailIA servicioEmailIA;

// Enviar email desde IA
servicioEmailIA.enviarEmail(
    idUsuario,
    "destinatario@example.com",
    "Asunto del email",
    "Contenido del email"
);
```

---

## Base de Datos

### Opción 1: PostgreSQL Local

1. **Instalar PostgreSQL:**
   - Windows: Descargar de https://www.postgresql.org/download/windows/
   - Mac: `brew install postgresql`
   - Linux: `sudo apt-get install postgresql`

2. **Crear base de datos:**
   ```sql
   psql -U postgres
   CREATE DATABASE innoad_db;
   CREATE USER admin WITH PASSWORD 'tu_password_seguro';
   ALTER ROLE admin SET client_encoding TO 'utf8';
   ALTER ROLE admin SET default_transaction_isolation TO 'read committed';
   ALTER ROLE admin SET timezone TO 'America/Bogota';
   GRANT ALL PRIVILEGES ON DATABASE innoad_db TO admin;
   ```

3. **Configurar variables:**
   ```env
   DATABASE_URL=postgresql://admin:tu_password_seguro@localhost:5432/innoad_db
   DATABASE_USERNAME=admin
   DATABASE_PASSWORD=tu_password_seguro
   ```

### Opción 2: Railway (Producción)

Railway genera automáticamente `DATABASE_URL`. No hacer nada más.

---

## Ejecución Local

### Requisitos previos:
- Java 21 JDK instalado
- Maven 3.9+ instalado
- PostgreSQL corriendo
- Variables de entorno configuradas

### Comandos:

```bash
# Limpiar y compilar
mvn clean compile

# Ejecutar tests
mvn test

# Ejecutar la aplicación
mvn spring-boot:run

# Generar JAR (producción)
mvn clean package -DskipTests
java -jar target/innoad-backend-*.jar
```

### Verificar que está corriendo:

```bash
# Health check
curl http://localhost:8080/actuator/health

# API docs (Swagger)
open http://localhost:8080/swagger-ui.html
```

---

## Despliegue en Railway

### Pasos:

1. **Conectar repositorio:**
   - Railway: New → Github Repo
   - Seleccionar repositorio `innoAdBackend`

2. **Agregar PostgreSQL:**
   - Railway Dashboard → Add Service → PostgreSQL
   - Railway automáticamente agrega `DATABASE_URL`

3. **Configurar variables:**
   - Railway Dashboard → Variables
   - Agregar:
     ```
     OPENAI_API_KEY=sk-...
     EMAIL_SMTP_HOST=smtp.gmail.com
     EMAIL_SMTP_PORT=587
     EMAIL_SMTP_USER=tu-email@gmail.com
     EMAIL_SMTP_PASSWORD=...
     JWT_SECRET=tu-clave-secreta
     SPRING_PROFILES_ACTIVE=prod
     ```

4. **Deploy automático:**
   - Railway detecta cambios en Git
   - Compila y despliega automáticamente

### URLs de Railway:

```
API: https://innoad-backend-prod.up.railway.app
Healthcheck: https://innoad-backend-prod.up.railway.app/actuator/health
```

---

## 🔒 Checklist de Seguridad

- [ ] Variables sensibles en `.env` (no en código)
- [ ] `.env` en `.gitignore`
- [ ] JWT_SECRET es fuerte (mínimo 256 bits)
- [ ] OPENAI_API_KEY no publicada
- [ ] EMAIL_SMTP_PASSWORD no publicada
- [ ] DATABASE_PASSWORD no publicada
- [ ] En Railway: todas las variables configuradas
- [ ] En producción: SPRING_PROFILES_ACTIVE=prod

---

## 🐛 Troubleshooting

### Error: "OpenAI API Key no configurada"
```
Solución: Verificar que OPENAI_API_KEY está en .env o variables de Railway
```

### Error: "Mail username no configurado"
```
Solución: Verificar EMAIL_SMTP_USER en .env
```

### Error: "Database connection refused"
```
Solución: 
1. Verificar PostgreSQL está corriendo: sudo systemctl status postgresql
2. Verificar DATABASE_URL es correcta
3. Verificar credenciales de base de datos
```

### Puerto 8080 ya está en uso
```
Solución: Cambiar puerto
export SERVER_PORT=8081
mvn spring-boot:run
```

---

## 📞 Soporte

Para más información:
- Spring Boot: https://spring.io/projects/spring-boot
- OpenAI API: https://platform.openai.com/docs
- Railway: https://railway.app/docs

---

**Última actualización:** Diciembre 2025
**Versión:** 2.0.0
