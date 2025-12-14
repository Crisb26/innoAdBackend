package com.innoad.modules.admin.service;

import com.innoad.modules.admin.domain.ConfiguracionSistema;
import com.innoad.modules.admin.domain.TipoMantenimiento;
import com.innoad.modules.auth.domain.Usuario;
import com.innoad.modules.admin.repository.RepositorioConfiguracionSistema;
import com.innoad.servicio.ServicioEmail;
import com.innoad.shared.dto.RolUsuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar el modo mantenimiento del sistema.
 * Controla el acceso basado en roles y código de seguridad.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ServicioModoMantenimiento {
    
    private final RepositorioConfiguracionSistema repositorioConfiguracion;
    private final PasswordEncoder passwordEncoder;
    private final ServicioEmail servicioEmail;
    
    @Value("${innoad.maintenance.security-code}")
    private String codigoSeguridadPorDefecto;
    
    @Value("${innoad.maintenance.email-for-recovery}")
    private String emailRecuperacionPorDefecto;
    
    private static final String CLAVE_CONFIG_MANTENIMIENTO = "MODO_MANTENIMIENTO";
    
    /**
     * Activa el modo mantenimiento CON CONTROL DE ROLES
     */
    @Transactional
    public void activarModoMantenimiento(
            Usuario usuario,
            String codigoSeguridad,
            String mensaje,
            LocalDateTime fechaFinEstimada,
            TipoMantenimiento tipo,
            List<RolUsuario> rolesAfectados,
            List<RolUsuario> rolesExcluidos,
            String urlContacto
    ) {
        if (!usuario.esAdministrador()) {
            throw new RuntimeException("Solo administradores pueden activar mantenimiento");
        }

        if (!verificarCodigoSeguridad(codigoSeguridad)) {
            throw new RuntimeException("Código de seguridad inválido");
        }

        ConfiguracionSistema config = obtenerOCrearConfiguracion();

        // Validar y establecer rolesExcluidos (ADMIN y DESARROLLADOR siempre)
        if (rolesExcluidos == null) {
            rolesExcluidos = new ArrayList<>();
        }
        if (!rolesExcluidos.contains(RolUsuario.ADMIN)) {
            rolesExcluidos.add(RolUsuario.ADMIN);
        }
        if (!rolesExcluidos.contains(RolUsuario.DESARROLLADOR)) {
            rolesExcluidos.add(RolUsuario.DESARROLLADOR);
        }

        // Si no especifica roles afectados, afectar a VISITANTE y USUARIO
        if (rolesAfectados == null || rolesAfectados.isEmpty()) {
            rolesAfectados = List.of(RolUsuario.VISITANTE, RolUsuario.USUARIO);
        }

        // Activar
        config.setModoMantenimientoActivo(true);
        config.setFechaInicioMantenimiento(LocalDateTime.now());
        config.setFechaFinEstimadaMantenimiento(fechaFinEstimada);
        config.setMensajeMantenimiento(mensaje != null ? mensaje : 
            "Estamos mejorando nuestro sistema para ofrecerte una mejor experiencia. Volveremos pronto.");
        config.setTipoMantenimiento(tipo != null ? tipo : TipoMantenimiento.PROGRAMADO);
        config.setRolesAfectados(rolesAfectados);
        config.setRolesExcluidos(rolesExcluidos);
        config.setUrlContactoSoporte(urlContacto != null ? urlContacto : "soporte@innoad.com");
        config.setUsuarioActualizacionId(usuario.getId());
        config.setFechaActualizacion(LocalDateTime.now());

        repositorioConfiguracion.save(config);
        
        log.info("Modo mantenimiento activado por {}. Tipo: {}. Roles afectados: {}",
                usuario.getEmail(), tipo, rolesAfectados);
    }
    
    /**
     * Desactiva el modo mantenimiento
     */
    @Transactional
    public void desactivarModoMantenimiento(Usuario usuario, String codigoSeguridad) {
        if (!usuario.esAdministrador()) {
            throw new RuntimeException("Solo administradores pueden desactivar mantenimiento");
        }

        if (!verificarCodigoSeguridad(codigoSeguridad)) {
            throw new RuntimeException("Código de seguridad inválido");
        }

        ConfiguracionSistema config = obtenerOCrearConfiguracion();
        
        config.setModoMantenimientoActivo(false);
        config.setFechaFinEstimadaMantenimiento(null);
        config.setUsuarioActualizacionId(usuario.getId());
        config.setFechaActualizacion(LocalDateTime.now());

        repositorioConfiguracion.save(config);
        
        log.info("Modo mantenimiento desactivado por {}", usuario.getEmail());
    }
    
    /**
     * Verifica si el modo mantenimiento está activo
     */
    public boolean esModoMantenimientoActivo() {
        Optional<ConfiguracionSistema> config = repositorioConfiguracion
                .findByClave(CLAVE_CONFIG_MANTENIMIENTO);
        
        if (config.isPresent()) {
            return config.get().estaActivo();
        }
        return false;
    }
    
    /**
     * Obtiene la información del modo mantenimiento
     */
    public ConfiguracionSistema obtenerInformacionMantenimiento() {
        return obtenerOCrearConfiguracion();
    }

    /**
     * Verifica si un usuario puede acceder durante el mantenimiento
     */
    public boolean puedeAcceder(Usuario usuario) {
        ConfiguracionSistema config = obtenerOCrearConfiguracion();
        return config.puedeAcceder(usuario.getRol());
    }

    /**
     * Verifica el código de seguridad
     */
    private boolean verificarCodigoSeguridad(String codigoIngresado) {
        ConfiguracionSistema config = obtenerOCrearConfiguracion();
        
        if (config.getCodigoSeguridadMantenimiento() != null) {
            return passwordEncoder.matches(codigoIngresado, config.getCodigoSeguridadMantenimiento());
        }
        
        return codigoIngresado.equals(codigoSeguridadPorDefecto);
    }
    
    /**
     * Obtiene o crea la configuración de mantenimiento
     */
    private ConfiguracionSistema obtenerOCrearConfiguracion() {
        return repositorioConfiguracion.findByClave(CLAVE_CONFIG_MANTENIMIENTO)
                .orElseGet(() -> {
                    ConfiguracionSistema nuevaConfig = new ConfiguracionSistema();
                    nuevaConfig.setClave(CLAVE_CONFIG_MANTENIMIENTO);
                    nuevaConfig.setValor("false");
                    nuevaConfig.setDescripcion("Configuración del modo mantenimiento del sistema");
                    nuevaConfig.setModoMantenimientoActivo(false);
                    nuevaConfig.setTipoMantenimiento(TipoMantenimiento.PROGRAMADO);
                    nuevaConfig.setRolesExcluidos(
                        List.of(RolUsuario.ADMIN, RolUsuario.DESARROLLADOR)
                    );
                    nuevaConfig.setRolesAfectados(
                        List.of(RolUsuario.VISITANTE, RolUsuario.USUARIO)
                    );
                    nuevaConfig.setUrlContactoSoporte("soporte@innoad.com");
                    return repositorioConfiguracion.save(nuevaConfig);
                });
    }
}
    
    /**
     * Activa el modo mantenimiento CON CONTROL DE ROLES
     */
    @Transactional
    public void activarModoMantenimiento(Usuario usuario, String codigoSeguridad, String mensaje, LocalDateTime fechaFinEstimada) {
        // Solo administradores pueden activar el modo mantenimiento
        if (!usuario.esAdministrador()) {
            throw new RuntimeException("Solo los administradores pueden activar el modo mantenimiento");
        }
        
        // Verificar código de seguridad
        if (!verificarCodigoSeguridad(codigoSeguridad)) {
            throw new RuntimeException("Código de seguridad inválido");
        }
        
        // Obtener o crear configuración
        ConfiguracionSistema config = obtenerOCrearConfiguracion();
        
        // Activar modo mantenimiento
        config.setModoMantenimientoActivo(true);
        config.setFechaInicioMantenimiento(LocalDateTime.now());
        config.setFechaFinEstimadaMantenimiento(fechaFinEstimada);
        config.setMensajeMantenimiento(mensaje != null ? mensaje : 
            "Estamos mejorando nuestro sistema para ofrecerte una mejor experiencia. Volveremos pronto.");
        config.setUsuarioActualizacionId(usuario.getId());
        config.setFechaActualizacion(LocalDateTime.now());
        
        repositorioConfiguracion.save(config);
    }
    
    /**
     * Desactiva el modo mantenimiento
     */
    @Transactional
    public void desactivarModoMantenimiento(Usuario usuario, String codigoSeguridad) {
        // Solo administradores pueden desactivar el modo mantenimiento
        if (!usuario.esAdministrador()) {
            throw new RuntimeException("Solo los administradores pueden desactivar el modo mantenimiento");
        }
        
        // Verificar código de seguridad
        if (!verificarCodigoSeguridad(codigoSeguridad)) {
            throw new RuntimeException("Código de seguridad inválido");
        }
        
        // Obtener configuración
        ConfiguracionSistema config = obtenerOCrearConfiguracion();
        
        // Desactivar modo mantenimiento
        config.setModoMantenimientoActivo(false);
        config.setFechaFinEstimadaMantenimiento(null);
        config.setUsuarioActualizacionId(usuario.getId());
        config.setFechaActualizacion(LocalDateTime.now());
        
        repositorioConfiguracion.save(config);
    }
    
    /**
     * Verifica si el modo mantenimiento está activo
     */
    public boolean esModoMantenimientoActivo() {
        Optional<ConfiguracionSistema> config = repositorioConfiguracion
                .findByClave(CLAVE_CONFIG_MANTENIMIENTO);
        
        return config.map(ConfiguracionSistema::getModoMantenimientoActivo)
                .orElse(false);
    }
    
    /**
     * Obtiene la información del modo mantenimiento
     */
    public ConfiguracionSistema obtenerInformacionMantenimiento() {
        return obtenerOCrearConfiguracion();
    }
    
    /**
     * Verifica si un usuario puede acceder durante el modo mantenimiento
     */
    public boolean puedeAccederEnMantenimiento(Usuario usuario) {
        if (!esModoMantenimientoActivo()) {
            return true; // Si no está en mantenimiento, todos pueden acceder
        }
        
        // Solo administradores, técnicos y desarrolladores pueden acceder
        return usuario.puedeAccederEnMantenimiento();
    }
    
    /**
     * Verifica el código de seguridad
     */
    private boolean verificarCodigoSeguridad(String codigoIngresado) {
        ConfiguracionSistema config = obtenerOCrearConfiguracion();
        
        // Si hay un código personalizado guardado, usarlo
        if (config.getCodigoSeguridadMantenimiento() != null) {
            return passwordEncoder.matches(codigoIngresado, config.getCodigoSeguridadMantenimiento());
        }
        
        // Si no, usar el código por defecto
        return codigoIngresado.equals(codigoSeguridadPorDefecto);
    }
    
    /**
     * Cambia el código de seguridad
     */
    @Transactional
    public void cambiarCodigoSeguridad(Usuario usuario, String codigoActual, String codigoNuevo) {
        // Solo administradores pueden cambiar el código
        if (!usuario.esAdministrador()) {
            throw new RuntimeException("Solo los administradores pueden cambiar el código de seguridad");
        }
        
        // Verificar código actual
        if (!verificarCodigoSeguridad(codigoActual)) {
            throw new RuntimeException("Código de seguridad actual inválido");
        }
        
        // Validar nuevo código
        if (codigoNuevo == null || codigoNuevo.length() < 8) {
            throw new RuntimeException("El nuevo código debe tener al menos 8 caracteres");
        }
        
        // Actualizar código
        ConfiguracionSistema config = obtenerOCrearConfiguracion();
        config.setCodigoSeguridadMantenimiento(passwordEncoder.encode(codigoNuevo));
        config.setUsuarioActualizacionId(usuario.getId());
        config.setFechaActualizacion(LocalDateTime.now());
        
        repositorioConfiguracion.save(config);
    }
    
    /**
     * Envía el código de seguridad al email de recuperación
     */
    public void enviarCodigoRecuperacion(Usuario usuario) {
        // Solo administradores pueden solicitar recuperación
        if (!usuario.esAdministrador()) {
            throw new RuntimeException("Solo los administradores pueden solicitar recuperación del código");
        }
        
        ConfiguracionSistema config = obtenerOCrearConfiguracion();
        
        // Determinar email de recuperación
        String emailRecuperacion = config.getEmailRecuperacionCodigo() != null ?
                config.getEmailRecuperacionCodigo() : emailRecuperacionPorDefecto;
        
        // Enviar email con el código
        servicioEmail.enviarEmailRecuperacionCodigoMantenimiento(
                emailRecuperacion,
                codigoSeguridadPorDefecto
        );
    }
    
    /**
     * Actualiza el email de recuperación del código
     */
    @Transactional
    public void actualizarEmailRecuperacion(Usuario usuario, String nuevoEmail) {
        // Solo administradores pueden cambiar el email
        if (!usuario.esAdministrador()) {
            throw new RuntimeException("Solo los administradores pueden actualizar el email de recuperación");
        }
        
        ConfiguracionSistema config = obtenerOCrearConfiguracion();
        config.setEmailRecuperacionCodigo(nuevoEmail);
        config.setUsuarioActualizacionId(usuario.getId());
        config.setFechaActualizacion(LocalDateTime.now());
        
        repositorioConfiguracion.save(config);
    }
    
    /**
     * Obtiene o crea la configuración de mantenimiento
     */
    private ConfiguracionSistema obtenerOCrearConfiguracion() {
        return repositorioConfiguracion.findByClave(CLAVE_CONFIG_MANTENIMIENTO)
                .orElseGet(() -> {
                    ConfiguracionSistema nuevaConfig = new ConfiguracionSistema();
                    nuevaConfig.setClave(CLAVE_CONFIG_MANTENIMIENTO);
                    nuevaConfig.setValor("false");
                    nuevaConfig.setDescripcion("Configuración del modo mantenimiento del sistema");
                    nuevaConfig.setModoMantenimientoActivo(false);
                    nuevaConfig.setCodigoSeguridadMantenimiento(passwordEncoder.encode(codigoSeguridadPorDefecto));
                    nuevaConfig.setEmailRecuperacionCodigo(emailRecuperacionPorDefecto);
                    return repositorioConfiguracion.save(nuevaConfig);
                });
    }
}
