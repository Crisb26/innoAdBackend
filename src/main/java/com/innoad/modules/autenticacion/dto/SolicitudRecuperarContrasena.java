package com.innoad.modules.autenticacion.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SolicitudRecuperarContrasena {

    @NotBlank
    @Email
    private String email;
}
