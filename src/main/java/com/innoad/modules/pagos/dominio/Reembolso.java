package com.innoad.modules.pagos.dominio;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad para gestionar reembolsos de pagos
 * Fase 5 - Sistema de reembolsos
 */
@Entity
@Table(name = "reembolsos", indexes = {
    @Index(name = "idx_pago_id", columnList = "pago_id"),
    @Index(name = "idx_usuario_id", columnList = "usuario_id"),
    @Index(name = "idx_estado", columnList = "estado")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reembolso implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Relación con pago
    @Column(name = "pago_id", nullable = false)
    private Long pagoId;
    
    // Información del reembolso
    @Column(name = "monto_reembolso", nullable = false, precision = 19, scale = 2)
    private BigDecimal montoReembolso;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoReembolso estado;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MotivoReembolso motivo;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;
    
    // Datos del usuario que solicita
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    
    @Column(nullable = false)
    private String usuarioEmail;
    
    // Información de procesamiento
    @Column(name = "id_transaccion_mp")
    private String idTransaccionMP;
    
    @Column(name = "estado_mp")
    private String estadoMP;
    
    @Column(columnDefinition = "TEXT")
    private String respuestaMP;
    
    // Datos administrativos
    @Column(name = "procesado_por")
    private String procesadoPor;
    
    @Column(name = "comentario_admin", columnDefinition = "TEXT")
    private String comentarioAdmin;
    
    @Column(name = "razon_rechazo", columnDefinition = "TEXT")
    private String razonRechazo;
    
    // Timestamps
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaSolicitud;
    
    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;
    
    @Column(name = "fecha_procesamiento")
    private LocalDateTime fechaProcesamiento;
    
    // Auditoría
    @Column(columnDefinition = "TEXT")
    private String respuestaJSON;
    
    @Column(name = "intentos_procesamiento")
    private Integer intentosProcesamiento;
    
    @PrePersist
    protected void onCreate() {
        this.fechaSolicitud = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        this.estado = EstadoReembolso.PENDIENTE;
        this.intentosProcesamiento = 0;
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    public enum EstadoReembolso {
        PENDIENTE,
        PROCESANDO,
        APROBADO,
        RECHAZADO,
        COMPLETADO,
        FALLIDO,
        CANCELADO
    }
    
    public enum MotivoReembolso {
        PAGO_DUPLICADO,
        PRODUCTO_NO_CONFORME,
        SERVICIO_NO_ENTREGADO,
        CAMBIO_DECISION,
        ERROR_CARGAR,
        OTRO
    }
}
