package com.innoad.controlador;

import com.innoad.dto.solicitud.SolicitudLogin;
import com.innoad.dto.solicitud.SolicitudRegistro;
import com.innoad.dto.respuesta.RespuestaAutenticacion;
import com.innoad.dto.respuesta.RespuestaAPI;
import com.innoad.dto.respuesta.RespuestaLogin;
import com.innoad.dto.solicitud.SolicitudRefreshToken;
import com.innoad.servicio.ServicioAutenticacion;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para la autenticación de usuarios.
 * Maneja registro, login, verificación y recuperación de contraseña.
 */
@RestController
@RequestMapping("/api/autenticacion")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "https://innoad.com"})
public class ControladorAutenticacion {
    
    private final ServicioAutenticacion servicioAutenticacion;
    
    /**
     * Endpoint para registrar un nuevo usuario
     */
    @PostMapping("/registrar")
    public ResponseEntity<RespuestaAutenticacion> registrar(
            @Valid @RequestBody SolicitudRegistro solicitud
    ) {
        try {
            RespuestaAutenticacion respuesta = servicioAutenticacion.registrar(solicitud);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(RespuestaAutenticacion.builder()
                            .mensaje("Error al registrar: " + e.getMessage())
                            .build());
        }
    }
    
    /**
     * Endpoint para iniciar sesión
     */
    @PostMapping("/login")
    public ResponseEntity<RespuestaAutenticacion> login(
            @Valid @RequestBody SolicitudLogin solicitud
    ) {
        try {
            RespuestaAutenticacion respuesta = servicioAutenticacion.autenticar(solicitud);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(RespuestaAutenticacion.builder()
                            .mensaje("Error al autenticar: " + e.getMessage())
                            .build());
        }
    }
    
    /**
     * Endpoint para verificar el email del usuario
     */
    @GetMapping("/verificar-email")
    public ResponseEntity<Map<String, String>> verificarEmail(
            @RequestParam String token
    ) {
        try {
            servicioAutenticacion.verificarEmail(token);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Email verificado exitosamente",
                    "exito", "true"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "mensaje", "Error al verificar email: " + e.getMessage(),
                            "exito", "false"
                    ));
        }
    }
    
    /**
     * Endpoint para solicitar recuperación de contraseña
     */
    @PostMapping("/recuperar-contrasena")
    public ResponseEntity<Map<String, String>> recuperarContrasena(
            @RequestBody Map<String, String> solicitud
    ) {
        try {
            servicioAutenticacion.solicitarRecuperacionContrasena(solicitud.get("email"));
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Email de recuperación enviado exitosamente",
                    "exito", "true"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "mensaje", "Error: " + e.getMessage(),
                            "exito", "false"
                    ));
        }
    }
    
    /**
     * Endpoint para restablecer la contraseña
     */
    @PostMapping("/restablecer-contrasena")
    public ResponseEntity<Map<String, String>> restablecerContrasena(
            @RequestBody Map<String, String> solicitud
    ) {
        try {
            servicioAutenticacion.restablecerContrasena(
                    solicitud.get("token"),
                    solicitud.get("nuevaContrasena")
            );
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Contraseña restablecida exitosamente",
                    "exito", "true"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "mensaje", "Error: " + e.getMessage(),
                            "exito", "false"
                    ));
        }
    }

        // ===== Endpoints v1 para Frontend =====

        /**
         * Iniciar sesión (contrato esperado por frontend)
         */
        @PostMapping("/iniciar-sesion")
        public ResponseEntity<RespuestaAPI<RespuestaLogin>> iniciarSesion(
                        @Valid @RequestBody SolicitudLogin solicitud
        ) {
                try {
                        var respuesta = servicioAutenticacion.autenticarV1(solicitud);
                        return ResponseEntity.ok(respuesta);
                } catch (Exception e) {
                        return ResponseEntity.badRequest().body(
                                        RespuestaAPI.<RespuestaLogin>builder()
                                                        .exitoso(false)
                                                        .mensaje("Error al autenticar: " + e.getMessage())
                                                        .build()
                        );
                }
        }

        /**
         * Refrescar token de acceso
         */
        @PostMapping("/refrescar-token")
        public ResponseEntity<RespuestaAPI<RespuestaLogin>> refrescarToken(
                        @Valid @RequestBody SolicitudRefreshToken solicitud
        ) {
                try {
                        var respuesta = servicioAutenticacion.refrescarToken(solicitud.getTokenActualizacion());
                        return ResponseEntity.ok(respuesta);
                } catch (Exception e) {
                        return ResponseEntity.badRequest().body(
                                        RespuestaAPI.<RespuestaLogin>builder()
                                                        .exitoso(false)
                                                        .mensaje("Error al refrescar token: " + e.getMessage())
                                                        .build()
                        );
                }
        }

        /**
         * Cerrar sesión (stateless: es un no-op, el frontend simplemente olvida tokens)
         */
        @PostMapping("/cerrar-sesion")
        public ResponseEntity<RespuestaAPI<Void>> cerrarSesion() {
                return ResponseEntity.ok(
                                RespuestaAPI.<Void>builder()
                                                .exitoso(true)
                                                .mensaje("Sesión cerrada")
                                                .build()
                );
        }
}
