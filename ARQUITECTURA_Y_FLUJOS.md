# 🏗️ Arquitectura del Sistema InnoAd - Chat e IA

## Diagrama de Arquitectura General

```
┌─────────────────────────────────────────────────────────────────────┐
│                         FRONTEND (Angular)                          │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │ PanelChatComponent    │  AsistenteIAComponent              │  │
│  │ - Enviar mensajes     │  - Consultar IA                    │  │
│  │ - Ver historial       │  - Ver historial                   │  │
│  │ - Notificaciones      │  - Estadísticas                    │  │
│  └──────────────────────────────────────────────────────────────┘  │
└─────────────┬──────────────────────────────────────────────────────┘
              │ HTTP/REST + JWT Token
              ↓
┌─────────────────────────────────────────────────────────────────────┐
│                    BACKEND (Spring Boot 3.5.7)                      │
│                                                                      │
│  ┌────────────────────────┐        ┌───────────────────────────┐  │
│  │   Controladores REST   │        │   Seguridad (JWT/RBAC)    │  │
│  │ ┌──────────────────┐   │        │ ┌─────────────────────┐   │  │
│  │ │ ControladorChat  │   │        │ │ @PreAuthorize      │   │  │
│  │ │ - 11 endpoints   │   │        │ │ - Role validation  │   │  │
│  │ └──────────────────┘   │        │ │ - Token validation │   │  │
│  │ ┌──────────────────┐   │        │ └─────────────────────┘   │  │
│  │ │ ControladorIA    │   │        └───────────────────────────┘  │
│  │ │ - 7 endpoints    │   │                                        │
│  │ └──────────────────┘   │                                        │
│  └────────────────────────┘                                        │
│              ↓                                                      │
│  ┌────────────────────────┐        ┌───────────────────────────┐  │
│  │   Capa de Servicios    │        │   Validaciones            │  │
│  │ ┌──────────────────┐   │        │ ┌─────────────────────┐   │  │
│  │ │ ServicioChat     │   │        │ │ @Valid              │   │  │
│  │ │ - Gestión chats  │   │        │ │ @NotBlank          │   │  │
│  │ │ - Mensajería     │   │        │ │ Input validation   │   │  │
│  │ │ - Tickets        │   │        │ └─────────────────────┘   │  │
│  │ └──────────────────┘   │        └───────────────────────────┘  │
│  │ ┌──────────────────┐   │                                        │
│  │ │ ServicioIA       │   │        ┌───────────────────────────┐  │
│  │ │ - Consultas      │   │        │  Configuración Externas   │  │
│  │ │ - Historial      │   │        │ ┌─────────────────────┐   │  │
│  │ │ - Prompts        │   │        │ │ PropiedadesOpenAI  │   │  │
│  │ └──────────────────┘   │        │ │ PropiedadesEmail   │   │  │
│  │ ┌──────────────────┐   │        │ └─────────────────────┘   │  │
│  │ │ ServicioOpenAI   │   │        └───────────────────────────┘  │
│  │ │ - Llamadas a GPT │   │                                        │
│  │ │ - Reintentos     │   │                                        │
│  │ └──────────────────┘   │                                        │
│  │ ┌──────────────────┐   │                                        │
│  │ │ ServicioEmailIA  │   │                                        │
│  │ │ - Enviar emails  │   │                                        │
│  │ │ - Reintentos     │   │                                        │
│  │ └──────────────────┘   │                                        │
│  └────────────────────────┘                                        │
│              ↓                                                      │
│  ┌────────────────────────┐                                        │
│  │  Capa de Repositorios  │                                        │
│  │ ┌──────────────────┐   │                                        │
│  │ │ RepositorioChatUsuario          │ @Query custom methods    │  │
│  │ │ RepositorioMensajeChat          │ @Repository annotated    │  │
│  │ │ RepositorioSolicitudChatTecnico │                         │  │
│  │ │ RepositorioPromptIAPorRol       │                         │  │
│  │ │ RepositorioHorarioAtencion      │                         │  │
│  │ │ RepositorioInfoSistemaInnoAd    │                         │  │
│  │ │ RepositorioEmailConfigurado     │                         │  │
│  │ │ RepositorioRegistroEmailIA      │                         │  │
│  │ │ RepositorioRegistroInteraccionIA│                         │  │
│  │ └──────────────────┘   │                                        │
│  └────────────────────────┘                                        │
│              ↓                                                      │
│  ┌────────────────────────┐        ┌───────────────────────────┐  │
│  │  Entidades JPA (ORM)   │        │   DTOs (Transfer Objects) │  │
│  │ ┌──────────────────┐   │        │ ┌─────────────────────┐   │  │
│  │ │ ChatUsuario      │   │        │ │ DTOChatUsuario      │   │  │
│  │ │ MensajeChat      │   │        │ │ DTOMensajeChat      │   │  │
│  │ │ SolicitudChat    │   │        │ │ DTOPromptIAPorRol   │   │  │
│  │ │ PromptIAPorRol   │   │        │ │ DTORegistroIA       │   │  │
│  │ │ HorarioAtencion  │   │        │ │ DTORespuestaChat    │   │  │
│  │ │ (y 4 más...)     │   │        │ │ DTORespuestaIA      │   │  │
│  │ └──────────────────┘   │        │ └─────────────────────┘   │  │
│  └────────────────────────┘        └───────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────┘
              ↓
         Hibernate/JPA
              ↓
┌─────────────────────────────────────────────────────────────────────┐
│                   BASE DE DATOS (PostgreSQL)                        │
│                                                                      │
│  Chat Tables              │    IA Tables        │  Shared Tables   │
│  ├─ chat_usuario          │    ├─ prompt_ia... │  ├─ usuario      │
│  ├─ mensaje_chat          │    ├─ horario...   │  ├─ rol          │
│  └─ solicitud_chat_tecnico│    ├─ info_sistema │  └─ configuracion│
│                           │    ├─ email_config │                  │
│                           │    ├─ registro_email                  │
│                           │    └─ registro_interaccion            │
└─────────────────────────────────────────────────────────────────────┘
              ↓
┌──────────────────────────────────────────────────────────────────────┐
│            Servicios Externos (API Integrations)                     │
│  ┌──────────────────────────┐      ┌──────────────────────────────┐ │
│  │   OpenAI API (gpt-4)     │      │  Email SMTP (Gmail, etc)     │ │
│  │ - Consultas de IA        │      │ - Envío de emails            │ │
│  │ - Reintentos automáticos │      │ - Auditoría de envíos        │ │
│  │ - Logging de tokens      │      │ - Reintentos automáticos     │ │
│  └──────────────────────────┘      └──────────────────────────────┘ │
└──────────────────────────────────────────────────────────────────────┘
```

---

## Flujos de Negocio

### 1. Flujo de Chat (Mensajería)

```
Usuario Solicitante                    Usuario Técnico
        │                                    │
        │  1. POST /api/chat/crear           │
        ├───────────────────────────────────→│
        │  (crear o obtener chat existente)  │
        │                                    │
        │←───────────────────────────────────┤
        │  200 OK: {chatId: 1, ...}          │
        │                                    │
        │  2. POST /api/chat/1/mensaje       │
        ├───────────────────────────────────→│ (WebSocket notify)
        │  {contenido: "Hola"}               │
        │                                    │
        │←───────────────────────────────────┤
        │  201 Created: {msgId: 1, ...}      │
        │                                    │
        │  3. GET /api/chat/1/mensajes       │
        ├───────────────────────────────────→│
        │  (para obtener historial)          │
        │                                    │
        │←───────────────────────────────────┤
        │  200 OK: {content: [...], ...}     │
        │                                    │
        │  4. PUT /api/chat/1/marcar-leidos  │
        ├───────────────────────────────────→│
        │  (cuando lee los mensajes)         │
        │                                    │
        │←───────────────────────────────────┤
        │  204 No Content                    │
        │                                    │
        │  5. PUT /api/chat/1/cerrar         │
        ├───────────────────────────────────→│
        │  (cuando termina la conversación)  │
        │                                    │
        │←───────────────────────────────────┤
        │  204 No Content                    │
```

### 2. Flujo de Solicitud de Soporte

```
Usuario                          Admin/Técnico
    │                                  │
    │  1. POST /api/chat/solicitud     │
    ├─────────────────────────────────→│
    │  {descripción: "..."}            │
    │                                  │
    │←─────────────────────────────────┤
    │  201 Created: {solicitud_id: 1}  │
    │                                  │
    │                    GET /api/chat/solicitudes/pendientes
    │                    ←──────────────────────────────
    │                    200 OK: [solicitud1, ...]
    │                    
    │                    2. PUT /api/chat/solicitud/1/asignar
    │                    ──────────────────────────────→
    │                    {idTecnicoAsignado: 5}
    │                    
    │                    ←──────────────────────────────
    │                    204 No Content
    │
    │                    3. PUT /api/chat/solicitud/1/estado
    │                    ──────────────────────────────→
    │                    {nuevoEstado: "EN_PROGRESO"}
    │                    
    │                    (Crear chat automático si es necesario)
    │
    └────────────────────── (Continuar con flujo de Chat)
```

### 3. Flujo de Consulta a IA

```
Usuario                          Backend                    OpenAI API
    │                                │                          │
    │ 1. POST /api/ia/consultar      │                          │
    ├───────────────────────────────→│                          │
    │ {idUsuario: 1,                 │                          │
    │  pregunta: "¿Cómo...?"}        │                          │
    │                                │ 2. Obtener prompt        │
    │                                │    por rol del usuario   │
    │                                │    (BD)                  │
    │                                │                          │
    │                                │ 3. Verificar horario     │
    │                                │    de atención (BD)      │
    │                                │                          │
    │                                │ 4. Registrar           │
    │                                │    interacción (BD)    │
    │                                │                          │
    │                                │ 5. Construir contexto    │
    │                                │    (prompt + info)       │
    │                                │                          │
    │                                │ 6. Llamar OpenAI         │
    │                                ├─────────────────────────→│
    │                                │ POST /v1/chat/completions│
    │                                │                          │
    │                                │←─────────────────────────┤
    │                                │ {choices: [{message...}]}│
    │                                │                          │
    │                                │ 7. Actualizar           │
    │                                │    interacción con      │
    │                                │    respuesta y métricas  │
    │                                │    (BD)                  │
    │                                │                          │
    │←───────────────────────────────┤                          │
    │ 201 Created:                   │                          │
    │ {respuesta: "...",             │                          │
    │  tokensUtilizados: 342,        │                          │
    │  tiempoRespuesta: 3.2}         │                          │
```

### 4. Flujo de Envío de Email desde IA

```
ServicioIA                    ServicioEmailIA                SMTP Server
       │                            │                            │
       │ 1. enviarEmail()           │                            │
       ├───────────────────────────→│                            │
       │ (idUsuario, destinatario,  │                            │
       │  asunto, contenido)        │                            │
       │                            │                            │
       │                            │ 2. Obtener config SMTP     │
       │                            │    activa (BD)             │
       │                            │                            │
       │                            │ 3. Registrar email         │
       │                            │    en auditoría (BD)       │
       │                            │    con estado PENDIENTE    │
       │                            │                            │
       │                            │ 4. Crear conexión SMTP     │
       │                            ├───────────────────────────→│
       │                            │ MAIL FROM, RCPT TO         │
       │                            │                            │
       │                            │←───────────────────────────┤
       │                            │ 250 OK                     │
       │                            │                            │
       │                            │ 5. Enviar contenido        │
       │                            ├───────────────────────────→│
       │                            │ DATA: message content      │
       │                            │                            │
       │                            │←───────────────────────────┤
       │                            │ 250 OK                     │
       │                            │                            │
       │                            │ 6. Actualizar auditoría    │
       │                            │    con estado ENVIADO      │
       │                            │    (BD)                    │
       │                            │                            │
       │←───────────────────────────┤                            │
       │ 200 OK: {registroId: 5}    │                            │
       │                            │                            │
       │ (Si falla, reintentar      │                            │
       │  hasta 3 veces con delay)  │                            │
```

---

## Mapeo de Entidades y Relaciones

```
usuario (tabla compartida)
  ├─→ (1:N) chat_usuario.id_usuario_tecnico
  ├─→ (1:N) chat_usuario.id_usuario_solicitante
  ├─→ (1:N) mensaje_chat.id_usuario_remitente
  ├─→ (1:N) solicitud_chat_tecnico.id_usuario
  ├─→ (1:N) solicitud_chat_tecnico.id_tecnico_asignado
  ├─→ (1:N) registro_interaccion_ia.id_usuario
  ├─→ (1:N) registro_email_ia.id_usuario
  └─→ (1:N) prompt_ia_por_rol.id_usuario_creador

chat_usuario
  └─→ (1:N) mensaje_chat.id_chat_usuario

solicitud_chat_tecnico
  (no tiene relaciones directas a otras tablas nuevas)

prompt_ia_por_rol
  └─ (auditoría de quién la creó)

horario_atencion
  └─ (datos estáticos, sin FK)

info_sistema_innoad
  └─ (datos de configuración, sin FK)

email_configurado
  └─ (datos de configuración, sin FK)

registro_email_ia
  └─ (auditoría, sin FK a email_configurado)

registro_interaccion_ia
  └─ (auditoría, sin FK a prompt_ia_por_rol)
```

---

## Flujo de Autenticación y Autorización

```
1. Login
   POST /api/auth/login
   {email, contrasenia}
   ↓
   [Validar credenciales]
   [Generar JWT token]
   ↓
   Response: {token, tipoToken: "Bearer", expiracionMs: 86400000}

2. Cada Request Posterior
   Header: Authorization: Bearer <jwt_token>
   ↓
   [Interceptor valida token]
   [Extrae claims: userId, rol, permisos]
   ↓
   @PreAuthorize("hasRole('TECNICO')")
   ↓
   [Si autorizado → procesar request]
   [Si no autorizado → 403 Forbidden]

3. Flujo de Autorización por Rol

   ROLE_ADMIN
   ├─ GET /api/chat/solicitudes/pendientes ✓
   ├─ PUT /api/chat/solicitud/{id}/asignar ✓
   ├─ PUT /api/chat/solicitud/{id}/estado ✓
   ├─ GET /api/ia/prompts ✓
   ├─ POST /api/ia/prompts ✓
   └─ PUT /api/ia/prompts/{id} ✓

   ROLE_TECNICO
   ├─ POST /api/chat/{id}/mensaje ✓
   ├─ GET /api/chat/{id}/mensajes ✓
   ├─ GET /api/chat/tecnico/{id} ✓
   ├─ PUT /api/chat/solicitud/{id}/asignar ✓
   ├─ PUT /api/chat/solicitud/{id}/estado ✓
   └─ POST /api/ia/consultar ✓

   ROLE_USUARIO
   ├─ POST /api/chat/{id}/mensaje ✓
   ├─ GET /api/chat/{id}/mensajes ✓
   ├─ GET /api/chat/usuario/{id} ✓
   ├─ POST /api/chat/solicitud ✓
   └─ POST /api/ia/consultar ✓

   ROLE_DEVELOPER
   └─ Acceso a todos los endpoints ✓
```

---

## Capa de Persistencia (Database Layer)

### Índices para Optimización

```sql
-- Chat Indexes
idx_chat_usuario_id_tecnico
  → WHERE usuario_tecnico = ?
  → SELECT chats para técnico específico

idx_chat_usuario_id_solicitante
  → WHERE usuario_solicitante = ?
  → SELECT chats para solicitante

idx_chat_usuario_activo
  → WHERE activo = true
  → Filtrar chats cerrados rápidamente

idx_chat_usuario_fecha_actualizacion
  → ORDER BY fecha_actualizacion DESC
  → Ordenar chats recientes primero

idx_mensaje_chat_id_chat
  → WHERE id_chat = ?
  → Obtener mensajes de un chat

idx_mensaje_chat_leido
  → WHERE leido = false
  → Contar no leídos rápidamente

-- IA Indexes
idx_prompt_rol
  → WHERE rol = 'ROLE_ADMIN'
  → Obtener prompt por rol

idx_interaccion_usuario
  → WHERE id_usuario = ?
  → Historial de usuario

idx_email_ia_estado
  → WHERE estado = 'PENDIENTE'
  → Obtener emails no enviados para reintentos
```

### Vistas para Reporting

```sql
vw_chats_activos
  SELECT c.id, nombre_tecnico, nombre_solicitante,
         mensajes_no_leidos, fecha_ultimo_mensaje
  FROM chat_usuario c
  WHERE activo = true

vw_solicitudes_pendientes
  SELECT s.id, usuario, descripcion, estado,
         dias_transcurridos, nombre_tecnico_asignado
  FROM solicitud_chat_tecnico s
  WHERE estado IN ('PENDIENTE', 'ASIGNADA', 'EN_PROGRESO')

vw_estadisticas_ia_usuarios
  SELECT u.id, u.nombre, total_interacciones,
         interacciones_completadas, total_tokens,
         tiempo_promedio, ultima_interaccion
  FROM registro_interaccion_ia ria
  GROUP BY u.id
```

### Procedimientos para Operaciones Complejas

```sql
sp_obtener_ultimo_mensaje(p_id_chat)
  → Obtener último mensaje de un chat

sp_contar_mensajes_no_leidos(p_id_chat, OUT p_cantidad)
  → Contar mensajes sin leer

sp_estadisticas_usuario_ia(p_id_usuario)
  → Agregación de estadísticas de IA

sp_marcar_chat_como_leido(p_id_chat)
  → Bulk update de mensajes

sp_obtener_chats_usuario(p_id_usuario)
  → JOIN con conteo de no leídos
```

---

## Configuración de Ciclo de Vida (Beans y Lifecycle)

```java
// 1. Inicialización
@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
public class InnoAdApplication {
    public static void main(String[] args) {
        SpringApplication.run(InnoAdApplication.class, args);
    }
}

// 2. Carga de Configuraciones
@Configuration
public class ConfiguracionRestTemplate {
    @Bean
    public RestTemplate restTemplateOpenAI() { ... }
}

// 3. Validación de Propiedades
@Bean
public PropiedadesOpenAI propiedadesOpenAI() {
    PropiedadesOpenAI props = new PropiedadesOpenAI();
    props.validar(); // Lanza excepción si config inválida
    return props;
}

// 4. Inyección en Servicios
@Service
public class ServicioOpenAI {
    @Autowired
    private PropiedadesOpenAI propiedadesOpenAI;
    
    // Ya configurado y validado
}

// 5. Transacciones
@Service
@Transactional
public class ServicioChat {
    // Todas las operaciones BD envueltas en transacción
    // Rollback automático en excepciones
}
```

---

## Manejo de Errores y Recuperación

```
Request → Controller
    ↓
    @PreAuthorize (Security Check)
    ├─ Si no autorizado → 403 Forbidden
    ↓
    @Valid (Input Validation)
    ├─ Si inválido → 400 Bad Request
    ↓
    Service Layer
    ├─ Lógica de negocio
    ├─ Si error → Lanzar CustomException
    ↓
    Repository Layer
    ├─ Si recurso no encontrado → Optional.empty()
    ├─ Si DB error → DataIntegrityViolationException
    ↓
    @ExceptionHandler (Global Exception Handler)
    ├─ CustomException → 400/409 con mensaje
    ├─ EntityNotFoundException → 404 Not Found
    ├─ AccessDeniedException → 403 Forbidden
    ├─ Exception genérica → 500 Internal Server Error
    ↓
    Response: {
        status: HTTP_CODE,
        mensaje: "Descripción del error",
        timestamp: "2025-12-09T10:45:00",
        path: "/api/chat/1"
    }
```

---

## Estrategia de Caching (Opcional)

```
Request
    ↓
Redis Cache
├─ Si hay → Devolver cached response
└─ Si no hay → Continuar
    ↓
Database Query
    ↓
Cache con TTL:
├─ Prompts de IA: 24 horas
├─ Horarios de atención: 7 días
├─ Info del sistema: 30 días
├─ Chat mensajes: 1 hora
└─ Estadísticas: 15 minutos
```

---

## Escala y Performance

### Connection Pooling
```yaml
hikari:
  maximum-pool-size: 100
  minimum-idle: 20
  connection-timeout: 30000ms
  idle-timeout: 600000ms (10 min)
```

### Query Optimization
```sql
-- ✓ Con índices
SELECT * FROM chat_usuario WHERE id_usuario_tecnico = 1
  → 1-10 ms

-- ✗ Sin índices
SELECT * FROM chat_usuario WHERE fecha_creacion = '2025-12-09'
  → 100-500 ms (table scan)
```

### Rate Limiting (Futuro)
```
Por usuario:
├─ 100 requests/minuto
├─ 5 consultas IA/minuto
└─ 20 emails/hora

Por IP:
└─ 1000 requests/minuto
```

---

**Documento creado:** 9 de Diciembre de 2025  
**Versión:** 2.0.0
