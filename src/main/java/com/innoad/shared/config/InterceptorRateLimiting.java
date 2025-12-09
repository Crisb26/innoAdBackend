package com.innoad.shared.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import com.innoad.servicio.ServicioCacheRedis;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Interceptor de Rate Limiting para proteger contra abuso
 * Límites:
 * - 100 requests por minuto por usuario
 * - 5 preguntas a IA por minuto por usuario
 * - 10 requests por minuto por IP (no autenticado)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InterceptorRateLimiting implements HandlerInterceptor {

    private final ServicioCacheRedis servicioCacheRedis;

    // Constantes de límites
    private static final long LIMITE_GENERAL = 100;           // por minuto
    private static final long LIMITE_IA = 5;                  // preguntas IA por minuto
    private static final long LIMITE_NO_AUTENTICADO = 10;     // por minuto
    private static final long VENTANA_TIEMPO = 60;            // segundos

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        String endpoint = request.getRequestURI();
        String metodo = request.getMethod();
        
        try {
            // Obtener usuario autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String usuario = auth != null && auth.isAuthenticated() ? auth.getName() : request.getRemoteAddr();
            
            // Aplicar límite específico para endpoints de IA
            if (esEndpointIA(endpoint)) {
                return validarRateLimit(usuario, "IA", LIMITE_IA, response);
            }
            
            // Aplicar límite general
            return validarRateLimit(usuario, "GENERAL", LIMITE_GENERAL, response);
            
        } catch (Exception e) {
            log.error("Error en rate limiting: {}", e.getMessage());
            // Si hay error, permitir pero loguear
            return true;
        }
    }

    /**
     * Valida si el usuario ha excedido el límite de requests
     */
    private boolean validarRateLimit(String usuario, String tipo, long limite, HttpServletResponse response) throws Exception {
        // Incrementar contador en Redis
        long contador = servicioCacheRedis.incrementarContadorRateLimit(usuario, tipo, VENTANA_TIEMPO);
        
        // Agregar headers con info de rate limit
        response.setHeader("X-RateLimit-Limit", String.valueOf(limite));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(0, limite - contador)));
        response.setHeader("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() + (VENTANA_TIEMPO * 1000)));
        
        // Si excede límite, retornar 429 Too Many Requests
        if (contador > limite) {
            response.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS);
            response.setContentType("application/json");
            response.getWriter().write(\"{\" +
                    \"\\\"error\\\":\\\"Too many requests\\\"\" +\n                    \"\\\"mensaje\\\":\\\"Ha excedido el límite de \" + limite + \" \" + tipo.toLowerCase() + \" por minuto\\\"\" +\n                    \"\\\"retryAfter\\\":\" + VENTANA_TIEMPO +\n                    \"}\");
            
            log.warn(\"Rate limit excedido para {}: {} (límite: {})\", usuario, contador, límite);
            return false;
        }
        
        return true;
    }

    /**
     * Verifica si es un endpoint de IA
     */
    private boolean esEndpointIA(String endpoint) {
        return endpoint.contains(\"/api/ia/\") || 
               endpoint.contains(\"/aplicacion/ia/\") ||
               endpoint.contains(\"/pregunta\") ||
               endpoint.contains(\"/asistente\");
    }
}
