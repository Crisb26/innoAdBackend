package com.innoad.dto.solicitud;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitar un código de verificación
 * Se usa tanto para registro como para recuperación
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudSolicitarCodigoVerificacion {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email válido")
    private String email;

    @NotBlank(message = "El tipo es obligatorio")
    private String tipo; // REGISTRO o RECUPERACION
}
