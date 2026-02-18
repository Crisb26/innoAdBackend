package com.innoad.hardware.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComandoDTO {
    private Long id;
    private Long dispositivoId;
    private String tipo;
    private String descripcion;
    private Map<String, Object> parametros;
    private String estado;
    private String respuesta;
}
