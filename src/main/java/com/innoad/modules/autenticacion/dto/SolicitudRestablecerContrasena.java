package com.innoad.modules.autenticacion.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SolicitudRestablecerContrasena {

    @NotBlank
    private String token;

    @NotBlank
    private String nuevaContrasena;
}
