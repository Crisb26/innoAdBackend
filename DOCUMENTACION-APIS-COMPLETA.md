# 📚 Documentación Completa de APIs - InnoAd Backend

## 🎯 URL Base del Backend
```
http://localhost:8081
```

---

## 🔐 1. APIs DE AUTENTICACIÓN

### 1.1 Registro Público de Usuarios
**Endpoint:** `POST /api/v1/autenticacion/registrarse`
**Acceso:** Público (sin autenticación)
**Descripción:** Permite que cualquier persona se registre como USUARIO en la plataforma

**Request Body:**
```json
{
  "nombre": "Juan",
  "apellido": "Pérez",
  "email": "juan@example.com",
  "nombreUsuario": "juanperez",
  "contrasena": "Password123!",
  "telefono": "+57 300 123 4567",
  "empresa": "Mi Empresa S.A.S",
  "cargo": "Gerente de Marketing"
}
```

**Validaciones:**
- `nombre`: 2-100 caracteres, obligatorio
- `apellido`: 2-100 caracteres, obligatorio
- `email`: Email válido, obligatorio
- `nombreUsuario`: 4-50 caracteres, solo letras, números, puntos, guiones y guiones bajos, obligatorio
- `contrasena`: Mínimo 8 caracteres, debe contener al menos una mayúscula, una minúscula, un número y un carácter especial, obligatorio
- `telefono`: Máximo 20 caracteres, opcional
- `empresa`: Máximo 100 caracteres, opcional
- `cargo`: Máximo 100 caracteres, opcional

**Response:**
```json
{
  "exitoso": true,
  "mensaje": "Usuario registrado exitosamente. Por favor verifica tu email.",
  "datos": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tipoToken": "Bearer",
    "id": 5,
    "nombreUsuario": "juanperez",
    "email": "juan@example.com",
    "nombreCompleto": "Juan Pérez",
    "rol": "USUARIO",
    "verificado": false
  },
  "timestamp": "2025-11-04T10:30:00Z"
}
```

---

### 1.2 Iniciar Sesión
**Endpoint:** `POST /api/v1/autenticacion/iniciar-sesion`
**Acceso:** Público (sin autenticación)

**Request Body:**
```json
{
  "nombreUsuarioOEmail": "juanperez",
  "contrasena": "Password123!"
}
```

**Response:**
```json
{
  "exitoso": true,
  "mensaje": "Autenticación exitosa",
  "datos": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenActualizacion": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "usuario": {
      "id": 5,
      "nombreUsuario": "juanperez",
      "email": "juan@example.com",
      "nombreCompleto": "Juan Pérez",
      "rol": {
        "nombre": "Usuario"
      }
    },
    "expiraEn": 86400
  },
  "timestamp": "2025-11-04T10:30:00Z"
}
```

---

### 1.3 Recuperar Contraseña
**Endpoint:** `POST /api/v1/autenticacion/recuperar-contrasena`
**Acceso:** Público (sin autenticación)

**Request Body:**
```json
{
  "email": "juan@example.com"
}
```

**Response:**
```json
{
  "exitoso": true,
  "mensaje": "Email de recuperación enviado exitosamente. Por favor revisa tu correo.",
  "timestamp": "2025-11-04T10:30:00Z"
}
```

---

### 1.4 Restablecer Contraseña
**Endpoint:** `POST /api/v1/autenticacion/restablecer-contrasena`
**Acceso:** Público (sin autenticación)

**Request Body:**
```json
{
  "token": "abc123-token-from-email",
  "nuevaContrasena": "NewPassword123!"
}
```

**Response:**
```json
{
  "exitoso": true,
  "mensaje": "Contraseña restablecida exitosamente. Ya puedes iniciar sesión.",
  "timestamp": "2025-11-04T10:30:00Z"
}
```

---

### 1.5 Refrescar Token
**Endpoint:** `POST /api/v1/autenticacion/refrescar-token`
**Acceso:** Público (sin autenticación)

**Request Body:**
```json
{
  "tokenActualizacion": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response:**
```json
{
  "exitoso": true,
  "mensaje": "Token renovado",
  "datos": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenActualizacion": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "usuario": {...},
    "expiraEn": 86400
  }
}
```

---

## 📺 2. APIs DE PANTALLAS (RASPBERRY PI)

**Nota:** Todos estos endpoints requieren autenticación (Header: `Authorization: Bearer <token>`)

### 2.1 Crear Pantalla
**Endpoint:** `POST /api/v1/pantallas`
**Acceso:** Usuarios autenticados

**Request Body:**
```json
{
  "nombre": "Pantalla Principal - Recepción",
  "descripcion": "Pantalla ubicada en la recepción del edificio",
  "ubicacion": "Recepción - Edificio A - Piso 1",
  "resolucion": "1920x1080",
  "orientacion": "HORIZONTAL",
  "notas": "Pantalla operativa 24/7"
}
```

**Validaciones:**
- `nombre`: 3-100 caracteres, obligatorio
- `descripcion`: Máximo 500 caracteres, opcional
- `ubicacion`: Máximo 200 caracteres, opcional
- `resolucion`: Formato "1920x1080", opcional
- `orientacion`: "HORIZONTAL" o "VERTICAL", opcional
- `notas`: Máximo 500 caracteres, opcional

**Response:**
```json
{
  "exitoso": true,
  "mensaje": "Pantalla creada exitosamente",
  "datos": {
    "id": 1,
    "nombre": "Pantalla Principal - Recepción",
    "descripcion": "Pantalla ubicada en la recepción del edificio",
    "codigoIdentificacion": "PI-A1B2C3D4",
    "estado": "INACTIVA",
    "ubicacion": "Recepción - Edificio A - Piso 1",
    "resolucion": "1920x1080",
    "orientacion": "HORIZONTAL",
    "usuarioId": 5,
    "nombreUsuario": "juanperez",
    "fechaRegistro": "2025-11-04T10:30:00",
    "ultimaConexion": null,
    "ultimaSincronizacion": null,
    "direccionIp": null,
    "versionSoftware": null,
    "informacionSistema": null,
    "notas": "Pantalla operativa 24/7",
    "estaConectada": false,
    "cantidadContenidos": 0
  }
}
```

**⚠️ IMPORTANTE:** Guarda el `codigoIdentificacion` (PI-A1B2C3D4), lo necesitarás para configurar la Raspberry Pi.

---

### 2.2 Listar Mis Pantallas
**Endpoint:** `GET /api/v1/pantallas`
**Acceso:** Usuarios autenticados

**Response:**
```json
{
  "exitoso": true,
  "mensaje": "Pantallas obtenidas exitosamente",
  "datos": [
    {
      "id": 1,
      "nombre": "Pantalla Principal - Recepción",
      "codigoIdentificacion": "PI-A1B2C3D4",
      "estado": "ACTIVA",
      "ubicacion": "Recepción - Edificio A - Piso 1",
      "resolucion": "1920x1080",
      "orientacion": "HORIZONTAL",
      "estaConectada": true,
      "cantidadContenidos": 5,
      "ultimaConexion": "2025-11-04T10:28:00"
    }
  ]
}
```

---

### 2.3 Obtener Pantalla por ID
**Endpoint:** `GET /api/v1/pantallas/{id}`
**Acceso:** Usuarios autenticados

**Response:**
```json
{
  "exitoso": true,
  "mensaje": "Pantalla obtenida exitosamente",
  "datos": {
    "id": 1,
    "nombre": "Pantalla Principal - Recepción",
    "codigoIdentificacion": "PI-A1B2C3D4",
    "estado": "ACTIVA",
    ...
  }
}
```

---

### 2.4 Actualizar Pantalla
**Endpoint:** `PUT /api/v1/pantallas/{id}`
**Acceso:** Usuarios autenticados (propietario o admin)

**Request Body:**
```json
{
  "nombre": "Pantalla Recepción (Actualizada)",
  "descripcion": "Nueva descripción",
  "ubicacion": "Nueva ubicación",
  "resolucion": "1920x1080",
  "orientacion": "HORIZONTAL",
  "notas": "Notas actualizadas"
}
```

---

### 2.5 Activar Pantalla
**Endpoint:** `POST /api/v1/pantallas/{id}/activar`
**Acceso:** Usuarios autenticados (propietario o admin)

**Response:**
```json
{
  "exitoso": true,
  "mensaje": "Pantalla activada exitosamente",
  "datos": {
    "id": 1,
    "estado": "ACTIVA",
    ...
  }
}
```

---

### 2.6 Desactivar Pantalla
**Endpoint:** `POST /api/v1/pantallas/{id}/desactivar`
**Acceso:** Usuarios autenticados (propietario o admin)

---

### 2.7 Eliminar Pantalla
**Endpoint:** `DELETE /api/v1/pantallas/{id}`
**Acceso:** Usuarios autenticados (propietario o admin)

---

## 🎨 3. APIs DE CONTENIDOS PUBLICITARIOS

**Nota:** Todos estos endpoints requieren autenticación

### 3.1 Crear Contenido de Texto/HTML
**Endpoint:** `POST /api/v1/contenidos`
**Acceso:** Usuarios autenticados

**Request Body (Texto):**
```json
{
  "titulo": "Promoción del Mes",
  "descripcion": "Descuento especial en todos nuestros productos",
  "tipo": "TEXTO",
  "contenidoTexto": "¡50% de descuento en toda la tienda! Aprovecha esta increíble oferta.",
  "duracionSegundos": 10,
  "pantallaId": 1,
  "orden": 1,
  "prioridad": "ALTA",
  "estado": "ACTIVO",
  "fechaInicio": "2025-11-04T00:00:00",
  "fechaFin": "2025-11-30T23:59:59",
  "tags": "promocion,descuento,venta"
}
```

**Request Body (HTML):**
```json
{
  "titulo": "Banner HTML Personalizado",
  "descripcion": "Banner con HTML personalizado",
  "tipo": "HTML",
  "contenidoHtml": "<div style='background: linear-gradient(to right, #ff6b6b, #4ecdc4); color: white; padding: 40px; text-align: center; font-size: 48px;'><h1>¡GRAN APERTURA!</h1><p>Nuevos productos disponibles</p></div>",
  "duracionSegundos": 15,
  "pantallaId": 1,
  "orden": 2,
  "prioridad": "NORMAL",
  "estado": "ACTIVO",
  "tags": "html,banner"
}
```

**Validaciones:**
- `titulo`: 3-200 caracteres, obligatorio
- `tipo`: "IMAGEN", "VIDEO", "TEXTO" o "HTML", obligatorio
- `duracionSegundos`: 1-3600 segundos, obligatorio
- `pantallaId`: ID de pantalla válida, obligatorio
- `orden`: Número positivo, opcional (default: 0)
- `prioridad`: "BAJA", "NORMAL", "ALTA" o "URGENTE", opcional (default: "NORMAL")
- `estado`: "BORRADOR", "ACTIVO", "PAUSADO" o "FINALIZADO", opcional (default: "BORRADOR")

**Response:**
```json
{
  "exitoso": true,
  "mensaje": "Contenido creado exitosamente",
  "datos": {
    "id": 1,
    "titulo": "Promoción del Mes",
    "descripcion": "Descuento especial en todos nuestros productos",
    "tipo": "TEXTO",
    "contenidoTexto": "¡50% de descuento en toda la tienda!...",
    "duracionSegundos": 10,
    "orden": 1,
    "prioridad": "ALTA",
    "estado": "ACTIVO",
    "pantallaId": 1,
    "nombrePantalla": "Pantalla Principal - Recepción",
    "usuarioId": 5,
    "nombreUsuario": "juanperez",
    "fechaCreacion": "2025-11-04T10:30:00",
    "fechaActualizacion": "2025-11-04T10:30:00",
    "vecesReproducido": 0,
    "ultimaReproduccion": null,
    "tags": "promocion,descuento,venta",
    "estaActivo": true,
    "enPeriodoPublicacion": true
  }
}
```

---

### 3.2 Crear Contenido con Archivo (Imagen/Video)
**Endpoint:** `POST /api/v1/contenidos/con-archivo`
**Content-Type:** `multipart/form-data`
**Acceso:** Usuarios autenticados

**Form Data:**
```
archivo: [File] (imagen.jpg o video.mp4)
titulo: "Banner Promocional Navidad"
descripcion: "Imagen promocional para campaña navideña"
tipo: "IMAGEN"
duracionSegundos: 10
pantallaId: 1
orden: 0
prioridad: "ALTA"
estado: "ACTIVO"
tags: "navidad,promocion"
```

**Validaciones de Archivos:**
- **Imágenes:** Máximo 10 MB, tipos: image/*
- **Videos:** Máximo 100 MB, tipos: video/*

**Response:**
```json
{
  "exitoso": true,
  "mensaje": "Contenido creado exitosamente",
  "datos": {
    "id": 2,
    "titulo": "Banner Promocional Navidad",
    "tipo": "IMAGEN",
    "urlArchivo": "http://localhost:8081/uploads/abc123-uuid.jpg",
    "nombreArchivo": "imagen.jpg",
    "tamanoArchivo": 2048576,
    "tipoMime": "image/jpeg",
    "duracionSegundos": 10,
    ...
  }
}
```

---

### 3.3 Listar Mis Contenidos
**Endpoint:** `GET /api/v1/contenidos`
**Acceso:** Usuarios autenticados

**Response:**
```json
{
  "exitoso": true,
  "mensaje": "Contenidos obtenidos exitosamente",
  "datos": [
    {
      "id": 1,
      "titulo": "Promoción del Mes",
      "tipo": "TEXTO",
      "estado": "ACTIVO",
      "pantallaId": 1,
      "nombrePantalla": "Pantalla Principal - Recepción",
      "duracionSegundos": 10,
      "vecesReproducido": 142,
      ...
    }
  ]
}
```

---

### 3.4 Listar Contenidos de una Pantalla
**Endpoint:** `GET /api/v1/contenidos/pantalla/{pantallaId}`
**Acceso:** Usuarios autenticados (propietario o admin)

---

### 3.5 Obtener Contenido por ID
**Endpoint:** `GET /api/v1/contenidos/{id}`
**Acceso:** Usuarios autenticados (propietario o admin)

---

### 3.6 Actualizar Contenido
**Endpoint:** `PUT /api/v1/contenidos/{id}`
**Acceso:** Usuarios autenticados (propietario o admin)

**Request Body:**
```json
{
  "titulo": "Título actualizado",
  "descripcion": "Nueva descripción",
  "contenidoTexto": "Nuevo texto",
  "duracionSegundos": 15,
  "orden": 5,
  "prioridad": "URGENTE",
  "estado": "ACTIVO",
  "fechaInicio": "2025-11-04T00:00:00",
  "fechaFin": "2025-12-31T23:59:59",
  "tags": "actualizado,nuevo"
}
```

---

### 3.7 Cambiar Estado de Contenido
**Endpoint:** `PATCH /api/v1/contenidos/{id}/estado`
**Acceso:** Usuarios autenticados (propietario o admin)

**Query Params:**
- `estado`: BORRADOR | ACTIVO | PAUSADO | FINALIZADO

**Ejemplo:**
```
PATCH /api/v1/contenidos/1/estado?estado=PAUSADO
```

---

### 3.8 Eliminar Contenido
**Endpoint:** `DELETE /api/v1/contenidos/{id}`
**Acceso:** Usuarios autenticados (propietario o admin)

---

## 🍓 4. APIs PARA RASPBERRY PI

**Nota:** Estas APIs son públicas (sin autenticación) para facilitar el acceso desde Raspberry Pi

### 4.1 Sincronizar Contenidos
**Endpoint:** `GET /api/v1/raspberry/sincronizar/{codigoIdentificacion}`
**Acceso:** Público

**Query Params (opcionales):**
- `direccionIp`: IP de la Raspberry Pi
- `versionSoftware`: Versión del software del player
- `informacionSistema`: Info del sistema (modelo, RAM, etc.)

**Ejemplo:**
```
GET /api/v1/raspberry/sincronizar/PI-A1B2C3D4?direccionIp=192.168.1.100&versionSoftware=1.0.0&informacionSistema=Raspberry Pi 4 Model B - 4GB RAM
```

**Response:**
```json
{
  "exitoso": true,
  "mensaje": "Contenidos sincronizados exitosamente",
  "datos": [
    {
      "id": 1,
      "titulo": "Promoción del Mes",
      "tipo": "TEXTO",
      "contenidoTexto": "¡50% de descuento!",
      "duracionSegundos": 10,
      "orden": 1,
      "prioridad": "ALTA"
    },
    {
      "id": 2,
      "titulo": "Banner Navidad",
      "tipo": "IMAGEN",
      "urlArchivo": "http://localhost:8081/uploads/abc123.jpg",
      "duracionSegundos": 10,
      "orden": 2,
      "prioridad": "NORMAL"
    }
  ]
}
```

---

### 4.2 Registrar Reproducción
**Endpoint:** `POST /api/v1/raspberry/reproduccion/{contenidoId}`
**Acceso:** Público

**Query Params:**
- `codigoIdentificacion`: Código de la pantalla

**Ejemplo:**
```
POST /api/v1/raspberry/reproduccion/1?codigoIdentificacion=PI-A1B2C3D4
```

---

### 4.3 Heartbeat (Mantener Conexión)
**Endpoint:** `POST /api/v1/raspberry/heartbeat/{codigoIdentificacion}`
**Acceso:** Público

**Query Params (opcionales):**
- `direccionIp`
- `versionSoftware`
- `informacionSistema`

**Recomendación:** Llamar cada 2-3 minutos

---

### 4.4 Health Check
**Endpoint:** `GET /api/v1/raspberry/health`
**Acceso:** Público

**Response:**
```json
{
  "exitoso": true,
  "mensaje": "Servidor funcionando correctamente",
  "datos": {
    "status": "UP",
    "service": "InnoAd Backend",
    "version": "2.0.0"
  }
}
```

---

### 4.5 Obtener Configuración de Pantalla
**Endpoint:** `GET /api/v1/raspberry/configuracion/{codigoIdentificacion}`
**Acceso:** Público

**Response:**
```json
{
  "exitoso": true,
  "mensaje": "Configuración obtenida",
  "datos": {
    "nombre": "Pantalla Principal - Recepción",
    "resolucion": "1920x1080",
    "orientacion": "HORIZONTAL",
    "estado": "ACTIVA",
    "ubicacion": "Recepción - Edificio A - Piso 1"
  }
}
```

---

## 🔧 5. CONFIGURACIÓN DEL FRONTEND

### Variables de Entorno Recomendadas

```typescript
// src/environments/environment.ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8081/api/v1',
  uploadsUrl: 'http://localhost:8081/uploads'
};

// src/environments/environment.prod.ts
export const environment = {
  production: true,
  apiUrl: 'https://api.innoad.com/api/v1',
  uploadsUrl: 'https://api.innoad.com/uploads'
};
```

### Servicio HTTP Base (Angular)

```typescript
// src/app/core/services/api.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('access_token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': token ? `Bearer ${token}` : ''
    });
  }

  get<T>(endpoint: string): Observable<T> {
    return this.http.get<T>(`${this.apiUrl}${endpoint}`, {
      headers: this.getHeaders()
    });
  }

  post<T>(endpoint: string, data: any): Observable<T> {
    return this.http.post<T>(`${this.apiUrl}${endpoint}`, data, {
      headers: this.getHeaders()
    });
  }

  put<T>(endpoint: string, data: any): Observable<T> {
    return this.http.put<T>(`${this.apiUrl}${endpoint}`, data, {
      headers: this.getHeaders()
    });
  }

  delete<T>(endpoint: string): Observable<T> {
    return this.http.delete<T>(`${this.apiUrl}${endpoint}`, {
      headers: this.getHeaders()
    });
  }

  uploadFile<T>(endpoint: string, formData: FormData): Observable<T> {
    const token = localStorage.getItem('access_token');
    const headers = new HttpHeaders({
      'Authorization': token ? `Bearer ${token}` : ''
    });
    // NO establecer Content-Type, el navegador lo hace automáticamente con boundary
    return this.http.post<T>(`${this.apiUrl}${endpoint}`, formData, { headers });
  }
}
```

---

## 📋 6. EJEMPLOS DE USO

### Ejemplo 1: Flujo Completo de Registro y Creación de Contenido

```typescript
// 1. Registrarse
const registroData = {
  nombre: 'Juan',
  apellido: 'Pérez',
  email: 'juan@example.com',
  nombreUsuario: 'juanperez',
  contrasena: 'Password123!',
  empresa: 'Mi Empresa'
};

this.apiService.post('/autenticacion/registrarse', registroData)
  .subscribe(response => {
    // Guardar token
    localStorage.setItem('access_token', response.datos.token);
    console.log('Usuario registrado:', response.datos);
  });

// 2. Crear Pantalla
const pantallaData = {
  nombre: 'Pantalla Principal',
  ubicacion: 'Recepción',
  resolucion: '1920x1080',
  orientacion: 'HORIZONTAL'
};

this.apiService.post('/pantallas', pantallaData)
  .subscribe(response => {
    const codigoPantalla = response.datos.codigoIdentificacion;
    console.log('Código de pantalla:', codigoPantalla);
    // Guardar este código para configurar Raspberry Pi
  });

// 3. Crear Contenido de Texto
const contenidoData = {
  titulo: 'Promoción del Mes',
  tipo: 'TEXTO',
  contenidoTexto: '¡50% de descuento!',
  duracionSegundos: 10,
  pantallaId: 1,
  estado: 'ACTIVO'
};

this.apiService.post('/contenidos', contenidoData)
  .subscribe(response => {
    console.log('Contenido creado:', response.datos);
  });
```

### Ejemplo 2: Subir Imagen

```typescript
onFileSelected(event: any) {
  const file: File = event.target.files[0];

  if (file) {
    const formData = new FormData();
    formData.append('archivo', file);
    formData.append('titulo', 'Mi Banner');
    formData.append('tipo', 'IMAGEN');
    formData.append('duracionSegundos', '10');
    formData.append('pantallaId', '1');
    formData.append('estado', 'ACTIVO');

    this.apiService.uploadFile('/contenidos/con-archivo', formData)
      .subscribe(response => {
        console.log('Imagen subida:', response.datos.urlArchivo);
      });
  }
}
```

---

## 🚀 7. GUÍA DE CONFIGURACIÓN RASPBERRY PI

### Script de Configuración para Raspberry Pi

```bash
#!/bin/bash
# raspberry-pi-setup.sh

# 1. Configurar variables
CODIGO_PANTALLA="PI-A1B2C3D4"  # Reemplazar con el código real
API_URL="http://localhost:8081/api/v1"
SYNC_INTERVAL=60  # Segundos entre sincronizaciones

# 2. Instalar dependencias
sudo apt-get update
sudo apt-get install -y chromium-browser unclutter

# 3. Crear directorio de trabajo
mkdir -p ~/innoad-player
cd ~/innoad-player

# 4. Crear script de sincronización
cat > sync.sh << 'EOF'
#!/bin/bash
while true; do
    # Obtener contenidos del servidor
    curl -s "${API_URL}/raspberry/sincronizar/${CODIGO_PANTALLA}" \
        -o contenidos.json

    # Heartbeat
    curl -s -X POST "${API_URL}/raspberry/heartbeat/${CODIGO_PANTALLA}?direccionIp=$(hostname -I | awk '{print $1}')"

    sleep ${SYNC_INTERVAL}
done
EOF

chmod +x sync.sh

# 5. Iniciar en modo kiosk
chromium-browser --kiosk --noerrdialogs --disable-infobars \
    --disable-session-crashed-bubble \
    file:///home/pi/innoad-player/index.html &

# 6. Ejecutar sincronización en background
./sync.sh &
```

---

## ✅ 8. RESUMEN DE ENDPOINTS

| Método | Endpoint | Autenticación | Descripción |
|--------|----------|---------------|-------------|
| POST | `/api/v1/autenticacion/registrarse` | No | Registro público |
| POST | `/api/v1/autenticacion/iniciar-sesion` | No | Login |
| POST | `/api/v1/autenticacion/recuperar-contrasena` | No | Recuperar contraseña |
| POST | `/api/v1/autenticacion/restablecer-contrasena` | No | Restablecer contraseña |
| POST | `/api/v1/pantallas` | Sí | Crear pantalla |
| GET | `/api/v1/pantallas` | Sí | Listar pantallas |
| GET | `/api/v1/pantallas/{id}` | Sí | Obtener pantalla |
| PUT | `/api/v1/pantallas/{id}` | Sí | Actualizar pantalla |
| DELETE | `/api/v1/pantallas/{id}` | Sí | Eliminar pantalla |
| POST | `/api/v1/pantallas/{id}/activar` | Sí | Activar pantalla |
| POST | `/api/v1/contenidos` | Sí | Crear contenido |
| POST | `/api/v1/contenidos/con-archivo` | Sí | Crear contenido con archivo |
| GET | `/api/v1/contenidos` | Sí | Listar contenidos |
| GET | `/api/v1/contenidos/pantalla/{id}` | Sí | Contenidos por pantalla |
| PUT | `/api/v1/contenidos/{id}` | Sí | Actualizar contenido |
| DELETE | `/api/v1/contenidos/{id}` | Sí | Eliminar contenido |
| PATCH | `/api/v1/contenidos/{id}/estado` | Sí | Cambiar estado |
| GET | `/api/v1/raspberry/sincronizar/{codigo}` | No | Sincronizar (Raspberry) |
| POST | `/api/v1/raspberry/reproduccion/{id}` | No | Registrar reproducción |
| POST | `/api/v1/raspberry/heartbeat/{codigo}` | No | Heartbeat |
| GET | `/api/v1/raspberry/health` | No | Health check |

---

## 🎓 9. NOTAS IMPORTANTES

1. **Autenticación:** Todos los endpoints de pantallas y contenidos requieren el header `Authorization: Bearer <token>`

2. **CORS:** El backend permite requests desde:
   - `http://localhost:8080`
   - `http://localhost:4200`
   - `http://127.0.0.1:8080`

3. **Límites de Archivos:**
   - Imágenes: 10 MB
   - Videos: 100 MB

4. **Expiración de Tokens:**
   - Access Token: 24 horas
   - Refresh Token: 7 días

5. **Estados de Contenido:**
   - `BORRADOR`: No se muestra en pantallas
   - `ACTIVO`: Se muestra en pantallas
   - `PAUSADO`: Temporalmente inactivo
   - `FINALIZADO`: Ya no se muestra

6. **Prioridades:**
   - `BAJA` < `NORMAL` < `ALTA` < `URGENTE`

---

## 📞 10. SOPORTE

Para documentación adicional, visita: `http://localhost:8081/swagger-ui.html`

---

**Fecha de actualización:** 4 de Noviembre de 2025
**Versión del Backend:** 2.0.0
