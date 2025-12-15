/**
 * CONSTANTES Y ENUMERACIONES - Roles y Permisos
 * Para usar tanto en Backend como Frontend
 */

// ============================================================================
// ENUMERACIONES DE ROLES
// ============================================================================

// Backend (Java)
public enum RolUsuario {
    SUPER_ADMIN(5, "Super Administrador"),
    ADMIN(4, "Administrador"),
    GERENTE(3, "Gerente"),
    OPERADOR(2, "Operador"),
    USUARIO(1, "Usuario");

    private final int nivel;
    private final String descripcion;

    RolUsuario(int nivel, String descripcion) {
        this.nivel = nivel;
        this.descripcion = descripcion;
    }

    public int getNivel() { return nivel; }
    public String getDescripcion() { return descripcion; }

    public boolean esAdmin() {
        return this == ADMIN || this == SUPER_ADMIN;
    }

    public boolean esGerente() {
        return this.nivel >= 3;
    }

    public boolean esOperador() {
        return this.nivel >= 2;
    }

    public boolean esUsuario() {
        return this.nivel >= 1;
    }
}

// Frontend (TypeScript)
export enum RolUsuario {
  SUPER_ADMIN = 'SUPER_ADMIN',
  ADMIN = 'ADMIN',
  GERENTE = 'GERENTE',
  OPERADOR = 'OPERADOR',
  USUARIO = 'USUARIO'
}

export const ROL_NIVELES: Record<RolUsuario, number> = {
  [RolUsuario.USUARIO]: 1,
  [RolUsuario.OPERADOR]: 2,
  [RolUsuario.GERENTE]: 3,
  [RolUsuario.ADMIN]: 4,
  [RolUsuario.SUPER_ADMIN]: 5
};

// ============================================================================
// ENUMERACIONES DE PERMISOS
// ============================================================================

// Backend (Java)
public enum CodigoPermiso {
    // SISTEMA
    MODO_MANTENIMIENTO_VER("MODO_MANTENIMIENTO_VER", "Ver estado mantenimiento", "Sistema"),
    MODO_MANTENIMIENTO_ACTIVAR("MODO_MANTENIMIENTO_ACTIVAR", "Activar/desactivar mantenimiento", "Sistema"),
    CONFIGURACION_SISTEMA("CONFIGURACION_SISTEMA", "Modificar configuración del sistema", "Sistema"),
    LOGS_AUDITORIA_VER("LOGS_AUDITORIA_VER", "Ver logs de auditoría", "Sistema"),
    LOGS_AUDITORIA_EXPORTAR("LOGS_AUDITORIA_EXPORTAR", "Exportar logs de auditoría", "Sistema"),
    BACKUPS_VER("BACKUPS_VER", "Ver backups", "Sistema"),
    BACKUPS_CREAR("BACKUPS_CREAR", "Crear backups", "Sistema"),
    BACKUPS_RESTAURAR("BACKUPS_RESTAURAR", "Restaurar desde backup", "Sistema"),

    // ROLES Y PERMISOS
    ROLES_VER("ROLES_VER", "Ver roles", "Roles"),
    ROLES_CREAR("ROLES_CREAR", "Crear roles", "Roles"),
    ROLES_EDITAR("ROLES_EDITAR", "Editar roles", "Roles"),
    ROLES_ELIMINAR("ROLES_ELIMINAR", "Eliminar roles", "Roles"),
    PERMISOS_VER("PERMISOS_VER", "Ver permisos disponibles", "Roles"),
    PERMISOS_ASIGNAR("PERMISOS_ASIGNAR", "Asignar permisos a roles", "Roles"),

    // USUARIOS
    USUARIOS_VER("USUARIOS_VER", "Ver lista de usuarios", "Usuarios"),
    USUARIOS_CREAR("USUARIOS_CREAR", "Crear nuevos usuarios", "Usuarios"),
    USUARIOS_EDITAR("USUARIOS_EDITAR", "Editar información de usuarios", "Usuarios"),
    USUARIOS_ELIMINAR("USUARIOS_ELIMINAR", "Eliminar usuarios", "Usuarios"),
    USUARIOS_CAMBIAR_ROL("USUARIOS_CAMBIAR_ROL", "Cambiar rol de usuario", "Usuarios"),
    USUARIOS_DESACTIVAR("USUARIOS_DESACTIVAR", "Desactivar usuarios", "Usuarios"),
    USUARIOS_EXPORTAR("USUARIOS_EXPORTAR", "Exportar lista de usuarios", "Usuarios"),
    PERFIL_VER_PROPIO("PERFIL_VER_PROPIO", "Ver su propio perfil", "Usuarios"),
    PERFIL_EDITAR_PROPIO("PERFIL_EDITAR_PROPIO", "Editar su propio perfil", "Usuarios"),
    PERFIL_CAMBIAR_CONTRASENA("PERFIL_CAMBIAR_CONTRASENA", "Cambiar su propia contraseña", "Usuarios"),
    PERFIL_VER_OTROS("PERFIL_VER_OTROS", "Ver perfiles de otros usuarios", "Usuarios"),

    // CAMPAÑAS
    CAMPANAS_VER("CAMPANAS_VER", "Ver campañas", "Campañas"),
    CAMPANAS_VER_PROPIAS("CAMPANAS_VER_PROPIAS", "Ver sus propias campañas", "Campañas"),
    CAMPANAS_CREAR("CAMPANAS_CREAR", "Crear campañas", "Campañas"),
    CAMPANAS_EDITAR("CAMPANAS_EDITAR", "Editar campañas", "Campañas"),
    CAMPANAS_ELIMINAR("CAMPANAS_ELIMINAR", "Eliminar campañas", "Campañas"),
    CAMPANAS_PUBLICAR("CAMPANAS_PUBLICAR", "Publicar campañas", "Campañas"),
    CAMPANAS_DESPUBLICAR("CAMPANAS_DESPUBLICAR", "Despublicar campañas", "Campañas"),
    CAMPANAS_PROGRAMAR("CAMPANAS_PROGRAMAR", "Programar fechas de campañas", "Campañas"),
    CAMPANAS_CLONAR("CAMPANAS_CLONAR", "Clonar campañas existentes", "Campañas"),

    // CONTENIDOS
    CONTENIDOS_VER("CONTENIDOS_VER", "Ver contenidos", "Contenidos"),
    CONTENIDOS_VER_PROPIOS("CONTENIDOS_VER_PROPIOS", "Ver sus propios contenidos", "Contenidos"),
    CONTENIDOS_CREAR("CONTENIDOS_CREAR", "Crear contenidos", "Contenidos"),
    CONTENIDOS_EDITAR("CONTENIDOS_EDITAR", "Editar contenidos", "Contenidos"),
    CONTENIDOS_ELIMINAR("CONTENIDOS_ELIMINAR", "Eliminar contenidos", "Contenidos"),
    CONTENIDOS_SUBIR_MULTIMEDIA("CONTENIDOS_SUBIR_MULTIMEDIA", "Subir archivos multimedia", "Contenidos"),
    CONTENIDOS_APROBAR("CONTENIDOS_APROBAR", "Aprobar contenidos", "Contenidos"),
    CONTENIDOS_RECHAZAR("CONTENIDOS_RECHAZAR", "Rechazar contenidos", "Contenidos"),
    CONTENIDOS_VER_VERSIONES("CONTENIDOS_VER_VERSIONES", "Ver versiones de contenidos", "Contenidos"),
    CONTENIDOS_EXPORTAR("CONTENIDOS_EXPORTAR", "Exportar contenidos", "Contenidos"),

    // PANTALLAS
    PANTALLAS_VER("PANTALLAS_VER", "Ver pantallas", "Pantallas"),
    PANTALLAS_CREAR("PANTALLAS_CREAR", "Crear pantallas", "Pantallas"),
    PANTALLAS_EDITAR("PANTALLAS_EDITAR", "Editar pantallas", "Pantallas"),
    PANTALLAS_ELIMINAR("PANTALLAS_ELIMINAR", "Eliminar pantallas", "Pantallas"),
    PANTALLAS_ASIGNAR_CONTENIDO("PANTALLAS_ASIGNAR_CONTENIDO", "Asignar contenido a pantallas", "Pantallas"),
    PANTALLAS_PROGRAMAR("PANTALLAS_PROGRAMAR", "Programar pantallas", "Pantallas"),
    PANTALLAS_MONITOREAR("PANTALLAS_MONITOREAR", "Monitorear pantallas", "Pantallas"),
    PANTALLAS_CONTROL_REMOTO("PANTALLAS_CONTROL_REMOTO", "Control remoto de pantallas", "Pantallas"),

    // REPORTES
    REPORTES_VER("REPORTES_VER", "Ver reportes", "Reportes"),
    REPORTES_VER_PROPIOS("REPORTES_VER_PROPIOS", "Ver sus propios reportes", "Reportes"),
    REPORTES_CREAR("REPORTES_CREAR", "Crear reportes", "Reportes"),
    REPORTES_PERSONALIZADO("REPORTES_PERSONALIZADO", "Crear reportes personalizados", "Reportes"),
    REPORTES_EXPORTAR_PDF("REPORTES_EXPORTAR_PDF", "Exportar reportes a PDF", "Reportes"),
    REPORTES_EXPORTAR_CSV("REPORTES_EXPORTAR_CSV", "Exportar reportes a CSV", "Reportes"),
    REPORTES_PROGRAMAR_ENVIO("REPORTES_PROGRAMAR_ENVIO", "Programar envío de reportes", "Reportes"),
    ESTADISTICAS_VER("ESTADISTICAS_VER", "Ver estadísticas", "Reportes"),
    ESTADISTICAS_TIEMPO_REAL("ESTADISTICAS_TIEMPO_REAL", "Ver estadísticas en tiempo real", "Reportes"),

    // INTEGRACIONES
    INTEGRACIONES_VER("INTEGRACIONES_VER", "Ver integraciones", "Integraciones"),
    INTEGRACIONES_CREAR("INTEGRACIONES_CREAR", "Crear integraciones", "Integraciones"),
    INTEGRACIONES_EDITAR("INTEGRACIONES_EDITAR", "Editar integraciones", "Integraciones"),
    INTEGRACIONES_ELIMINAR("INTEGRACIONES_ELIMINAR", "Eliminar integraciones", "Integraciones"),
    API_KEYS_VER("API_KEYS_VER", "Ver API keys", "Integraciones"),
    API_KEYS_CREAR("API_KEYS_CREAR", "Crear API keys", "Integraciones"),
    API_KEYS_REGENERAR("API_KEYS_REGENERAR", "Regenerar API keys", "Integraciones");

    private final String codigo;
    private final String descripcion;
    private final String categoria;

    CodigoPermiso(String codigo, String descripcion, String categoria) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.categoria = categoria;
    }

    public String getCodigo() { return codigo; }
    public String getDescripcion() { return descripcion; }
    public String getCategoria() { return categoria; }
}

// Frontend (TypeScript)
export enum CodigoPermiso {
  // SISTEMA
  MODO_MANTENIMIENTO_VER = 'MODO_MANTENIMIENTO_VER',
  MODO_MANTENIMIENTO_ACTIVAR = 'MODO_MANTENIMIENTO_ACTIVAR',
  CONFIGURACION_SISTEMA = 'CONFIGURACION_SISTEMA',
  LOGS_AUDITORIA_VER = 'LOGS_AUDITORIA_VER',
  LOGS_AUDITORIA_EXPORTAR = 'LOGS_AUDITORIA_EXPORTAR',
  BACKUPS_VER = 'BACKUPS_VER',
  BACKUPS_CREAR = 'BACKUPS_CREAR',
  BACKUPS_RESTAURAR = 'BACKUPS_RESTAURAR',

  // ROLES Y PERMISOS
  ROLES_VER = 'ROLES_VER',
  ROLES_CREAR = 'ROLES_CREAR',
  ROLES_EDITAR = 'ROLES_EDITAR',
  ROLES_ELIMINAR = 'ROLES_ELIMINAR',
  PERMISOS_VER = 'PERMISOS_VER',
  PERMISOS_ASIGNAR = 'PERMISOS_ASIGNAR',

  // USUARIOS
  USUARIOS_VER = 'USUARIOS_VER',
  USUARIOS_CREAR = 'USUARIOS_CREAR',
  USUARIOS_EDITAR = 'USUARIOS_EDITAR',
  USUARIOS_ELIMINAR = 'USUARIOS_ELIMINAR',
  USUARIOS_CAMBIAR_ROL = 'USUARIOS_CAMBIAR_ROL',
  USUARIOS_DESACTIVAR = 'USUARIOS_DESACTIVAR',
  USUARIOS_EXPORTAR = 'USUARIOS_EXPORTAR',
  PERFIL_VER_PROPIO = 'PERFIL_VER_PROPIO',
  PERFIL_EDITAR_PROPIO = 'PERFIL_EDITAR_PROPIO',
  PERFIL_CAMBIAR_CONTRASENA = 'PERFIL_CAMBIAR_CONTRASENA',
  PERFIL_VER_OTROS = 'PERFIL_VER_OTROS',

  // CAMPAÑAS
  CAMPANAS_VER = 'CAMPANAS_VER',
  CAMPANAS_VER_PROPIAS = 'CAMPANAS_VER_PROPIAS',
  CAMPANAS_CREAR = 'CAMPANAS_CREAR',
  CAMPANAS_EDITAR = 'CAMPANAS_EDITAR',
  CAMPANAS_ELIMINAR = 'CAMPANAS_ELIMINAR',
  CAMPANAS_PUBLICAR = 'CAMPANAS_PUBLICAR',
  CAMPANAS_DESPUBLICAR = 'CAMPANAS_DESPUBLICAR',
  CAMPANAS_PROGRAMAR = 'CAMPANAS_PROGRAMAR',
  CAMPANAS_CLONAR = 'CAMPANAS_CLONAR',

  // CONTENIDOS
  CONTENIDOS_VER = 'CONTENIDOS_VER',
  CONTENIDOS_VER_PROPIOS = 'CONTENIDOS_VER_PROPIOS',
  CONTENIDOS_CREAR = 'CONTENIDOS_CREAR',
  CONTENIDOS_EDITAR = 'CONTENIDOS_EDITAR',
  CONTENIDOS_ELIMINAR = 'CONTENIDOS_ELIMINAR',
  CONTENIDOS_SUBIR_MULTIMEDIA = 'CONTENIDOS_SUBIR_MULTIMEDIA',
  CONTENIDOS_APROBAR = 'CONTENIDOS_APROBAR',
  CONTENIDOS_RECHAZAR = 'CONTENIDOS_RECHAZAR',
  CONTENIDOS_VER_VERSIONES = 'CONTENIDOS_VER_VERSIONES',
  CONTENIDOS_EXPORTAR = 'CONTENIDOS_EXPORTAR',

  // PANTALLAS
  PANTALLAS_VER = 'PANTALLAS_VER',
  PANTALLAS_CREAR = 'PANTALLAS_CREAR',
  PANTALLAS_EDITAR = 'PANTALLAS_EDITAR',
  PANTALLAS_ELIMINAR = 'PANTALLAS_ELIMINAR',
  PANTALLAS_ASIGNAR_CONTENIDO = 'PANTALLAS_ASIGNAR_CONTENIDO',
  PANTALLAS_PROGRAMAR = 'PANTALLAS_PROGRAMAR',
  PANTALLAS_MONITOREAR = 'PANTALLAS_MONITOREAR',
  PANTALLAS_CONTROL_REMOTO = 'PANTALLAS_CONTROL_REMOTO',

  // REPORTES
  REPORTES_VER = 'REPORTES_VER',
  REPORTES_VER_PROPIOS = 'REPORTES_VER_PROPIOS',
  REPORTES_CREAR = 'REPORTES_CREAR',
  REPORTES_PERSONALIZADO = 'REPORTES_PERSONALIZADO',
  REPORTES_EXPORTAR_PDF = 'REPORTES_EXPORTAR_PDF',
  REPORTES_EXPORTAR_CSV = 'REPORTES_EXPORTAR_CSV',
  REPORTES_PROGRAMAR_ENVIO = 'REPORTES_PROGRAMAR_ENVIO',
  ESTADISTICAS_VER = 'ESTADISTICAS_VER',
  ESTADISTICAS_TIEMPO_REAL = 'ESTADISTICAS_TIEMPO_REAL',

  // INTEGRACIONES
  INTEGRACIONES_VER = 'INTEGRACIONES_VER',
  INTEGRACIONES_CREAR = 'INTEGRACIONES_CREAR',
  INTEGRACIONES_EDITAR = 'INTEGRACIONES_EDITAR',
  INTEGRACIONES_ELIMINAR = 'INTEGRACIONES_ELIMINAR',
  API_KEYS_VER = 'API_KEYS_VER',
  API_KEYS_CREAR = 'API_KEYS_CREAR',
  API_KEYS_REGENERAR = 'API_KEYS_REGENERAR'
}

// ============================================================================
// MAPEO DE ROLES A PERMISOS PREDETERMINADOS
// ============================================================================

export const PERMISOS_POR_ROL: Record<RolUsuario, CodigoPermiso[]> = {
  [RolUsuario.SUPER_ADMIN]: [
    // Todos los permisos
    ...Object.values(CodigoPermiso)
  ],
  
  [RolUsuario.ADMIN]: [
    // Sistema (excepto backup restaurar)
    CodigoPermiso.MODO_MANTENIMIENTO_VER,
    CodigoPermiso.MODO_MANTENIMIENTO_ACTIVAR,
    CodigoPermiso.CONFIGURACION_SISTEMA,
    CodigoPermiso.LOGS_AUDITORIA_VER,
    CodigoPermiso.LOGS_AUDITORIA_EXPORTAR,
    CodigoPermiso.BACKUPS_VER,
    CodigoPermiso.BACKUPS_CREAR,
    
    // Roles y Permisos
    CodigoPermiso.ROLES_VER,
    CodigoPermiso.ROLES_CREAR,
    CodigoPermiso.ROLES_EDITAR,
    CodigoPermiso.ROLES_ELIMINAR,
    CodigoPermiso.PERMISOS_VER,
    CodigoPermiso.PERMISOS_ASIGNAR,
    
    // Usuarios
    CodigoPermiso.USUARIOS_VER,
    CodigoPermiso.USUARIOS_CREAR,
    CodigoPermiso.USUARIOS_EDITAR,
    CodigoPermiso.USUARIOS_ELIMINAR,
    CodigoPermiso.USUARIOS_CAMBIAR_ROL,
    CodigoPermiso.USUARIOS_DESACTIVAR,
    CodigoPermiso.USUARIOS_EXPORTAR,
    CodigoPermiso.PERFIL_VER_PROPIO,
    CodigoPermiso.PERFIL_EDITAR_PROPIO,
    CodigoPermiso.PERFIL_CAMBIAR_CONTRASENA,
    CodigoPermiso.PERFIL_VER_OTROS,
    
    // Campañas
    CodigoPermiso.CAMPANAS_VER,
    CodigoPermiso.CAMPANAS_CREAR,
    CodigoPermiso.CAMPANAS_EDITAR,
    CodigoPermiso.CAMPANAS_ELIMINAR,
    CodigoPermiso.CAMPANAS_PUBLICAR,
    CodigoPermiso.CAMPANAS_DESPUBLICAR,
    CodigoPermiso.CAMPANAS_PROGRAMAR,
    CodigoPermiso.CAMPANAS_CLONAR,
    
    // Contenidos
    CodigoPermiso.CONTENIDOS_VER,
    CodigoPermiso.CONTENIDOS_CREAR,
    CodigoPermiso.CONTENIDOS_EDITAR,
    CodigoPermiso.CONTENIDOS_ELIMINAR,
    CodigoPermiso.CONTENIDOS_SUBIR_MULTIMEDIA,
    CodigoPermiso.CONTENIDOS_APROBAR,
    CodigoPermiso.CONTENIDOS_RECHAZAR,
    CodigoPermiso.CONTENIDOS_VER_VERSIONES,
    CodigoPermiso.CONTENIDOS_EXPORTAR,
    
    // Pantallas
    CodigoPermiso.PANTALLAS_VER,
    CodigoPermiso.PANTALLAS_CREAR,
    CodigoPermiso.PANTALLAS_EDITAR,
    CodigoPermiso.PANTALLAS_ELIMINAR,
    CodigoPermiso.PANTALLAS_ASIGNAR_CONTENIDO,
    CodigoPermiso.PANTALLAS_PROGRAMAR,
    CodigoPermiso.PANTALLAS_MONITOREAR,
    CodigoPermiso.PANTALLAS_CONTROL_REMOTO,
    
    // Reportes
    CodigoPermiso.REPORTES_VER,
    CodigoPermiso.REPORTES_CREAR,
    CodigoPermiso.REPORTES_PERSONALIZADO,
    CodigoPermiso.REPORTES_EXPORTAR_PDF,
    CodigoPermiso.REPORTES_EXPORTAR_CSV,
    CodigoPermiso.REPORTES_PROGRAMAR_ENVIO,
    CodigoPermiso.ESTADISTICAS_VER,
    CodigoPermiso.ESTADISTICAS_TIEMPO_REAL,
    
    // Integraciones
    CodigoPermiso.INTEGRACIONES_VER,
    CodigoPermiso.INTEGRACIONES_CREAR,
    CodigoPermiso.INTEGRACIONES_EDITAR,
    CodigoPermiso.INTEGRACIONES_ELIMINAR,
    CodigoPermiso.API_KEYS_VER,
    CodigoPermiso.API_KEYS_CREAR,
    CodigoPermiso.API_KEYS_REGENERAR
  ],
  
  [RolUsuario.GERENTE]: [
    // Perfil
    CodigoPermiso.PERFIL_VER_PROPIO,
    CodigoPermiso.PERFIL_EDITAR_PROPIO,
    CodigoPermiso.PERFIL_CAMBIAR_CONTRASENA,
    
    // Campañas
    CodigoPermiso.CAMPANAS_VER,
    CodigoPermiso.CAMPANAS_CREAR,
    CodigoPermiso.CAMPANAS_EDITAR,
    CodigoPermiso.CAMPANAS_ELIMINAR,
    CodigoPermiso.CAMPANAS_PUBLICAR,
    CodigoPermiso.CAMPANAS_DESPUBLICAR,
    CodigoPermiso.CAMPANAS_PROGRAMAR,
    CodigoPermiso.CAMPANAS_CLONAR,
    
    // Contenidos (lectura)
    CodigoPermiso.CONTENIDOS_VER,
    CodigoPermiso.CONTENIDOS_VER_VERSIONES,
    CodigoPermiso.CONTENIDOS_EXPORTAR,
    
    // Pantallas (monitoreo)
    CodigoPermiso.PANTALLAS_VER,
    CodigoPermiso.PANTALLAS_MONITOREAR,
    
    // Reportes
    CodigoPermiso.REPORTES_VER,
    CodigoPermiso.REPORTES_CREAR,
    CodigoPermiso.REPORTES_PERSONALIZADO,
    CodigoPermiso.REPORTES_EXPORTAR_PDF,
    CodigoPermiso.REPORTES_EXPORTAR_CSV,
    CodigoPermiso.REPORTES_PROGRAMAR_ENVIO,
    CodigoPermiso.ESTADISTICAS_VER,
    CodigoPermiso.ESTADISTICAS_TIEMPO_REAL
  ],
  
  [RolUsuario.OPERADOR]: [
    // Perfil
    CodigoPermiso.PERFIL_VER_PROPIO,
    CodigoPermiso.PERFIL_EDITAR_PROPIO,
    CodigoPermiso.PERFIL_CAMBIAR_CONTRASENA,
    
    // Contenidos (CRUD)
    CodigoPermiso.CONTENIDOS_VER,
    CodigoPermiso.CONTENIDOS_CREAR,
    CodigoPermiso.CONTENIDOS_EDITAR,
    CodigoPermiso.CONTENIDOS_ELIMINAR,
    CodigoPermiso.CONTENIDOS_SUBIR_MULTIMEDIA,
    CodigoPermiso.CONTENIDOS_VER_VERSIONES,
    CodigoPermiso.CONTENIDOS_EXPORTAR,
    
    // Pantallas (CRUD + control)
    CodigoPermiso.PANTALLAS_VER,
    CodigoPermiso.PANTALLAS_CREAR,
    CodigoPermiso.PANTALLAS_EDITAR,
    CodigoPermiso.PANTALLAS_ELIMINAR,
    CodigoPermiso.PANTALLAS_ASIGNAR_CONTENIDO,
    CodigoPermiso.PANTALLAS_PROGRAMAR,
    CodigoPermiso.PANTALLAS_MONITOREAR,
    CodigoPermiso.PANTALLAS_CONTROL_REMOTO,
    
    // Campañas (lectura)
    CodigoPermiso.CAMPANAS_VER,
    
    // Reportes (lectura de propios)
    CodigoPermiso.REPORTES_VER_PROPIOS,
    CodigoPermiso.ESTADISTICAS_VER
  ],
  
  [RolUsuario.USUARIO]: [
    // Perfil (propio)
    CodigoPermiso.PERFIL_VER_PROPIO,
    CodigoPermiso.PERFIL_EDITAR_PROPIO,
    CodigoPermiso.PERFIL_CAMBIAR_CONTRASENA,
    
    // Campañas (lectura propias)
    CodigoPermiso.CAMPANAS_VER,
    CodigoPermiso.CAMPANAS_VER_PROPIAS,
    
    // Contenidos (lectura propios)
    CodigoPermiso.CONTENIDOS_VER,
    CodigoPermiso.CONTENIDOS_VER_PROPIOS,
    CodigoPermiso.CONTENIDOS_VER_VERSIONES,
    
    // Pantallas (lectura + monitoreo)
    CodigoPermiso.PANTALLAS_VER,
    CodigoPermiso.PANTALLAS_MONITOREAR,
    
    // Reportes (lectura propios)
    CodigoPermiso.REPORTES_VER,
    CodigoPermiso.REPORTES_VER_PROPIOS,
    CodigoPermiso.REPORTES_EXPORTAR_PDF,
    CodigoPermiso.REPORTES_EXPORTAR_CSV,
    CodigoPermiso.ESTADISTICAS_VER,
    CodigoPermiso.ESTADISTICAS_TIEMPO_REAL
  ]
};

