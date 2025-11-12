# Guía de Migración a Arquitectura Modular

## Objetivo

Reorganizar el código actual en módulos independientes para facilitar el mantenimiento y permitir una futura migración a microservicios si es necesario.

## Estructura Creada

```
com.innoad/
├── InnoAdApplication.java          (Mantener aquí)
├── modules/                         (NUEVO)
│   ├── auth/                       (Autenticación)
│   │   ├── domain/                 → Mover: Usuario, Rol, Permiso, Sesion, TokenVerificacion
│   │   ├── service/                → Mover: ServicioAutenticacion, ServicioJWT, ServicioDetallesUsuario
│   │   ├── repository/             → Mover: RepositorioUsuario, RepositorioTokenVerificacion
│   │   ├── controller/             → Mover: ControladorAutenticacion
│   │   └── dto/                    → Mover: DTOs de autenticación
│   │
│   ├── campaigns/                  (Campañas)
│   │   ├── domain/                 → Crear entidades cuando existan
│   │   ├── service/                → Lógica de campañas
│   │   ├── repository/             → Repositorios de campañas
│   │   ├── controller/             → Endpoints de campañas
│   │   └── dto/                    → DTOs de campañas
│   │
│   ├── content/                    (Contenidos)
│   │   ├── domain/                 → Mover: Contenido, Publicidad
│   │   ├── service/                → Mover: ServicioContenido, ServicioAlmacenamiento
│   │   ├── repository/             → Mover: RepositorioContenido, RepositorioPublicidad
│   │   ├── controller/             → Mover: ControladorContenido
│   │   └── dto/                    → Mover: DTOs de contenido
│   │
│   ├── screens/                    (Pantallas y dispositivos)
│   │   ├── domain/                 → Mover: Pantalla
│   │   ├── service/                → Mover: ServicioPantalla
│   │   ├── repository/             → Mover: RepositorioPantalla
│   │   ├── controller/             → Mover: ControladorPantalla, ControladorRaspberryPi
│   │   └── dto/                    → Mover: DTOs de pantallas
│   │
│   ├── stats/                      (Estadísticas)
│   │   ├── domain/                 → Crear cuando existan entidades
│   │   ├── service/                → Servicios de estadísticas
│   │   ├── repository/             → Repositorios de estadísticas
│   │   ├── controller/             → Endpoints de estadísticas
│   │   └── dto/                    → DTOs de estadísticas
│   │
│   ├── ia/                         (Inteligencia Artificial)
│   │   ├── domain/                 → Mover: ConversacionIA
│   │   ├── service/                → Mover: ServicioAgenteIA
│   │   ├── repository/             → Mover: RepositorioConversacionIA
│   │   ├── controller/             → Endpoints de IA
│   │   └── dto/                    → DTOs de IA
│   │
│   └── admin/                      (Administración)
│       ├── domain/                 → Mover: ConfiguracionSistema, Auditoria
│       ├── service/                → Mover: ServicioAdministracion, ServicioModoMantenimiento
│       ├── repository/             → Mover: RepositorioConfiguracionSistema, RepositorioAuditoria
│       ├── controller/             → Mover: ControladorAdministracion
│       └── dto/                    → DTOs de admin
│
└── shared/                         (NUEVO - Código compartido)
    ├── config/                     → Mover: ConfiguracionSeguridad, ConfiguracionWeb, etc.
    ├── exception/                  → Mover: Todas las excepciones
    ├── security/                   → Mover: FiltroAutenticacionJWT
    ├── util/                       → Mover: Utilidades
    └── dto/                        → DTOs compartidos entre módulos
```

## Reglas de la Arquitectura Modular

### ✅ PERMITIDO

1. **Módulo A → Servicio de Módulo B**
   ```java
   // En campaigns/service/ServicioCampana.java
   @Autowired
   private com.innoad.modules.auth.service.ServicioUsuario servicioUsuario;
   
   Usuario usuario = servicioUsuario.obtenerPorId(usuarioId);
   ```

2. **Cualquier módulo → Shared**
   ```java
   import com.innoad.shared.exception.RecursoNoEncontradoException;
   import com.innoad.shared.util.ValidadorUtil;
   ```

3. **Módulo usa sus propios repositorios**
   ```java
   // En campaigns/service/ServicioCampana.java
   @Autowired
   private com.innoad.modules.campaigns.repository.RepositorioCampana repositorio;
   ```

### ❌ PROHIBIDO

1. **Módulo A → Repositorio de Módulo B**
   ```java
   // ❌ MAL - No hacer esto
   @Autowired
   private com.innoad.modules.auth.repository.RepositorioUsuario repoUsuario;
   ```

2. **Módulo A → Domain de Módulo B (excepto como parámetro/retorno de servicio)**
   ```java
   // ❌ MAL - No instanciar directamente
   Usuario usuario = new Usuario();
   
   // ✅ BIEN - Obtener a través del servicio
   Usuario usuario = authService.obtenerUsuario(id);
   ```

3. **Imports circulares**
   ```java
   // ❌ MAL
   // auth importa campaigns Y campaigns importa auth
   ```

## Pasos de Migración

### Fase 1: Mover código compartido (1-2 días)

1. **Mover configuraciones a `shared/config/`**
   ```bash
   mv configuracion/* shared/config/
   ```

2. **Mover excepciones a `shared/exception/`**
   ```bash
   mv excepcion/* shared/exception/
   ```

3. **Mover filtros a `shared/security/`**
   ```bash
   mv filtro/* shared/security/
   ```

4. **Mover utilidades a `shared/util/`**
   ```bash
   mv utilidad/* shared/util/
   ```

5. **Actualizar imports en todos los archivos**

### Fase 2: Módulo Auth (2-3 días)

1. **Mover entidades**
   - `modelo/entidades/Usuario.java` → `modules/auth/domain/Usuario.java`
   - `modelo/entidades/TokenVerificacion.java` → `modules/auth/domain/TokenVerificacion.java`
   - Crear: `Rol.java`, `Permiso.java`, `Sesion.java`

2. **Mover servicios**
   - `ServicioAutenticacion.java` → `modules/auth/service/`
   - `ServicioJWT.java` → `modules/auth/service/`
   - `ServicioDetallesUsuario.java` → `modules/auth/service/`
   - `ServicioVerificacionEmail.java` → `modules/auth/service/`

3. **Mover repositorios**
   - `RepositorioUsuario.java` → `modules/auth/repository/`
   - `RepositorioTokenVerificacion.java` → `modules/auth/repository/`

4. **Mover controlador**
   - `ControladorAutenticacion.java` → `modules/auth/controller/`

5. **Mover DTOs**
   - Todos los DTOs de auth → `modules/auth/dto/`

### Fase 3: Módulo Content (2 días)

1. **Mover entidades**
   - `Contenido.java` → `modules/content/domain/`
   - `Publicidad.java` → `modules/content/domain/`

2. **Mover servicios**
   - `ServicioContenido.java` → `modules/content/service/`
   - `ServicioAlmacenamiento.java` → `modules/content/service/`
   - `ServicioEmail.java` → `modules/content/service/` (o shared)

3. **Mover repositorios**
   - `RepositorioContenido.java` → `modules/content/repository/`
   - `RepositorioPublicidad.java` → `modules/content/repository/`

4. **Mover controlador**
   - `ControladorContenido.java` → `modules/content/controller/`

### Fase 4: Módulo Screens (2 días)

1. **Mover entidades**
   - `Pantalla.java` → `modules/screens/domain/`
   - Crear: `DispositivoRaspberry.java`, `Ubicacion.java`, etc.

2. **Mover servicios**
   - `ServicioPantalla.java` → `modules/screens/service/`

3. **Mover repositorios**
   - `RepositorioPantalla.java` → `modules/screens/repository/`

4. **Mover controladores**
   - `ControladorPantalla.java` → `modules/screens/controller/`
   - `ControladorRaspberryPi.java` → `modules/screens/controller/`

### Fase 5: Módulo Admin (1 día)

1. **Mover entidades**
   - `ConfiguracionSistema.java` → `modules/admin/domain/`
   - `Auditoria.java` → `modules/admin/domain/`

2. **Mover servicios**
   - `ServicioAdministracion.java` → `modules/admin/service/`
   - `ServicioModoMantenimiento.java` → `modules/admin/service/`

3. **Mover repositorios**
   - `RepositorioConfiguracionSistema.java` → `modules/admin/repository/`
   - `RepositorioAuditoria.java` → `modules/admin/repository/`

4. **Mover controlador**
   - `ControladorAdministracion.java` → `modules/admin/controller/`

### Fase 6: Módulo IA (1 día)

1. **Mover entidades**
   - `ConversacionIA.java` → `modules/ia/domain/`
   - Crear: `AgenteIA.java`, `MensajeIA.java`

2. **Mover servicios**
   - `ServicioAgenteIA.java` → `modules/ia/service/`

3. **Mover repositorios**
   - `RepositorioConversacionIA.java` → `modules/ia/repository/`

### Fase 7: Testing y ajustes (2-3 días)

1. Compilar y corregir imports
2. Ejecutar tests
3. Verificar endpoints
4. Actualizar documentación

## Ejemplo de Migración

### ANTES (Estructura actual)
```java
package com.innoad.servicio;

import com.innoad.modelo.entidades.Usuario;
import com.innoad.repositorio.RepositorioUsuario;

@Service
public class ServicioAutenticacion {
    @Autowired
    private RepositorioUsuario repositorioUsuario;
    
    public Usuario obtenerUsuario(Long id) {
        return repositorioUsuario.findById(id).orElseThrow();
    }
}
```

### DESPUÉS (Estructura modular)
```java
package com.innoad.modules.auth.service;

import com.innoad.modules.auth.domain.Usuario;
import com.innoad.modules.auth.repository.RepositorioUsuario;
import com.innoad.shared.exception.RecursoNoEncontradoException;

@Service
public class ServicioAutenticacion {
    @Autowired
    private RepositorioUsuario repositorioUsuario;
    
    public Usuario obtenerUsuario(Long id) {
        return repositorioUsuario.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
    }
}
```

### Uso desde otro módulo
```java
package com.innoad.modules.campaigns.service;

import com.innoad.modules.auth.service.ServicioAutenticacion;
import com.innoad.modules.auth.domain.Usuario;
import com.innoad.modules.campaigns.domain.Campana;

@Service
public class ServicioCampana {
    @Autowired
    private ServicioAutenticacion authService; // ✅ OK - Usar servicio
    
    // ❌ NO hacer esto:
    // @Autowired
    // private RepositorioUsuario repoUsuario; // Acceso directo a repo de otro módulo
    
    public Campana crearCampana(CampanaDTO dto) {
        // Obtener usuario a través del servicio
        Usuario usuario = authService.obtenerUsuario(dto.getUsuarioId());
        
        Campana campana = new Campana();
        campana.setUsuarioId(usuario.getId());
        return repositorioCampana.save(campana);
    }
}
```

## Beneficios Inmediatos

1. ✅ **Código mejor organizado** - Fácil encontrar componentes
2. ✅ **Separación de responsabilidades** - Cada módulo tiene un propósito claro
3. ✅ **Testing más fácil** - Probar módulos independientemente
4. ✅ **Onboarding rápido** - Nuevos devs encuentran código más rápido
5. ✅ **Preparado para microservicios** - Si en el futuro se necesita

## Verificación

Después de cada fase, verificar:

```bash
# Compilar
mvn clean compile

# Ejecutar tests
mvn test

# Ejecutar aplicación
mvn spring-boot:run

# Verificar endpoints
curl http://localhost:8080/api/v1/autenticacion/iniciar-sesion
```

## Notas Importantes

- **NO** mover todo de golpe. Hacerlo por fases.
- **Compilar** después de cada módulo migrado.
- **Commit** después de cada fase exitosa.
- **No romper** funcionalidad existente.
- Los DTOs compartidos van a `shared/dto/`.
- Enumeraciones compartidas van a `shared/domain/`.

## Tiempo Estimado Total

- **Fase 1 (Shared):** 1-2 días
- **Fase 2 (Auth):** 2-3 días
- **Fase 3 (Content):** 2 días
- **Fase 4 (Screens):** 2 días
- **Fase 5 (Admin):** 1 día
- **Fase 6 (IA):** 1 día
- **Fase 7 (Testing):** 2-3 días

**TOTAL: 11-14 días** (2-3 semanas trabajando a tiempo parcial)

## Siguiente Paso

Comenzar con **Fase 1: Mover código compartido** ya que es lo más sencillo y no rompe la funcionalidad existente.
