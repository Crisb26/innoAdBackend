# 📡 API REST - InnoAd Chat e IA
## Especificación de Endpoints y Ejemplos

---

## 🔐 Autenticación

Todos los endpoints requieren un token JWT en el header:

```
Authorization: Bearer <tu_token_jwt>
```

**Cómo obtener el token:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "usuario@example.com",
    "contrasenia": "tu_password"
  }'
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipoToken": "Bearer",
  "expiracionMs": 86400000
}
```

---

## 💬 API de Chat

### 1. Crear o Obtener Chat
**Endpoint:** `POST /api/chat/crear`

**Descripción:** Crea un nuevo chat entre un técnico y un solicitante, o devuelve el existente.

**Headers:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Body:**
```json
{
  "idUsuarioTecnico": 1,
  "idUsuarioSolicitante": 2
}
```

**Response (201):**
```json
{
  "id": 1,
  "idUsuarioTecnico": 1,
  "idUsuarioSolicitante": 2,
  "activo": true,
  "fechaCreacion": "2025-12-09T10:30:00",
  "fechaCierre": null,
  "fechaActualizacion": "2025-12-09T10:30:00"
}
```

**Errores:**
- `400`: Usuario técnico o solicitante no válido
- `401`: No autenticado
- `403`: Permisos insuficientes

---

### 2. Enviar Mensaje
**Endpoint:** `POST /api/chat/{idChat}/mensaje`

**Descripción:** Envía un nuevo mensaje en un chat activo.

**Parameters:**
- `idChat` (path, required): ID del chat

**Body:**
```json
{
  "idUsuarioRemitente": 1,
  "contenido": "Hola, ¿cómo estás?"
}
```

**Response (201):**
```json
{
  "id": 1,
  "idChatUsuario": 1,
  "idUsuarioRemitente": 1,
  "contenido": "Hola, ¿cómo estás?",
  "fechaCreacion": "2025-12-09T10:35:00",
  "leido": false,
  "fechaLectura": null
}
```

**Errores:**
- `404`: Chat no encontrado o no está activo
- `400`: Mensaje vacío o muy largo
- `401`: No autenticado

---

### 3. Obtener Mensajes de un Chat
**Endpoint:** `GET /api/chat/{idChat}/mensajes`

**Descripción:** Obtiene todos los mensajes de un chat con paginación.

**Parameters:**
- `idChat` (path, required): ID del chat
- `page` (query, optional): Número de página (default: 0)
- `size` (query, optional): Mensajes por página (default: 20)

**Query Example:**
```
GET /api/chat/1/mensajes?page=0&size=10
```

**Response (200):**
```json
{
  "content": [
    {
      "id": 1,
      "idChatUsuario": 1,
      "idUsuarioRemitente": 1,
      "contenido": "Hola",
      "fechaCreacion": "2025-12-09T10:35:00",
      "leido": true,
      "fechaLectura": "2025-12-09T10:36:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "empty": false,
      "sorted": true
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 25,
  "totalPages": 3,
  "last": false,
  "size": 10,
  "number": 0,
  "sort": {
    "empty": false,
    "sorted": true
  },
  "numberOfElements": 10,
  "first": true,
  "empty": false
}
```

---

### 4. Marcar Mensajes como Leídos
**Endpoint:** `PUT /api/chat/{idChat}/marcar-leidos`

**Descripción:** Marca todos los mensajes no leídos de un chat como leídos.

**Parameters:**
- `idChat` (path, required): ID del chat

**Response (204):** Sin contenido

---

### 5. Cerrar Chat
**Endpoint:** `PUT /api/chat/{idChat}/cerrar`

**Descripción:** Cierra un chat (marca como inactivo).

**Parameters:**
- `idChat` (path, required): ID del chat

**Response (204):** Sin contenido

---

### 6. Obtener Chats por Usuario
**Endpoint:** `GET /api/chat/usuario/{idUsuario}`

**Descripción:** Obtiene todos los chats activos de un usuario.

**Parameters:**
- `idUsuario` (path, required): ID del usuario

**Response (200):**
```json
[
  {
    "id": 1,
    "idUsuarioTecnico": 1,
    "idUsuarioSolicitante": 2,
    "activo": true,
    "fechaCreacion": "2025-12-09T10:30:00",
    "mensajesNoLeidos": 3
  }
]
```

---

### 7. Obtener Chats por Técnico
**Endpoint:** `GET /api/chat/tecnico/{idTecnico}`

**Descripción:** Obtiene todos los chats asignados a un técnico.

**Parameters:**
- `idTecnico` (path, required): ID del técnico

---

### 8. Crear Solicitud de Chat Técnico
**Endpoint:** `POST /api/chat/solicitud`

**Descripción:** Crea una nueva solicitud de soporte técnico.

**Body:**
```json
{
  "idUsuario": 2,
  "descripcion": "No puedo acceder a mi cuenta"
}
```

**Response (201):**
```json
{
  "id": 1,
  "idUsuario": 2,
  "descripcion": "No puedo acceder a mi cuenta",
  "estado": "PENDIENTE",
  "fechaCreacion": "2025-12-09T10:40:00",
  "fechaAsignacion": null,
  "idTecnicoAsignado": null,
  "fechaActualizacion": "2025-12-09T10:40:00"
}
```

**Estados disponibles:** PENDIENTE, ASIGNADA, EN_PROGRESO, RESUELTA, CANCELADA

---

### 9. Asignar Solicitud a Técnico
**Endpoint:** `PUT /api/chat/solicitud/{idSolicitud}/asignar`

**Descripción:** Asigna una solicitud pendiente a un técnico.

**Parameters:**
- `idSolicitud` (path, required): ID de la solicitud
- `idTecnicoAsignado` (query, required): ID del técnico

**Query Example:**
```
PUT /api/chat/solicitud/1/asignar?idTecnicoAsignado=5
```

**Response (204):** Sin contenido

---

### 10. Cambiar Estado de Solicitud
**Endpoint:** `PUT /api/chat/solicitud/{idSolicitud}/estado`

**Descripción:** Cambia el estado de una solicitud.

**Parameters:**
- `idSolicitud` (path, required): ID de la solicitud

**Body:**
```json
{
  "nuevoEstado": "EN_PROGRESO"
}
```

**Response (204):** Sin contenido

---

### 11. Obtener Solicitudes Pendientes
**Endpoint:** `GET /api/chat/solicitudes/pendientes`

**Descripción:** Obtiene todas las solicitudes sin asignar (paginado).

**Query:**
```
GET /api/chat/solicitudes/pendientes?page=0&size=20
```

**Response (200):** Lista paginada de solicitudes

---

## 🤖 API de IA

### 1. Consultar IA
**Endpoint:** `POST /api/ia/consultar`

**Descripción:** Envía una pregunta a la IA y obtiene una respuesta inteligente basada en el rol del usuario.

**Body:**
```json
{
  "idUsuario": 1,
  "pregunta": "¿Cómo creo una nueva campaña?"
}
```

**Response (201):**
```json
{
  "id": 1,
  "idUsuario": 1,
  "pregunta": "¿Cómo creo una nueva campaña?",
  "respuesta": "Para crear una nueva campaña debes... [respuesta generada por IA]",
  "estado": "COMPLETADA",
  "tokensUtilizados": 342,
  "tiempoRespuesta": 3.2,
  "mensajeError": null,
  "fechaCreacion": "2025-12-09T10:45:00",
  "fechaCompletacion": "2025-12-09T10:45:03"
}
```

**Errores:**
- `400`: Pregunta vacía
- `503`: Servicio de IA no disponible (fuera de horario)
- `429`: Límite de rate limiting alcanzado

---

### 2. Obtener Historial de IA
**Endpoint:** `GET /api/ia/historial/{idUsuario}`

**Descripción:** Obtiene el historial de interacciones de un usuario con la IA.

**Parameters:**
- `idUsuario` (path, required): ID del usuario
- `page` (query, optional): Número de página (default: 0)
- `size` (query, optional): Registros por página (default: 20)

**Response (200):**
```json
{
  "content": [
    {
      "id": 1,
      "idUsuario": 1,
      "pregunta": "¿Cómo creo una campaña?",
      "respuesta": "...",
      "estado": "COMPLETADA",
      "tokensUtilizados": 342,
      "tiempoRespuesta": 3.2,
      "fechaCreacion": "2025-12-09T10:45:00"
    }
  ],
  "totalElements": 15,
  "totalPages": 1,
  "number": 0,
  "size": 20
}
```

---

### 3. Obtener Estadísticas de IA
**Endpoint:** `GET /api/ia/estadisticas/{idUsuario}`

**Descripción:** Obtiene estadísticas de uso de la IA del usuario.

**Parameters:**
- `idUsuario` (path, required): ID del usuario

**Response (200):**
```json
{
  "idUsuario": 1,
  "consultasRealizadas": 15,
  "consultasCompletadas": 14,
  "consultasFallidas": 1,
  "tokensUsadosHoy": 5234,
  "tokensUsadosTotalMes": 25000,
  "tiempoPromedio": 2.8,
  "ultimaConsulta": "2025-12-09T10:45:00"
}
```

---

### 4. Obtener Prompts
**Endpoint:** `GET /api/ia/prompts`

**Descripción:** Obtiene todos los prompts activos. **Requiere ADMIN o DEVELOPER**.

**Response (200):**
```json
[
  {
    "id": 1,
    "rol": "ROLE_ADMIN",
    "instruccion": "Eres un asistente administrativo...",
    "contexto": "Sistema de gestión...",
    "tokenMaximo": 2000,
    "temperatura": 0.7,
    "activo": true,
    "fechaCreacion": "2025-12-01T08:00:00"
  }
]
```

---

### 5. Crear Prompt
**Endpoint:** `POST /api/ia/prompts`

**Descripción:** Crea un nuevo prompt. **Requiere ADMIN**.

**Body:**
```json
{
  "rol": "ROLE_USUARIO",
  "instruccion": "Eres un asistente amigable para usuarios normales",
  "contexto": "Sistema de publicidad digital InnoAd",
  "tokenMaximo": 2000,
  "temperatura": 0.6
}
```

**Response (201):**
```json
{
  "id": 5,
  "rol": "ROLE_USUARIO",
  "instruccion": "Eres un asistente amigable...",
  "contexto": "Sistema de publicidad digital...",
  "tokenMaximo": 2000,
  "temperatura": 0.6,
  "activo": true,
  "fechaCreacion": "2025-12-09T10:50:00"
}
```

---

### 6. Actualizar Prompt
**Endpoint:** `PUT /api/ia/prompts/{idPrompt}`

**Descripción:** Actualiza un prompt existente. **Requiere ADMIN**.

**Parameters:**
- `idPrompt` (path, required): ID del prompt

**Body:**
```json
{
  "instruccion": "Nueva instrucción actualizada",
  "temperatura": 0.8,
  "tokenMaximo": 2500
}
```

**Response (204):** Sin contenido

---

### 7. Verificar Disponibilidad
**Endpoint:** `GET /api/ia/horario/disponible`

**Descripción:** Verifica si la IA está disponible según los horarios de atención.

**Response (200):**
```json
{
  "disponible": true,
  "horarioActual": "LUNES 14:30",
  "proximaDisponibilidad": "2025-12-09T16:00:00",
  "diaActual": "LUNES",
  "horaInicio": "08:00",
  "horaFin": "18:00"
}
```

---

## 📊 Códigos de Respuesta HTTP

| Código | Descripción |
|--------|-------------|
| 200 | OK - Solicitud exitosa |
| 201 | Created - Recurso creado |
| 204 | No Content - Operación exitosa sin contenido |
| 400 | Bad Request - Datos inválidos |
| 401 | Unauthorized - No autenticado |
| 403 | Forbidden - Permisos insuficientes |
| 404 | Not Found - Recurso no encontrado |
| 409 | Conflict - Conflicto (ej: chat ya existe) |
| 429 | Too Many Requests - Límite de rate limit alcanzado |
| 500 | Internal Server Error - Error del servidor |
| 503 | Service Unavailable - Servicio no disponible (IA fuera de horario) |

---

## 🔒 Control de Acceso (RBAC)

### Chat
- `POST /api/chat/crear` - TECNICO, ADMIN, DEVELOPER
- `POST /api/chat/{id}/mensaje` - USUARIO, TECNICO, ADMIN, DEVELOPER
- `GET /api/chat/{id}/mensajes` - Propietario del chat
- `PUT /api/chat/{id}/marcar-leidos` - Propietario del chat
- `PUT /api/chat/{id}/cerrar` - Propietario o ADMIN
- `GET /api/chat/usuario/{id}` - El usuario mismo
- `GET /api/chat/tecnico/{id}` - El técnico mismo o ADMIN
- `POST /api/chat/solicitud` - USUARIO, ADMIN
- `PUT /api/chat/solicitud/{id}/asignar` - ADMIN, TECNICO
- `PUT /api/chat/solicitud/{id}/estado` - TECNICO, ADMIN
- `GET /api/chat/solicitudes/pendientes` - ADMIN, TECNICO

### IA
- `POST /api/ia/consultar` - USUARIO, TECNICO, ADMIN, DEVELOPER
- `GET /api/ia/historial/{id}` - El usuario mismo o ADMIN
- `GET /api/ia/estadisticas/{id}` - El usuario mismo o ADMIN
- `GET /api/ia/prompts` - ADMIN, DEVELOPER
- `POST /api/ia/prompts` - ADMIN
- `PUT /api/ia/prompts/{id}` - ADMIN
- `GET /api/ia/horario/disponible` - Todos autenticados

---

## 📱 Ejemplos con cURL

### Crear Chat
```bash
curl -X POST http://localhost:8080/api/chat/crear \
  -H "Authorization: Bearer eyJhbGc..." \
  -H "Content-Type: application/json" \
  -d '{
    "idUsuarioTecnico": 1,
    "idUsuarioSolicitante": 2
  }'
```

### Enviar Mensaje
```bash
curl -X POST http://localhost:8080/api/chat/1/mensaje \
  -H "Authorization: Bearer eyJhbGc..." \
  -H "Content-Type: application/json" \
  -d '{
    "idUsuarioRemitente": 1,
    "contenido": "Hola"
  }'
```

### Consultar IA
```bash
curl -X POST http://localhost:8080/api/ia/consultar \
  -H "Authorization: Bearer eyJhbGc..." \
  -H "Content-Type: application/json" \
  -d '{
    "idUsuario": 1,
    "pregunta": "¿Cómo creo una campaña?"
  }'
```

### Obtener Chats
```bash
curl -X GET "http://localhost:8080/api/chat/usuario/1?page=0&size=10" \
  -H "Authorization: Bearer eyJhbGc..."
```

---

## 🚀 Importar en Postman

1. Descargar `InnoAd-Chat-IA-API.postman_collection.json`
2. Abrir Postman
3. Ir a: File → Import
4. Seleccionar el archivo JSON
5. Las variables {{baseUrl}} y {{jwtToken}} se autocompletan

---

**Última actualización:** Diciembre 2025
**Versión API:** 2.0.0
