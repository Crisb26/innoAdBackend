package com.innoad.modules.usuario.controller;

import com.innoad.dto.respuesta.RespuestaAPI;
import com.innoad.dto.solicitud.SolicitudActualizarPerfil;
import com.innoad.dto.respuesta.RespuestaLogin;
import com.innoad.modules.auth.domain.Usuario;
import com.innoad.modules.auth.service.ServicioAutenticacion;
import com.innoad.modules.auth.repository.RepositorioUsuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de Usuario - Gestión de perfil y datos de usuario
 * 📍 Endpoints: /api/usuario
 */
@RestController
@RequestMapping("/api/usuario")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "👤 Usuario", description = "Gestión de perfil y datos del usuario autenticado")
@SecurityRequirement(name = "BearerAuth")
@CrossOrigin(origins = {
    "http://localhost",
    "http://localhost:80",
    "http://localhost:4200",
    "http://localhost:8080",
    "http://127.0.0.1",
    "http://127.0.0.1:80"
})
public class ControladorUsuario {

    private final ServicioAutenticacion servicioAutenticacion;
    private final RepositorioUsuario repositorioUsuario;

    /**
     * Obtiene el perfil del usuario autenticado
     * 📍 GET /api/usuario/perfil
     */
    @GetMapping("/perfil")
    @Operation(
        summary = "Obtener perfil del usuario",
        description = "Obtiene la información del perfil del usuario autenticado"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil obtenido exitosamente"),
        @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    public ResponseEntity<RespuestaAPI<RespuestaLogin.UsuarioLogin>> obtenerPerfil() {
        try {
            // Obtener usuario del contexto de seguridad
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            
            if (username == null || username.isEmpty()) {
                return ResponseEntity.status(401).body(
                    RespuestaAPI.<RespuestaLogin.UsuarioLogin>builder()
                        .exitoso(false)
                        .mensaje("Usuario no autenticado")
                        .build()
                );
            }

            // Buscar usuario en BD
            Usuario usuario = repositorioUsuario.findByNombreUsuario(username)
                    .or(() -> repositorioUsuario.findByEmail(username))
                    .orElse(null);
                    
            if (usuario == null) {
                return ResponseEntity.status(401).body(
                    RespuestaAPI.<RespuestaLogin.UsuarioLogin>builder()
                        .exitoso(false)
                        .mensaje("Usuario no encontrado")
                        .build()
                );
            }

            // Construir respuesta del perfil
            String rolNombre = switch (usuario.getRol()) {
                case ADMIN -> "ADMIN";
                case TECNICO -> "TECNICO";
                case USUARIO -> "USUARIO";
                default -> "USUARIO";
            };

            RespuestaLogin.UsuarioLogin perfilUsuario = RespuestaLogin.UsuarioLogin.builder()
                    .id(usuario.getId())
                    .nombreUsuario(usuario.getNombreUsuario())
                    .email(usuario.getEmail())
                    .nombreCompleto(usuario.getNombreCompleto())
                    .telefono(usuario.getTelefono())
                    .direccion(usuario.getDireccion())
                    .cedula(usuario.getCedula())
                    .avatarUrl(usuario.getFotoPerfil())
                    .rol(RespuestaLogin.RolSimple.builder().nombre(rolNombre).build())
                    .build();

            return ResponseEntity.ok(
                    RespuestaAPI.<RespuestaLogin.UsuarioLogin>builder()
                            .exitoso(true)
                            .mensaje("Perfil obtenido exitosamente")
                            .datos(perfilUsuario)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error al obtener perfil: ", e);
            return ResponseEntity.status(500).body(
                    RespuestaAPI.<RespuestaLogin.UsuarioLogin>builder()
                            .exitoso(false)
                            .mensaje("Error al obtener perfil: " + e.getMessage())
                            .build()
            );
        }
    }

    /**
     * Actualiza el perfil del usuario autenticado
     * 📍 PUT /api/usuario/perfil
     */
    @PutMapping("/perfil")
    @Operation(
        summary = "Actualizar perfil del usuario",
        description = "Actualiza la información del perfil del usuario autenticado"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil actualizado exitosamente"),
        @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<RespuestaAPI<RespuestaLogin.UsuarioLogin>> actualizarPerfil(
            @Valid @RequestBody SolicitudActualizarPerfil solicitud,
            @AuthenticationPrincipal Usuario usuario
    ) {
        try {
            if (usuario == null) {
                return ResponseEntity.status(401).body(
                    RespuestaAPI.<RespuestaLogin.UsuarioLogin>builder()
                        .exitoso(false)
                        .mensaje("Usuario no autenticado")
                        .build()
                );
            }

            RespuestaLogin.UsuarioLogin usuarioActualizado = servicioAutenticacion.actualizarPerfil(solicitud);

            return ResponseEntity.ok(
                    RespuestaAPI.<RespuestaLogin.UsuarioLogin>builder()
                            .exitoso(true)
                            .mensaje("Perfil actualizado exitosamente")
                            .datos(usuarioActualizado)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(400).body(
                    RespuestaAPI.<RespuestaLogin.UsuarioLogin>builder()
                            .exitoso(false)
                            .mensaje("Error al actualizar perfil: " + e.getMessage())
                            .build()
            );
        }
    }
}
