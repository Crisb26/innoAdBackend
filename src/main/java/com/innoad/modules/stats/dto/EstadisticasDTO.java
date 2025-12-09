package com.innoad.modules.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO para estadísticas en tiempo real
 */
@Data\n@NoArgsConstructor\n@AllArgsConstructor\n@Builder\npublic class EstadisticasDTO {\n\n    // Estadísticas de Chat\n    private long totalMensajesChat;\n    private long totalUsuariosActivos;\n    private long totalSesionesChat;\n    private double tiempoPromedioRespuestaChat;  // en ms\n\n    // Estadísticas de IA\n    private long totalPreguntasIA;\n    private long totalRespuestasExitosas;\n    private long totalRespuestasError;\n    private double tasaExitoIA;  // porcentaje\n    private double tiempoPromedioPreguntaIA;  // en ms\n    private long tokensUsadosHoy;\n    private double costoHoyIA;  // en USD\n\n    // Estadísticas de Sistema\n    private int usuariosConectados;\n    private int usuariosActivos;\n    private double tasaDisponibilidadSistema;  // porcentaje\n    private double tiempoPromedioRespuestaSistema;  // en ms\n    private long totalSolicitudesProcessadas;\n    private long totalErrores;\n\n    // Metadata\n    private LocalDateTime ultimaActualizacion;\n    private String periodo;  // \"última_hora\", \"hoy\", \"esta_semana\", \"este_mes\"\n}
