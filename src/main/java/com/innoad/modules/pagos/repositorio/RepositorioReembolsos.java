package com.innoad.modules.pagos.repositorio;

import com.innoad.modules.pagos.dominio.Reembolso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestión de Reembolsos
 */
@Repository
public interface RepositorioReembolsos extends JpaRepository<Reembolso, Long> {
    
    // Buscar reembolsos por pago
    List<Reembolso> findByPagoId(Long pagoId);
    
    // Listar reembolsos del usuario
    Page<Reembolso> findByUsuarioId(Long usuarioId, Pageable pageable);
    
    // Listar por estado
    Page<Reembolso> findByEstado(Reembolso.EstadoReembolso estado, Pageable pageable);
    
    // Listar reembolsos pendientes
    Page<Reembolso> findByEstadoOrderByFechaSolicitudDesc(
        Reembolso.EstadoReembolso estado, 
        Pageable pageable
    );
    
    // Buscar reembolso por usuario y pago
    Optional<Reembolso> findByPagoIdAndUsuarioId(Long pagoId, Long usuarioId);
    
    // Contar reembolsos por estado
    @Query("SELECT COUNT(r) FROM Reembolso r WHERE r.estado = :estado")
    Long countByEstado(@Param("estado") Reembolso.EstadoReembolso estado);
    
    // Obtener monto total reembolsado por usuario
    @Query("SELECT COALESCE(SUM(r.montoReembolso), 0) FROM Reembolso r " +
           "WHERE r.usuarioId = :usuarioId AND r.estado = 'COMPLETADO'")
    BigDecimal sumMontoReembolsadoPorUsuario(@Param("usuarioId") Long usuarioId);
    
    // Reembolsos entre fechas
    Page<Reembolso> findByFechaSolicitudBetween(
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        Pageable pageable
    );
    
    // Reembolsos por motivo
    Page<Reembolso> findByMotivo(Reembolso.MotivoReembolso motivo, Pageable pageable);
    
    // Reembolsos procesados por administrador
    Page<Reembolso> findByProcesadoPor(String procesadoPor, Pageable pageable);
}
