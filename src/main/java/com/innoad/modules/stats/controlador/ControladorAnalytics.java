package com.innoad.modules.stats.controlador;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.innoad.modules.stats.dto.EstadisticasDTO;
import com.innoad.modules.stats.servicio.ServicioAnalytics;

/**
 * Controlador para endpoints de Analytics y estadísticas
 * Acceso restringido a ADMIN, SUPER_ADMIN y OPERATOR
 */
@RestController
@RequestMapping(\"/api/analytics\")
@RequiredArgsConstructor
@Slf4j
public class ControladorAnalytics {

    private final ServicioAnalytics servicioAnalytics;

    /**
     * GET /api/analytics/ultima-hora
     * Obtiene estadísticas del último minuto
     */
    @GetMapping(\"/ultima-hora\")
    @PreAuthorize(\"hasAnyRole('ADMIN', 'SUPER_ADMIN', 'OPERATOR')\")
    public ResponseEntity<EstadisticasDTO> obtenerUltimaHora() {
        try {
            log.info(\"Obteniendo estadísticas última hora\");
            EstadisticasDTO estadisticas = servicioAnalytics.obtenerEstadisticasUltimaHora();
            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            log.error(\"Error obteniendo estadísticas última hora: {}\", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/analytics/hoy
     * Obtiene estadísticas del día completo
     */
    @GetMapping(\"/hoy\")
    @PreAuthorize(\"hasAnyRole('ADMIN', 'SUPER_ADMIN', 'OPERATOR')\")
    public ResponseEntity<EstadisticasDTO> obtenerHoy() {
        try {
            log.info(\"Obteniendo estadísticas de hoy\");
            EstadisticasDTO estadisticas = servicioAnalytics.obtenerEstadisticasHoy();
            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            log.error(\"Error obteniendo estadísticas de hoy: {}\", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/analytics/semanal
     * Obtiene estadísticas de la semana
     */
    @GetMapping(\"/semanal\")
    @PreAuthorize(\"hasAnyRole('ADMIN', 'SUPER_ADMIN', 'OPERATOR')\")
    public ResponseEntity<EstadisticasDTO> obtenerSemanal() {
        try {
            log.info(\"Obteniendo estadísticas semanales\");
            EstadisticasDTO estadisticas = servicioAnalytics.obtenerEstadisticasSemanales();
            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            log.error(\"Error obteniendo estadísticas semanales: {}\", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * POST /api/analytics/resetear
     * Resetea los contadores (SOLO SUPER_ADMIN)
     */
    @PostMapping(\"/resetear\")
    @PreAuthorize(\"hasRole('SUPER_ADMIN')\")
    public ResponseEntity<String> resetearContadores() {
        try {
            log.warn(\"Reseteando contadores de analytics\");
            servicioAnalytics.resetearContadores();
            return ResponseEntity.ok(\"Contadores reseteados correctamente\");
        } catch (Exception e) {
            log.error(\"Error reseteando contadores: {}\", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
