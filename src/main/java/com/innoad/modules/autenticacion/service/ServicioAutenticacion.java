package com.innoad.modules.autenticacion.service;

import com.innoad.modules.autenticacion.dto.LoginRequest;
import com.innoad.modules.autenticacion.dto.LoginResponse;
import com.innoad.modules.autenticacion.dto.RegistroRequest;
import com.innoad.modules.autenticacion.dto.SolicitudRecuperarContrasena;
import com.innoad.modules.autenticacion.dto.SolicitudRestablecerContrasena;
import com.innoad.modules.auth.repository.RepositorioUsuario;
import com.innoad.modules.auth.domain.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio de Autenticación - Gestión de login, registro y JWT
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ServicioAutenticacion {

    private final PasswordEncoder passwordEncoder;
    private final RepositorioUsuario repositorioUsuario;
    private final JavaMailSender mailSender;

    @Value("${innoad.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    // Token expiración (horas)
    @Value("${innoad.recovery.token-expiration-hours:2}")
    private long tokenExpirationHours;

    /**
     * Authenticate user with email and password
     */
    public LoginResponse login(LoginRequest request) {
        log.info("Attempting login for user: {}", request.getEmail());
        
        // TODO: Implementar autenticación real con BD
        // Por ahora retorna una respuesta simulada
        
        return LoginResponse.builder()
                .token("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                .refreshToken("refresh_token_example")
                .usuarioId(1L)
                .nombreUsuario("Usuario Demo")
                .email(request.getEmail())
                .rol("USUARIO_NIVEL_1")
                .build();
    }

    /**
     * Register new user
     */
    public LoginResponse registrar(RegistroRequest request) {
        log.info("Attempting registration for user: {}", request.getEmail());
        
        // TODO: Implementar registro real con BD
        
        return LoginResponse.builder()
                .token("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                .refreshToken("refresh_token_example")
                .usuarioId(2L)
                .nombreUsuario(request.getNombre())
                .email(request.getEmail())
                .rol("USUARIO_NIVEL_1")
                .build();
    }

    /**
     * Refresh JWT token
     */
    public LoginResponse refreshToken(String refreshToken) {
        log.info("Attempting to refresh token");
        
        // TODO: Implementar refresh real
        
        return LoginResponse.builder()
                .token("new_jwt_token")
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Validate JWT token
     */
    public boolean validarToken(String token) {
        try {
            // TODO: Implementar validación real de JWT
            return !token.isEmpty();
        } catch (Exception e) {
            log.error("Error validating token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Alias para autenticar (llamado desde el controlador como autenticar)
     */
    public LoginResponse autenticar(LoginRequest request) {
        return login(request);
    }

    /**
     * Solicita recuperación de contraseña: genera token, guarda y envía email.
     */
    public void solicitarRecuperacion(SolicitudRecuperarContrasena solicitud) {
        log.info("Solicitud de recuperación para: {}", solicitud.getEmail());

        Optional<Usuario> usuarioOpt = repositorioUsuario.findByEmail(solicitud.getEmail());
        if (usuarioOpt.isEmpty()) {
            // No revelar existencia del email
            log.info("Email no encontrado (se responde OK para evitar enumeración): {}", solicitud.getEmail());
            return;
        }

        Usuario usuario = usuarioOpt.get();
        String token = UUID.randomUUID().toString();
        usuario.setTokenRecuperacion(token);
        usuario.setTokenRecuperacionExpiracion(LocalDateTime.now().plusHours(tokenExpirationHours));
        repositorioUsuario.save(usuario);

        // Enviar email con enlace de restablecimiento
        String link = frontendUrl + "/autenticacion/restablecer?token=" + token;
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(usuario.getEmail());
        msg.setSubject("Restablece tu contraseña - InnoAd");
        msg.setText("Hola " + usuario.getNombre() + ",\n\n" +
                "Hemos recibido una solicitud para restablecer tu contraseña. Haz clic en el enlace o pégalo en tu navegador:\n\n" +
                link + "\n\n" +
                "Si no solicitaste este cambio, puedes ignorar este correo.\n\n" +
                "Saludos,\nInnoAd");

        try {
            mailSender.send(msg);
        } catch (Exception e) {
            log.error("Error enviando email de recuperación: {}", e.getMessage());
        }
    }

    /**
     * Restablece la contraseña usando token válido
     */
    public void restablecerContrasena(SolicitudRestablecerContrasena solicitud) {
        log.info("Restablecer contraseña con token: {}", solicitud.getToken());

        Optional<Usuario> usuarioOpt = repositorioUsuario.findByTokenRecuperacion(solicitud.getToken());
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Token inválido");
        }

        Usuario usuario = usuarioOpt.get();
        if (usuario.getTokenRecuperacionExpiracion() == null || usuario.getTokenRecuperacionExpiracion().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token expirado");
        }

        usuario.setContrasena(passwordEncoder.encode(solicitud.getNuevaContrasena()));
        usuario.setTokenRecuperacion(null);
        usuario.setTokenRecuperacionExpiracion(null);
        repositorioUsuario.save(usuario);
    }

    /**
     * Logout (invalidate token)
     */
    public void logout(String token) {
        log.info("User logged out");
        // TODO: Implementar logout real (añadir a blacklist de tokens)
    }
}
