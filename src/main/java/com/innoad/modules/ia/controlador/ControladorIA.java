package com.innoad.modules.ia.controlador;

import com.innoad.modules.ia.dto.DTOPromptIAPorRol;
import com.innoad.modules.ia.dto.DTORegistroInteraccionIA;
import com.innoad.modules.ia.dto.DTORespuestaIA;
import com.innoad.modules.ia.servicio.ServicioIA;
import com.innoad.modules.ia.servicio.ServicioOpenAI;
import com.innoad.modules.ia.servicio.ServicioOpenAI.RespuestaOpenAI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ia")
@RequiredArgsConstructor
@Slf4j
public class ControladorIA {

    private final ServicioIA servicioIA;
    private final ServicioOpenAI servicioOpenAI;

    @PostMapping("/consultar")
    @PreAuthorize("hasAnyRole('ROLE_USUARIO', 'ROLE_TECNICO', 'ROLE_DEVELOPER', 'ROLE_ADMIN')")
    public ResponseEntity<DTORespuestaIA> consultarIA(
            @RequestParam Long idUsuario,
            @RequestParam String rol,
            @RequestBody String pregunta) {
        try {
            log.info("Consultando IA para usuario {} con rol {}", idUsuario, rol);

            // Registrar la interacción
            var registroInteraccion = servicioIA.registrarInteraccion(idUsuario, pregunta);

            // Obtener el prompt para el rol
            var prompt = servicioIA.obtenerPromptParaRol(rol);

            // Verificar horario de atención
            boolean enHorario = servicioIA.estaEnHorarioAtencion();
            if (!enHorario) {
                log.warn("Consulta fuera de horario de atención");
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(DTORespuestaIA.error("El servicio no está disponible en este momento", "503"));
            }

            // Construir contexto
            String contexto = servicioIA.construirContextoIA();

            // Llamar a OpenAI
            RespuestaOpenAI respuestaOpenAI = servicioOpenAI.llamarAPI(pregunta, prompt, contexto);

            if (!respuestaOpenAI.getExito()) {
                servicioIA.registrarErrorInteraccion(registroInteraccion.getId(), respuestaOpenAI.getError());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(DTORespuestaIA.error("Error al procesar la solicitud", "500"));
            }

            // Actualizar registro con respuesta
            var registroActualizado = servicioIA.actualizarRegistroInteraccion(
                    registroInteraccion.getId(),
                    respuestaOpenAI.getRespuesta(),
                    respuestaOpenAI.getTokensUtilizados(),
                    respuestaOpenAI.getTiempoRespuesta()
            );

            DTORegistroInteraccionIA dtoRespuesta = DTORegistroInteraccionIA.builder()
                    .id(registroActualizado.getId())
                    .respuesta(registroActualizado.getRespuesta())
                    .tokensUtilizados(registroActualizado.getTokensUtilizados())
                    .tiempoRespuesta(registroActualizado.getTiempoRespuesta())
                    .estado(registroActualizado.getEstado())
                    .fechaCompletacion(registroActualizado.getFechaCompletacion())
                    .build();

            return ResponseEntity.ok(DTORespuestaIA.exitoso(dtoRespuesta, "Consulta procesada exitosamente"));
        } catch (Exception e) {
            log.error("Error al consultar IA: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DTORespuestaIA.error("Error interno del servidor", "500"));
        }
    }

    @GetMapping("/historial/{idUsuario}")
    @PreAuthorize("hasAnyRole('ROLE_USUARIO', 'ROLE_TECNICO', 'ROLE_DEVELOPER', 'ROLE_ADMIN')")
    public ResponseEntity<DTORespuestaIA> obtenerHistorial(
            @PathVariable Long idUsuario,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanio) {
        try {
            log.info("Obteniendo historial de usuario {}", idUsuario);
            Pageable pageable = PageRequest.of(pagina, tamanio);
            Page<DTORegistroInteraccionIA> historial = servicioIA.obtenerHistorialInteracciones(idUsuario, pageable);

            return ResponseEntity.ok(DTORespuestaIA.exitoso(historial, "Historial obtenido exitosamente"));
        } catch (Exception e) {
            log.error("Error al obtener historial: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DTORespuestaIA.error("Error al obtener historial", "500"));
        }
    }

    @GetMapping("/estadisticas/{idUsuario}")
    @PreAuthorize("hasAnyRole('ROLE_USUARIO', 'ROLE_TECNICO', 'ROLE_DEVELOPER', 'ROLE_ADMIN')")
    public ResponseEntity<DTORespuestaIA> obtenerEstadisticas(@PathVariable Long idUsuario) {
        try {
            log.info("Obteniendo estadísticas de usuario {}", idUsuario);
            long interaccionesCompletadas = servicioIA.contarInteraccionesCompletadas(idUsuario);

            var estadisticas = java.util.Map.of(
                    "idUsuario", idUsuario,
                    "interaccionesCompletadas", interaccionesCompletadas
            );

            return ResponseEntity.ok(DTORespuestaIA.exitoso(estadisticas, "Estadísticas obtenidas exitosamente"));
        } catch (Exception e) {
            log.error("Error al obtener estadísticas: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DTORespuestaIA.error("Error al obtener estadísticas", "500"));
        }
    }

    @GetMapping("/prompts")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_DEVELOPER')")
    public ResponseEntity<DTORespuestaIA> obtenerPrompts() {
        try {
            log.info("Obteniendo prompts activos");
            List<DTOPromptIAPorRol> prompts = servicioIA.obtenerPromptsActivos();

            return ResponseEntity.ok(DTORespuestaIA.exitoso(prompts, "Prompts obtenidos exitosamente"));
        } catch (Exception e) {
            log.error("Error al obtener prompts: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DTORespuestaIA.error("Error al obtener prompts", "500"));
        }
    }

    @PostMapping("/prompts")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<DTORespuestaIA> crearPrompt(@RequestBody DTOPromptIAPorRol.Crear dto) {
        try {
            log.info("Creando nuevo prompt para rol: {}", dto.getRol());
            var prompt = servicioIA.crearPrompt(dto);

            DTOPromptIAPorRol dtoRespuesta = DTOPromptIAPorRol.builder()
                    .id(prompt.getId())
                    .rol(prompt.getRol())
                    .instruccion(prompt.getInstruccion())
                    .contexto(prompt.getContexto())
                    .tokenMaximo(prompt.getTokenMaximo())
                    .temperatura(prompt.getTemperatura())
                    .activo(prompt.getActivo())
                    .build();

            return ResponseEntity.ok(DTORespuestaIA.exitoso(dtoRespuesta, "Prompt creado exitosamente"));
        } catch (Exception e) {
            log.error("Error al crear prompt: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DTORespuestaIA.error("Error al crear prompt", "500"));
        }
    }

    @PutMapping("/prompts/{idPrompt}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<DTORespuestaIA> actualizarPrompt(
            @PathVariable Long idPrompt,
            @RequestBody DTOPromptIAPorRol.Actualizar dto) {
        try {
            log.info("Actualizando prompt {}", idPrompt);
            var prompt = servicioIA.actualizarPrompt(idPrompt, dto);

            return ResponseEntity.ok(DTORespuestaIA.exitoso(prompt.getId(), "Prompt actualizado exitosamente"));
        } catch (Exception e) {
            log.error("Error al actualizar prompt: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DTORespuestaIA.error("Error al actualizar prompt", "500"));
        }
    }

    @GetMapping("/horario/disponible")
    @PreAuthorize("hasAnyRole('ROLE_USUARIO', 'ROLE_TECNICO', 'ROLE_DEVELOPER', 'ROLE_ADMIN')")
    public ResponseEntity<DTORespuestaIA> verificarDisponibilidad() {
        try {
            boolean disponible = servicioIA.estaEnHorarioAtencion();
            var resultado = java.util.Map.of("disponible", disponible);

            return ResponseEntity.ok(DTORespuestaIA.exitoso(resultado, 
                    disponible ? "IA disponible" : "IA no disponible en este horario"));
        } catch (Exception e) {
            log.error("Error al verificar disponibilidad: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DTORespuestaIA.error("Error al verificar disponibilidad", "500"));
        }
    }
}
