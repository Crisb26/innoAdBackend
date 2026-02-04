package com.innoad.modules.pagos.servicio;

import com.innoad.modules.pagos.dominio.Pago;
import com.innoad.modules.pagos.dominio.Reembolso;
import com.innoad.modules.pagos.dto.ReembolsoDTO;
import com.innoad.modules.pagos.repositorio.RepositorioPagos;
import com.innoad.modules.pagos.repositorio.RepositorioReembolsos;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de Reembolsos
 * Gestiona solicitudes de devolución de pagos
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ServicioReembolsos {
    
    private final RepositorioReembolsos repositorioReembolsos;
    private final RepositorioPagos repositorioPagos;
    private final ServicioPagos servicioPagos;
    
    /**
     * Solicitar reembolso para un pago
     */
    public ReembolsoDTO solicitarReembolso(
            Long pagoId,
            Long usuarioId,
            String email,
            Reembolso.MotivoReembolso motivo,
            String descripcion,
            BigDecimal monto
    ) {
        log.info("Solicitud de reembolso para pago: {} por usuario: {}", pagoId, usuarioId);
        
        // Validar pago existe y pertenece al usuario
        Pago pago = repositorioPagos.findById(pagoId)
                .orElseThrow(() -> new IllegalArgumentException("Pago no encontrado"));
        
        if (!pago.getUsuarioId().equals(usuarioId)) {
            throw new IllegalArgumentException("El pago no pertenece al usuario autenticado");
        }
        
        // Validar estado del pago
        if (!pago.getEstado().equals(Pago.EstadoPago.APROBADO)) {
            throw new IllegalArgumentException("Solo se pueden reembolsar pagos aprobados");
        }
        
        // Validar monto a reembolsar
        BigDecimal montoDisponible = calcularMontoDisponibleReembolso(pago);
        if (monto.compareTo(BigDecimal.ZERO) <= 0 || monto.compareTo(montoDisponible) > 0) {
            throw new IllegalArgumentException("Monto de reembolso inválido");
        }
        
        // Crear reembolso
        Reembolso reembolso = Reembolso.builder()
                .pagoId(pagoId)
                .montoReembolso(monto)
                .estado(Reembolso.EstadoReembolso.PENDIENTE)
                .motivo(motivo)
                .descripcion(descripcion)
                .usuarioId(usuarioId)
                .usuarioEmail(email)
                .build();
        
        Reembolso reembolsoGuardado = repositorioReembolsos.save(reembolso);
        
        log.info("Reembolso creado: {} por monto: {}", reembolsoGuardado.getId(), monto);
        
        return ReembolsoDTO.fromEntity(reembolsoGuardado);
    }
    
    /**
     * Obtener reembolso por ID
     */
    public ReembolsoDTO obtenerReembolso(Long id) {
        Reembolso reembolso = repositorioReembolsos.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reembolso no encontrado"));
        
        return ReembolsoDTO.fromEntity(reembolso);
    }
    
    /**
     * Listar reembolsos del usuario
     */
    public Page<ReembolsoDTO> listarReembolsosUsuario(Long usuarioId, Pageable pageable) {
        Page<Reembolso> reembolsos = repositorioReembolsos.findByUsuarioId(usuarioId, pageable);
        return reembolsos.map(ReembolsoDTO::fromEntity);
    }
    
    /**
     * Listar reembolsos por estado (ADMINISTRADOR)
     */
    public Page<ReembolsoDTO> listarReembolsosPorEstado(
            Reembolso.EstadoReembolso estado,
            Pageable pageable
    ) {
        Page<Reembolso> reembolsos = repositorioReembolsos.findByEstado(estado, pageable);
        return reembolsos.map(ReembolsoDTO::fromEntity);
    }
    
    /**
     * Obtener reembolsos pendientes
     */
    public Page<ReembolsoDTO> obtenerReembolsosPendientes(Pageable pageable) {
        Page<Reembolso> reembolsos = repositorioReembolsos
                .findByEstadoOrderByFechaSolicitudDesc(
                    Reembolso.EstadoReembolso.PENDIENTE, 
                    pageable
                );
        return reembolsos.map(ReembolsoDTO::fromEntity);
    }
    
    /**
     * Procesar reembolso (aprobar)
     */
    public ReembolsoDTO procesarReembolso(
            Long reembolsoId,
            String procesadoPor,
            String comentario
    ) {
        log.info("Procesando reembolso: {} por: {}", reembolsoId, procesadoPor);
        
        Reembolso reembolso = repositorioReembolsos.findById(reembolsoId)
                .orElseThrow(() -> new IllegalArgumentException("Reembolso no encontrado"));
        
        if (!reembolso.getEstado().equals(Reembolso.EstadoReembolso.PENDIENTE)) {
            throw new IllegalArgumentException("El reembolso ya fue procesado");
        }
        
        reembolso.setEstado(Reembolso.EstadoReembolso.PROCESANDO);
        reembolso.setProcesadoPor(procesadoPor);
        reembolso.setComentarioAdmin(comentario);
        reembolso.setIntentosProcesamiento(reembolso.getIntentosProcesamiento() + 1);
        
        // TODO: Integrar con Mercado Pago para procesar el reembolso
        // MercadoPagoConfig.setAccessToken(mercadoPagoAccessToken);
        // RefundClient client = new RefundClient();
        // client.create(pagoMP, refundRequest);
        
        // Por ahora, marcar como completado
        reembolso.setEstado(Reembolso.EstadoReembolso.COMPLETADO);
        reembolso.setFechaProcesamiento(java.time.LocalDateTime.now());
        
        // Actualizar pago
        Pago pago = repositorioPagos.findById(reembolso.getPagoId()).orElse(null);
        if (pago != null) {
            BigDecimal montoActualReembolsado = pago.getMontoReembolsado() != null ? 
                pago.getMontoReembolsado() : BigDecimal.ZERO;
            pago.setMontoReembolsado(montoActualReembolsado.add(reembolso.getMontoReembolso()));
            pago.setFechaReembolso(java.time.LocalDateTime.now());
            pago.setMotivoReembolso(reembolso.getMotivo().toString());
            
            if (pago.getMontoReembolsado().compareTo(pago.getMonto()) >= 0) {
                pago.setEstado(Pago.EstadoPago.REEMBOLSADO);
            }
            
            repositorioPagos.save(pago);
        }
        
        Reembolso reembolsoActualizado = repositorioReembolsos.save(reembolso);
        
        log.info("Reembolso procesado: {}", reembolsoId);
        
        return ReembolsoDTO.fromEntity(reembolsoActualizado);
    }
    
    /**
     * Rechazar reembolso
     */
    public ReembolsoDTO rechazarReembolso(
            Long reembolsoId,
            String razonRechazo,
            String procesadoPor
    ) {
        log.info("Rechazando reembolso: {}", reembolsoId);
        
        Reembolso reembolso = repositorioReembolsos.findById(reembolsoId)
                .orElseThrow(() -> new IllegalArgumentException("Reembolso no encontrado"));
        
        if (!reembolso.getEstado().equals(Reembolso.EstadoReembolso.PENDIENTE)) {
            throw new IllegalArgumentException("No se puede rechazar un reembolso que ya fue procesado");
        }
        
        reembolso.setEstado(Reembolso.EstadoReembolso.RECHAZADO);
        reembolso.setRazonRechazo(razonRechazo);
        reembolso.setProcesadoPor(procesadoPor);
        
        Reembolso reembolsoActualizado = repositorioReembolsos.save(reembolso);
        
        log.info("Reembolso rechazado: {}", reembolsoId);
        
        return ReembolsoDTO.fromEntity(reembolsoActualizado);
    }
    
    /**
     * Cancelar reembolso
     */
    public ReembolsoDTO cancelarReembolso(Long reembolsoId) {
        Reembolso reembolso = repositorioReembolsos.findById(reembolsoId)
                .orElseThrow(() -> new IllegalArgumentException("Reembolso no encontrado"));
        
        if (reembolso.getEstado().equals(Reembolso.EstadoReembolso.COMPLETADO) ||
            reembolso.getEstado().equals(Reembolso.EstadoReembolso.CANCELADO)) {
            throw new IllegalArgumentException("No se puede cancelar un reembolso en ese estado");
        }
        
        reembolso.setEstado(Reembolso.EstadoReembolso.CANCELADO);
        
        Reembolso reembolsoActualizado = repositorioReembolsos.save(reembolso);
        
        return ReembolsoDTO.fromEntity(reembolsoActualizado);
    }
    
    /**
     * Obtener reembolsos de un pago
     */
    public List<ReembolsoDTO> obtenerReembolsosPago(Long pagoId) {
        return repositorioReembolsos.findByPagoId(pagoId)
                .stream()
                .map(ReembolsoDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Calcular monto disponible para reembolsar
     */
    public BigDecimal calcularMontoDisponibleReembolso(Pago pago) {
        BigDecimal montoReembolsado = pago.getMontoReembolsado() != null ? 
            pago.getMontoReembolsado() : BigDecimal.ZERO;
        
        return pago.getMonto().subtract(montoReembolsado);
    }
    
    /**
     * Obtener estadísticas de reembolsos
     */
    public ReembolsoEstadisticas obtenerEstadisticas() {
        long totalSolicitudes = repositorioReembolsos.count();
        long pendientes = repositorioReembolsos.countByEstado(Reembolso.EstadoReembolso.PENDIENTE);
        long procesadas = repositorioReembolsos.countByEstado(Reembolso.EstadoReembolso.COMPLETADO);
        long rechazadas = repositorioReembolsos.countByEstado(Reembolso.EstadoReembolso.RECHAZADO);
        
        return ReembolsoEstadisticas.builder()
                .totalSolicitudes(totalSolicitudes)
                .pendientes(pendientes)
                .procesadas(procesadas)
                .rechazadas(rechazadas)
                .build();
    }
    
    /**
     * DTO para estadísticas
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.Builder
    public static class ReembolsoEstadisticas {
        private long totalSolicitudes;
        private long pendientes;
        private long procesadas;
        private long rechazadas;
    }
}
