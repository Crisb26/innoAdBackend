package com.innoad.modules.pagos.dto;

import com.innoad.modules.pagos.dominio.Reembolso;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para transferencia de datos de Reembolsos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReembolsoDTO {
    
    private Long id;
    private Long pagoId;
    private BigDecimal montoReembolso;
    private String estado;
    private String motivo;
    private String descripcion;
    private Long usuarioId;
    private String usuarioEmail;
    private String procesadoPor;
    private String comentarioAdmin;
    private String razonRechazo;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaActualizacion;
    private LocalDateTime fechaProcesamiento;
    
    // Para respuestas simplificadas
    private PagoDTO pagoInfo;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PagoInfo {
        private Long id;
        private String referencia;
        private BigDecimal monto;
        private String planNombre;
        private LocalDateTime fechaPago;
    }
    
    // Convertir desde entidad
    public static ReembolsoDTO fromEntity(Reembolso reembolso) {
        return ReembolsoDTO.builder()
                .id(reembolso.getId())
                .pagoId(reembolso.getPagoId())
                .montoReembolso(reembolso.getMontoReembolso())
                .estado(reembolso.getEstado().toString())
                .motivo(reembolso.getMotivo().toString())
                .descripcion(reembolso.getDescripcion())
                .usuarioId(reembolso.getUsuarioId())
                .usuarioEmail(reembolso.getUsuarioEmail())
                .procesadoPor(reembolso.getProcesadoPor())
                .comentarioAdmin(reembolso.getComentarioAdmin())
                .razonRechazo(reembolso.getRazonRechazo())
                .fechaSolicitud(reembolso.getFechaSolicitud())
                .fechaActualizacion(reembolso.getFechaActualizacion())
                .fechaProcesamiento(reembolso.getFechaProcesamiento())
                .build();
    }
}
