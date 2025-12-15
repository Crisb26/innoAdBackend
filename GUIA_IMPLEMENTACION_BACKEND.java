/**
 * GUIA_IMPLEMENTACION_BACKEND - Spring Security & Roles/Permisos
 * Paso a paso para implementar el sistema de roles y permisos
 */

// ============================================================================
// PASO 1: ACTUALIZAR ENTIDADES JPA
// ============================================================================

// 1.1 Usuario.java - Agregar relación con roles
@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String contrasena;

    @Column(nullable = false)
    private String nombre;

    @Column
    private String apellido;

    @Column
    private Boolean activo = true;

    // ✅ NUEVA RELACIÓN
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id")
    private Rol rol;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    public boolean tienePermiso(String codigoPermiso) {
        if (rol == null) return false;
        return rol.getPermisos().stream()
            .anyMatch(p -> p.getCodigo().equals(codigoPermiso));
    }

    public boolean tieneAlgunPermiso(String... codigos) {
        if (rol == null) return false;
        Set<String> permisosCodigos = rol.getPermisos().stream()
            .map(Permiso::getCodigo)
            .collect(Collectors.toSet());
        return Arrays.stream(codigos).anyMatch(permisosCodigos::contains);
    }

    public boolean tieneTodosPermisos(String... codigos) {
        if (rol == null) return false;
        Set<String> permisosCodigos = rol.getPermisos().stream()
            .map(Permiso::getCodigo)
            .collect(Collectors.toSet());
        return Arrays.stream(codigos).allMatch(permisosCodigos::contains);
    }
}

// 1.2 Rol.java - Nueva entidad
@Entity
@Table(name = "rol")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre; // SUPER_ADMIN, ADMIN, GERENTE, OPERADOR, USUARIO

    @Column
    private String descripcion;

    @Column(name = "nivel_acceso", nullable = false)
    private Integer nivelAcceso; // 1-5

    @Column
    private Boolean activo = true;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
        name = "rol_permiso",
        joinColumns = @JoinColumn(name = "rol_id"),
        inverseJoinColumns = @JoinColumn(name = "permiso_id")
    )
    private Set<Permiso> permisos = new HashSet<>();

    @OneToMany(mappedBy = "rol", cascade = CascadeType.REFRESH)
    private Set<Usuario> usuarios = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    public void agregarPermiso(Permiso permiso) {
        this.permisos.add(permiso);
        permiso.getRoles().add(this);
    }

    public void quitarPermiso(Permiso permiso) {
        this.permisos.remove(permiso);
        permiso.getRoles().remove(this);
    }

    public boolean tienePermiso(String codigoPermiso) {
        return this.permisos.stream()
            .anyMatch(p -> p.getCodigo().equals(codigoPermiso));
    }
}

// 1.3 Permiso.java - Nueva entidad
@Entity
@Table(name = "permiso")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Permiso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo; // USUARIOS_VER, CAMPANAS_CREAR, etc.

    @Column(nullable = false)
    private String descripcion;

    @Column
    private String categoria; // Sistema, Usuarios, Campañas, Contenidos, etc.

    @Column
    private Boolean activo = true;

    @ManyToMany(mappedBy = "permisos", fetch = FetchType.LAZY)
    private Set<Rol> roles = new HashSet<>();

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}

// ============================================================================
// PASO 2: CREAR REPOSITORIOS
// ============================================================================

// 2.1 RolRepository.java
@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(String nombre);
    List<Rol> findByActivoTrue();
    List<Rol> findByNivelAccesoGreaterThanEqualAndActivoTrue(Integer nivel);
}

// 2.2 PermisoRepository.java
@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Long> {
    Optional<Permiso> findByCodigo(String codigo);
    List<Permiso> findByActivoTrue();
    List<Permiso> findByCategoria(String categoria);
}

// 2.3 UsuarioRepository.java - Actualizar
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByActivoTrue();
    List<Usuario> findByRolNombre(String rolNombre); // ✅ NUEVO
    Page<Usuario> findByActivoTrueAndRolNivelAccesoGreaterThanEqual(
        Boolean activo, Integer nivel, Pageable pageable); // ✅ NUEVO
}

// ============================================================================
// PASO 3: CREAR SERVICIOS
// ============================================================================

// 3.1 RolService.java - Gestión de Roles
@Service
@Transactional
@Slf4j
public class RolService {
    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PermisoRepository permisoRepository;

    /**
     * Obtener todos los roles activos
     */
    public List<RolDTO> obtenerTodos() {
        return rolRepository.findByActivoTrue().stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }

    /**
     * Obtener rol por nombre
     */
    public RolDTO obtenerPorNombre(String nombre) {
        return rolRepository.findByNombre(nombre)
            .map(this::convertirADTO)
            .orElseThrow(() -> new RolNoEncontradoException("Rol no encontrado: " + nombre));
    }

    /**
     * Crear nuevo rol con permisos
     */
    public RolDTO crear(CrearRolRequest request) {
        // Validar que no exista
        if (rolRepository.findByNombre(request.getNombre()).isPresent()) {
            throw new RolYaExisteException("El rol " + request.getNombre() + " ya existe");
        }

        Rol rol = new Rol();
        rol.setNombre(request.getNombre());
        rol.setDescripcion(request.getDescripcion());
        rol.setNivelAcceso(request.getNivelAcceso());
        rol.setActivo(true);

        // Asignar permisos
        if (request.getCodigosPermisos() != null && !request.getCodigosPermisos().isEmpty()) {
            for (String codigoPermiso : request.getCodigosPermisos()) {
                Permiso permiso = permisoRepository.findByCodigo(codigoPermiso)
                    .orElseThrow(() -> new PermissionNotFoundException(
                        "Permiso no encontrado: " + codigoPermiso));
                rol.agregarPermiso(permiso);
            }
        }

        Rol rolGuardado = rolRepository.save(rol);
        log.info("Rol creado: {}", rol.getNombre());
        return convertirADTO(rolGuardado);
    }

    /**
     * Actualizar rol existente
     */
    public RolDTO actualizar(Long id, ActualizarRolRequest request) {
        Rol rol = rolRepository.findById(id)
            .orElseThrow(() -> new RolNoEncontradoException("Rol no encontrado"));

        rol.setDescripcion(request.getDescripcion());
        rol.setNivelAcceso(request.getNivelAcceso());

        // Actualizar permisos
        if (request.getCodigosPermisos() != null) {
            rol.getPermisos().clear();
            for (String codigoPermiso : request.getCodigosPermisos()) {
                Permiso permiso = permisoRepository.findByCodigo(codigoPermiso)
                    .orElseThrow(() -> new PermissionNotFoundException(
                        "Permiso no encontrado: " + codigoPermiso));
                rol.agregarPermiso(permiso);
            }
        }

        Rol rolActualizado = rolRepository.save(rol);
        log.info("Rol actualizado: {}", rol.getNombre());
        return convertirADTO(rolActualizado);
    }

    /**
     * Agregar permiso a rol
     */
    public RolDTO agregarPermiso(Long rolId, String codigoPermiso) {
        Rol rol = rolRepository.findById(rolId)
            .orElseThrow(() -> new RolNoEncontradoException("Rol no encontrado"));

        Permiso permiso = permisoRepository.findByCodigo(codigoPermiso)
            .orElseThrow(() -> new PermissionNotFoundException(
                "Permiso no encontrado: " + codigoPermiso));

        if (!rol.tienePermiso(codigoPermiso)) {
            rol.agregarPermiso(permiso);
            rolRepository.save(rol);
            log.info("Permiso {} agregado al rol {}", codigoPermiso, rol.getNombre());
        }

        return convertirADTO(rol);
    }

    /**
     * Remover permiso de rol
     */
    public RolDTO removerPermiso(Long rolId, String codigoPermiso) {
        Rol rol = rolRepository.findById(rolId)
            .orElseThrow(() -> new RolNoEncontradoException("Rol no encontrado"));

        Permiso permiso = rol.getPermisos().stream()
            .filter(p -> p.getCodigo().equals(codigoPermiso))
            .findFirst()
            .orElseThrow(() -> new PermissionNotFoundException(
                "Permiso no asignado al rol"));

        rol.quitarPermiso(permiso);
        rolRepository.save(rol);
        log.info("Permiso {} removido del rol {}", codigoPermiso, rol.getNombre());

        return convertirADTO(rol);
    }

    /**
     * Desactivar rol
     */
    public void desactivar(Long id) {
        Rol rol = rolRepository.findById(id)
            .orElseThrow(() -> new RolNoEncontradoException("Rol no encontrado"));
        rol.setActivo(false);
        rolRepository.save(rol);
        log.info("Rol desactivado: {}", rol.getNombre());
    }

    private RolDTO convertirADTO(Rol rol) {
        return RolDTO.builder()
            .id(rol.getId())
            .nombre(rol.getNombre())
            .descripcion(rol.getDescripcion())
            .nivelAcceso(rol.getNivelAcceso())
            .activo(rol.getActivo())
            .permisos(rol.getPermisos().stream()
                .map(p -> PermisoDTO.builder()
                    .id(p.getId())
                    .codigo(p.getCodigo())
                    .descripcion(p.getDescripcion())
                    .categoria(p.getCategoria())
                    .build())
                .collect(Collectors.toSet()))
            .build();
    }
}

// 3.2 PermissionService.java - Verificación de permisos
@Service
@Slf4j
public class PermissionService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Verificar si usuario tiene permiso
     */
    public boolean usuarioTienePermiso(Long usuarioId, String codigoPermiso) {
        return usuarioRepository.findById(usuarioId)
            .map(usuario -> usuario.tienePermiso(codigoPermiso))
            .orElse(false);
    }

    /**
     * Verificar si usuario tiene alguno de los permisos
     */
    public boolean usuarioTieneAlgunPermiso(Long usuarioId, String... codigos) {
        return usuarioRepository.findById(usuarioId)
            .map(usuario -> usuario.tieneAlgunPermiso(codigos))
            .orElse(false);
    }

    /**
     * Verificar si usuario tiene todos los permisos
     */
    public boolean usuarioTieneTodosPermisos(Long usuarioId, String... codigos) {
        return usuarioRepository.findById(usuarioId)
            .map(usuario -> usuario.tieneTodosPermisos(codigos))
            .orElse(false);
    }

    /**
     * Obtener usuario actual del contexto
     */
    public Usuario obtenerUsuarioActual() {
        String email = SecurityContextHolder.getContext()
            .getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
    }

    /**
     * Obtener permisos del usuario actual
     */
    public Set<String> obtenerPermisosActuales() {
        Usuario usuario = obtenerUsuarioActual();
        if (usuario.getRol() == null) {
            return new HashSet<>();
        }
        return usuario.getRol().getPermisos().stream()
            .map(Permiso::getCodigo)
            .collect(Collectors.toSet());
    }

    /**
     * Validar acceso a recurso específico
     */
    public void validarAcceso(Long usuarioId, String codigoPermiso) {
        if (!usuarioTienePermiso(usuarioId, codigoPermiso)) {
            log.warn("Acceso denegado para usuario {} a permiso {}", usuarioId, codigoPermiso);
            throw new AccesoNoAutorizadoException(
                "No tienes permiso para: " + codigoPermiso);
        }
    }
}

// ============================================================================
// PASO 4: CONFIGURAR SPRING SECURITY
// ============================================================================

// 4.1 SecurityConfig.java - Configuración de seguridad
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
    prePostEnabled = true,
    securedEnabled = true,
    jsr250Enabled = true
)
@Slf4j
public class SecurityConfig {
    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .authorizeRequests()
                // Rutas públicas
                .antMatchers("/api/auth/**", "/api/public/**").permitAll()
                .antMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                
                // Rutas protegidas
                .antMatchers("/api/usuarios/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                .antMatchers("/api/roles/**").hasRole("SUPER_ADMIN")
                .antMatchers("/api/campanas/**").hasAnyRole("GERENTE", "ADMIN", "SUPER_ADMIN")
                .antMatchers("/api/contenidos/**").hasAnyRole("OPERADOR", "GERENTE", "ADMIN", "SUPER_ADMIN")
                .antMatchers("/api/pantallas/**").hasAnyRole("OPERADOR", "GERENTE", "ADMIN", "SUPER_ADMIN")
                .antMatchers("/api/reportes/**").hasAnyRole("GERENTE", "ADMIN", "SUPER_ADMIN")
                
                // Todas las demás rutas requieren autenticación
                .anyRequest().authenticated()
                .and()
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}

// 4.2 Anotación personalizada - @RequierePermiso
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority(#permiso)")
public @interface RequierePermiso {
    String value();
}

// Uso:
// @RequierePermiso("USUARIOS_VER")
// public List<UsuarioDTO> obtenerTodos() { ... }

// ============================================================================
// PASO 5: CREAR CONTROLADORES
// ============================================================================

// 5.1 RolController.java
@RestController
@RequestMapping("/api/roles")
@Slf4j
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class RolController {
    @Autowired
    private RolService rolService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<RolDTO>> obtenerTodos() {
        return ResponseEntity.ok(rolService.obtenerTodos());
    }

    @GetMapping("/{nombre}")
    public ResponseEntity<RolDTO> obtenerPorNombre(@PathVariable String nombre) {
        return ResponseEntity.ok(rolService.obtenerPorNombre(nombre));
    }

    @PostMapping
    public ResponseEntity<RolDTO> crear(@RequestBody CrearRolRequest request) {
        log.info("Creando nuevo rol: {}", request.getNombre());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(rolService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RolDTO> actualizar(
            @PathVariable Long id,
            @RequestBody ActualizarRolRequest request) {
        log.info("Actualizando rol: {}", id);
        return ResponseEntity.ok(rolService.actualizar(id, request));
    }

    @PostMapping("/{rolId}/permisos/{codigoPermiso}")
    public ResponseEntity<RolDTO> agregarPermiso(
            @PathVariable Long rolId,
            @PathVariable String codigoPermiso) {
        log.info("Agregando permiso {} al rol {}", codigoPermiso, rolId);
        return ResponseEntity.ok(rolService.agregarPermiso(rolId, codigoPermiso));
    }

    @DeleteMapping("/{rolId}/permisos/{codigoPermiso}")
    public ResponseEntity<RolDTO> removerPermiso(
            @PathVariable Long rolId,
            @PathVariable String codigoPermiso) {
        log.info("Removiendo permiso {} del rol {}", codigoPermiso, rolId);
        return ResponseEntity.ok(rolService.removerPermiso(rolId, codigoPermiso));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        log.info("Desactivando rol: {}", id);
        rolService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}

// 5.2 PermissionController.java
@RestController
@RequestMapping("/api/permisos")
@PreAuthorize("isAuthenticated()")
@Slf4j
public class PermissionController {
    @Autowired
    private PermissionService permissionService;

    @GetMapping("/mis-permisos")
    public ResponseEntity<Set<String>> obtenerMisPermisos() {
        return ResponseEntity.ok(permissionService.obtenerPermisosActuales());
    }

    @GetMapping("/verificar/{codigo}")
    public ResponseEntity<Boolean> verificarPermiso(@PathVariable String codigo) {
        Usuario usuario = permissionService.obtenerUsuarioActual();
        boolean tiene = permissionService.usuarioTienePermiso(usuario.getId(), codigo);
        return ResponseEntity.ok(tiene);
    }

    @PostMapping("/validar")
    public ResponseEntity<Boolean> validarPermisos(@RequestBody ValidarPermisosRequest request) {
        Usuario usuario = permissionService.obtenerUsuarioActual();
        boolean valido;

        switch (request.getModo()) {
            case "todos":
                valido = permissionService.usuarioTieneTodosPermisos(
                    usuario.getId(),
                    request.getCodigosPermisos().toArray(new String[0]));
                break;
            case "alguno":
                valido = permissionService.usuarioTieneAlgunPermiso(
                    usuario.getId(),
                    request.getCodigosPermisos().toArray(new String[0]));
                break;
            default:
                valido = false;
        }

        return ResponseEntity.ok(valido);
    }
}

// ============================================================================
// PASO 6: INICIALIZAR PERMISOS Y ROLES POR DEFECTO
// ============================================================================

// 6.1 InitializerDataConfiguration.java - Llenar datos iniciales
@Configuration
@Slf4j
public class InitializerDataConfiguration {
    @Autowired
    private PermisoRepository permisoRepository;

    @Autowired
    private RolRepository rolRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeData() {
        try {
            // Crear permisos si no existen
            crearPermisosDefault();

            // Crear roles si no existen
            crearRolesDefault();

            log.info("✅ Datos iniciales cargados correctamente");
        } catch (Exception e) {
            log.error("❌ Error al inicializar datos", e);
        }
    }

    private void crearPermisosDefault() {
        String[][] permisos = {
            // SISTEMA
            {"MODO_MANTENIMIENTO_VER", "Ver estado mantenimiento", "Sistema"},
            {"MODO_MANTENIMIENTO_ACTIVAR", "Activar/desactivar mantenimiento", "Sistema"},
            {"CONFIGURACION_SISTEMA", "Modificar configuración del sistema", "Sistema"},
            
            // USUARIOS
            {"USUARIOS_VER", "Ver lista de usuarios", "Usuarios"},
            {"USUARIOS_CREAR", "Crear nuevos usuarios", "Usuarios"},
            {"USUARIOS_EDITAR", "Editar información de usuarios", "Usuarios"},
            {"USUARIOS_ELIMINAR", "Eliminar usuarios", "Usuarios"},
            
            // CAMPAÑAS
            {"CAMPANAS_VER", "Ver campañas", "Campañas"},
            {"CAMPANAS_CREAR", "Crear campañas", "Campañas"},
            {"CAMPANAS_EDITAR", "Editar campañas", "Campañas"},
            {"CAMPANAS_ELIMINAR", "Eliminar campañas", "Campañas"},
            {"CAMPANAS_PUBLICAR", "Publicar campañas", "Campañas"},
            
            // CONTENIDOS
            {"CONTENIDOS_VER", "Ver contenidos", "Contenidos"},
            {"CONTENIDOS_CREAR", "Crear contenidos", "Contenidos"},
            {"CONTENIDOS_EDITAR", "Editar contenidos", "Contenidos"},
            {"CONTENIDOS_ELIMINAR", "Eliminar contenidos", "Contenidos"},
            
            // PANTALLAS
            {"PANTALLAS_VER", "Ver pantallas", "Pantallas"},
            {"PANTALLAS_CREAR", "Crear pantallas", "Pantallas"},
            {"PANTALLAS_EDITAR", "Editar pantallas", "Pantallas"},
            {"PANTALLAS_ELIMINAR", "Eliminar pantallas", "Pantallas"},
            
            // REPORTES
            {"REPORTES_VER", "Ver reportes", "Reportes"},
            {"REPORTES_CREAR", "Crear reportes", "Reportes"},
            {"REPORTES_EXPORTAR", "Exportar reportes", "Reportes"}
        };

        for (String[] permiso : permisos) {
            if (permisoRepository.findByCodigo(permiso[0]).isEmpty()) {
                Permiso p = new Permiso();
                p.setCodigo(permiso[0]);
                p.setDescripcion(permiso[1]);
                p.setCategoria(permiso[2]);
                p.setActivo(true);
                permisoRepository.save(p);
                log.info("✓ Permiso creado: {}", permiso[0]);
            }
        }
    }

    private void crearRolesDefault() {
        // SUPER_ADMIN - Todos los permisos
        if (rolRepository.findByNombre("SUPER_ADMIN").isEmpty()) {
            Rol superAdmin = new Rol();
            superAdmin.setNombre("SUPER_ADMIN");
            superAdmin.setDescripcion("Super Administrador - Acceso completo");
            superAdmin.setNivelAcceso(5);
            superAdmin.setActivo(true);
            superAdmin.setPermisos(new HashSet<>(permisoRepository.findByActivoTrue()));
            rolRepository.save(superAdmin);
            log.info("✓ Rol creado: SUPER_ADMIN");
        }

        // ADMIN - Casi todos excepto backups restaurar
        if (rolRepository.findByNombre("ADMIN").isEmpty()) {
            Rol admin = new Rol();
            admin.setNombre("ADMIN");
            admin.setDescripcion("Administrador");
            admin.setNivelAcceso(4);
            admin.setActivo(true);
            
            Set<Permiso> permisos = permisoRepository.findByActivoTrue().stream()
                .filter(p -> !p.getCodigo().equals("BACKUPS_RESTAURAR"))
                .collect(Collectors.toSet());
            admin.setPermisos(permisos);
            rolRepository.save(admin);
            log.info("✓ Rol creado: ADMIN");
        }

        // GERENTE - Campañas, reportes, contenidos lectura
        if (rolRepository.findByNombre("GERENTE").isEmpty()) {
            Rol gerente = new Rol();
            gerente.setNombre("GERENTE");
            gerente.setDescripcion("Gerente");
            gerente.setNivelAcceso(3);
            gerente.setActivo(true);
            
            Set<Permiso> permisos = permisoRepository.findAll().stream()
                .filter(p -> p.getCodigo().matches("(CAMPANAS|REPORTES|CONTENIDOS_VER).*"))
                .collect(Collectors.toSet());
            gerente.setPermisos(permisos);
            rolRepository.save(gerente);
            log.info("✓ Rol creado: GERENTE");
        }

        // OPERADOR - Contenidos, pantallas CRUD
        if (rolRepository.findByNombre("OPERADOR").isEmpty()) {
            Rol operador = new Rol();
            operador.setNombre("OPERADOR");
            operador.setDescripcion("Operador");
            operador.setNivelAcceso(2);
            operador.setActivo(true);
            
            Set<Permiso> permisos = permisoRepository.findAll().stream()
                .filter(p -> p.getCodigo().matches("(CONTENIDOS|PANTALLAS).*"))
                .collect(Collectors.toSet());
            operador.setPermisos(permisos);
            rolRepository.save(operador);
            log.info("✓ Rol creado: OPERADOR");
        }

        // USUARIO - Lectura básica
        if (rolRepository.findByNombre("USUARIO").isEmpty()) {
            Rol usuario = new Rol();
            usuario.setNombre("USUARIO");
            usuario.setDescripcion("Usuario estándar");
            usuario.setNivelAcceso(1);
            usuario.setActivo(true);
            
            Set<Permiso> permisos = permisoRepository.findAll().stream()
                .filter(p -> p.getCodigo().matches(".*(VER|EXPORTAR).*"))
                .limit(5) // Mínimos permisos
                .collect(Collectors.toSet());
            usuario.setPermisos(permisos);
            rolRepository.save(usuario);
            log.info("✓ Rol creado: USUARIO");
        }
    }
}

// ============================================================================
// PASO 7: CREAR DTOs
// ============================================================================

// 7.1 RolDTO.java
@Data
@Builder
public class RolDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer nivelAcceso;
    private Boolean activo;
    private Set<PermisoDTO> permisos;
}

// 7.2 PermisoDTO.java
@Data
@Builder
public class PermisoDTO {
    private Long id;
    private String codigo;
    private String descripcion;
    private String categoria;
}

// 7.3 CrearRolRequest.java
@Data
public class CrearRolRequest {
    private String nombre;
    private String descripcion;
    private Integer nivelAcceso;
    private List<String> codigosPermisos;
}

// 7.4 ActualizarRolRequest.java
@Data
public class ActualizarRolRequest {
    private String descripcion;
    private Integer nivelAcceso;
    private List<String> codigosPermisos;
}

// 7.5 ValidarPermisosRequest.java
@Data
public class ValidarPermisosRequest {
    private List<String> codigosPermisos; // ["USUARIOS_VER", "USUARIOS_CREAR"]
    private String modo; // "todos" o "alguno"
}

// ============================================================================
// PASO 8: CONFIGURAR JPA SPECIFICATIONS PARA FILTRADO
// ============================================================================

// 8.1 UsuarioSpecifications.java - Filtrar usuarios por rol
public class UsuarioSpecifications {
    public static Specification<Usuario> conRol(String nombreRol) {
        return (root, query, cb) -> cb.equal(root.get("rol").get("nombre"), nombreRol);
    }

    public static Specification<Usuario> conNivelAccesoMinimo(Integer nivel) {
        return (root, query, cb) -> 
            cb.greaterThanOrEqualTo(root.get("rol").get("nivelAcceso"), nivel);
    }

    public static Specification<Usuario> estaActivo(Boolean activo) {
        return (root, query, cb) -> cb.equal(root.get("activo"), activo);
    }

    public static Specification<Usuario> filtrarPorPermiso(String codigoPermiso) {
        return (root, query, cb) -> {
            Join<Usuario, Rol> rolJoin = root.join("rol");
            Join<Rol, Permiso> permisoJoin = rolJoin.join("permisos");
            return cb.equal(permisoJoin.get("codigo"), codigoPermiso);
        };
    }
}

// Uso en servicio:
// Page<Usuario> usuariosAdmin = usuarioRepository.findAll(
//     Specification.where(UsuarioSpecifications.conRol("ADMIN"))
//         .and(UsuarioSpecifications.estaActivo(true)),
//     pageable
// );

// ============================================================================
// PASO 9: ASPECTOS PARA AUDITORÍA
// ============================================================================

// 9.1 AuditoriaAspect.java - Registrar cambios de permisos
@Aspect
@Component
@Slf4j
public class AuditoriaAspect {
    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Pointcut("execution(public * com.innoad.servicios.RolService.*(..))")
    public void rolServiceMethods() {}

    @Before("rolServiceMethods()")
    public void registrarIntento(JoinPoint joinPoint) {
        Usuario usuario = obtenerUsuarioActual();
        String metodo = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info("⏱ Operación iniciada por {} - Método: {}", 
            usuario.getEmail(), metodo);
    }

    @AfterReturning(pointcut = "rolServiceMethods()", returning = "resultado")
    public void registrarExito(JoinPoint joinPoint, Object resultado) {
        Usuario usuario = obtenerUsuarioActual();
        String metodo = joinPoint.getSignature().getName();

        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario(usuario);
        auditoria.setAccion(metodo);
        auditoria.setResultado("EXITOSO");
        auditoria.setTimestamp(LocalDateTime.now());

        auditoriaRepository.save(auditoria);
        log.info("✅ Operación completada por {} - Método: {}", 
            usuario.getEmail(), metodo);
    }

    @AfterThrowing(pointcut = "rolServiceMethods()", throwing = "exception")
    public void registrarFallo(JoinPoint joinPoint, Exception exception) {
        Usuario usuario = obtenerUsuarioActual();
        String metodo = joinPoint.getSignature().getName();

        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario(usuario);
        auditoria.setAccion(metodo);
        auditoria.setResultado("FALLIDO");
        auditoria.setDetalles(exception.getMessage());
        auditoria.setTimestamp(LocalDateTime.now());

        auditoriaRepository.save(auditoria);
        log.error("❌ Error en operación de {} - Método: {}", 
            usuario.getEmail(), metodo, exception);
    }

    private Usuario obtenerUsuarioActual() {
        String email = SecurityContextHolder.getContext()
            .getAuthentication().getName();
        // Asumir que está cargado o manejar excepción
        return null;
    }
}

