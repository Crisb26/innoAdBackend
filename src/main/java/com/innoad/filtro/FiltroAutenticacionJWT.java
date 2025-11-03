package com.innoad.filtro;

import com.innoad.servicio.ServicioJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de autenticación JWT que intercepta todas las peticiones HTTP.
 * Valida el token JWT y establece el contexto de seguridad.
 */
@Component
@RequiredArgsConstructor
public class FiltroAutenticacionJWT extends OncePerRequestFilter {
    
    private final ServicioJWT servicioJWT;
    private final UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        // Obtener el header de autorización
        final String authHeader = request.getHeader("Authorization");
        
        // Si no hay header o no empieza con "Bearer ", continuar sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            // Extraer el token
            final String jwt = authHeader.substring(7);
            final String nombreUsuario = servicioJWT.extraerNombreUsuario(jwt);
            
            // Si hay nombre de usuario y no hay autenticación actual
            if (nombreUsuario != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Cargar los detalles del usuario
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(nombreUsuario);
                
                // Validar el token
                if (servicioJWT.esTokenValido(jwt, userDetails)) {
                    // Crear el token de autenticación
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    
                    // Establecer detalles adicionales
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Establecer la autenticación en el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Log del error (en producción usar un logger apropiado)
            System.err.println("Error al procesar token JWT: " + e.getMessage());
        }
        
        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}
