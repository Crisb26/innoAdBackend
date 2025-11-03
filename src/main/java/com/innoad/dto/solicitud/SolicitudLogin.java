package com.innoad.dto.solicitud;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de inicio de sesión
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudLogin {
    
    @NotBlank(message = "El nombre de usuario o email es obligatorio")
    private String nombreUsuarioOEmail;
    
    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;
}
