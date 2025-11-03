package com.innoad.servicio;

import com.innoad.dto.solicitud.SolicitudLogin;
import com.innoad.dto.solicitud.SolicitudRegistro;
import com.innoad.dto.respuesta.RespuestaAutenticacion;
import com.innoad.modelo.entidades.Usuario;
import com.innoad.modelo.enumeraciones.RolUsuario;
import com.innoad.repositorio.RepositorioUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Servicio de autenticación para registro, login y recuperación de contraseña.
 */
@Service
@RequiredArgsConstructor
public class ServicioAutenticacion {
    
    private final RepositorioUsuario repositorioUsuario;
    private final PasswordEncoder passwordEncoder;
    private final ServicioJWT servicioJWT;
    private final AuthenticationManager authenticationManager;
    private final ServicioEmail servicioEmail;
    
    @Value("${innoad.max-users}")
    private Integer maxUsuarios;
    
    /**
     * Registra un nuevo usuario en el sistema
     */
    @Transactional
    public RespuestaAutenticacion registrar(SolicitudRegistro solicitud) {
        // Verificar límite de usuarios
        Long usuariosActivos = repositorioUsuario.contarUsuariosActivos();
        if (usuariosActivos >= maxUsuarios) {
            throw new RuntimeException("Se ha alcanzado el límite máximo de usuarios del sistema");
        }
        
        // Verificar si el usuario ya existe
        if (repositorioUsuario.existsByNombreUsuario(solicitud.getNombreUsuario())) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }
        
        if (repositorioUsuario.existsByEmail(solicitud.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        
        // Crear el usuario
        var usuario = Usuario.builder()
                .nombre(solicitud.getNombre())
                .apellido(solicitud.getApellido())
                .email(solicitud.getEmail())
                .nombreUsuario(solicitud.getNombreUsuario())
                .contrasena(passwordEncoder.encode(solicitud.getContrasena()))
                .rol(solicitud.getRol() != null ? solicitud.getRol() : RolUsuario.USUARIO)
                .telefono(solicitud.getTelefono())
                .empresa(solicitud.getEmpresa())
                .cargo(solicitud.getCargo())
                .activo(true)
                .verificado(false)
                .intentosFallidos(0)
                .build();
        
        // Generar token de verificación
        String tokenVerificacion = UUID.randomUUID().toString();
        usuario.setTokenVerificacion(tokenVerificacion);
        usuario.setTokenVerificacionExpiracion(LocalDateTime.now().plusHours(24));
        
        // Guardar usuario
        usuario = repositorioUsuario.save(usuario);
        
        // Enviar email de verificación
        try {
            servicioEmail.enviarEmailVerificacion(usuario.getEmail(), tokenVerificacion);
        } catch (Exception e) {
            // Log error pero no fallar el registro
            System.err.println("Error al enviar email de verificación: " + e.getMessage());
        }
        
        // Generar JWT
        var jwtToken = servicioJWT.generarToken(usuario);
        
        return RespuestaAutenticacion.builder()
                .token(jwtToken)
                .tipoToken("Bearer")
                .id(usuario.getId())
                .nombreUsuario(usuario.getNombreUsuario())
                .email(usuario.getEmail())
                .nombreCompleto(usuario.getNombreCompleto())
                .rol(usuario.getRol())
                .verificado(usuario.getVerificado())
                .mensaje("Usuario registrado exitosamente. Por favor verifica tu email.")
                .build();
    }
    
    /**
     * Autentica un usuario existente
     */
    @Transactional
    public RespuestaAutenticacion autenticar(SolicitudLogin solicitud) {
        // Buscar usuario por nombre de usuario o email
        Usuario usuario = repositorioUsuario.findByNombreUsuario(solicitud.getNombreUsuarioOEmail())
                .or(() -> repositorioUsuario.findByEmail(solicitud.getNombreUsuarioOEmail()))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Verificar si la cuenta está bloqueada
        if (!usuario.isAccountNonLocked()) {
            throw new RuntimeException("Cuenta bloqueada por múltiples intentos fallidos. Intenta más tarde.");
        }
        
        try {
            // Autenticar
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            usuario.getNombreUsuario(),
                            solicitud.getContrasena()
                    )
            );
            
            // Reset intentos fallidos
            usuario.setIntentosFallidos(0);
            usuario.setFechaBloqueo(null);
            usuario.setUltimoAcceso(LocalDateTime.now());
            repositorioUsuario.save(usuario);
            
        } catch (Exception e) {
            // Incrementar intentos fallidos
            usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);
            
            // Bloquear cuenta después de 5 intentos
            if (usuario.getIntentosFallidos() >= 5) {
                usuario.setFechaBloqueo(LocalDateTime.now());
            }
            
            repositorioUsuario.save(usuario);
            throw new RuntimeException("Credenciales inválidas");
        }
        
        // Generar JWT
        var jwtToken = servicioJWT.generarToken(usuario);
        
        return RespuestaAutenticacion.builder()
                .token(jwtToken)
                .tipoToken("Bearer")
                .id(usuario.getId())
                .nombreUsuario(usuario.getNombreUsuario())
                .email(usuario.getEmail())
                .nombreCompleto(usuario.getNombreCompleto())
                .rol(usuario.getRol())
                .verificado(usuario.getVerificado())
                .mensaje("Autenticación exitosa")
                .build();
    }
    
    /**
     * Verifica el email de un usuario
     */
    @Transactional
    public void verificarEmail(String token) {
        Usuario usuario = repositorioUsuario.findByTokenVerificacion(token)
                .orElseThrow(() -> new RuntimeException("Token de verificación inválido"));
        
        if (usuario.getTokenVerificacionExpiracion().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token de verificación expirado");
        }
        
        usuario.setVerificado(true);
        usuario.setTokenVerificacion(null);
        usuario.setTokenVerificacionExpiracion(null);
        repositorioUsuario.save(usuario);
    }
    
    /**
     * Solicita recuperación de contraseña
     */
    @Transactional
    public void solicitarRecuperacionContrasena(String email) {
        Usuario usuario = repositorioUsuario.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        String tokenRecuperacion = UUID.randomUUID().toString();
        usuario.setTokenRecuperacion(tokenRecuperacion);
        usuario.setTokenRecuperacionExpiracion(LocalDateTime.now().plusHours(2));
        repositorioUsuario.save(usuario);
        
        // Enviar email de recuperación
        servicioEmail.enviarEmailRecuperacion(email, tokenRecuperacion);
    }
    
    /**
     * Restablece la contraseña de un usuario
     */
    @Transactional
    public void restablecerContrasena(String token, String nuevaContrasena) {
        Usuario usuario = repositorioUsuario.findByTokenRecuperacion(token)
                .orElseThrow(() -> new RuntimeException("Token de recuperación inválido"));
        
        if (usuario.getTokenRecuperacionExpiracion().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token de recuperación expirado");
        }
        
        usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));
        usuario.setTokenRecuperacion(null);
        usuario.setTokenRecuperacionExpiracion(null);
        repositorioUsuario.save(usuario);
    }
}
