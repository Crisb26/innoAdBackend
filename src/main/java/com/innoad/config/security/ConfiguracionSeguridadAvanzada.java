package com.innoad.config.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ConfiguracionSeguridadAvanzada {

    private final UserDetailsService userDetailsService;

    public ConfiguracionSeguridadAvanzada(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Encriptador BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Proveedor de autenticación DAO con UserDetailsService
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Gestor de autenticación de Spring Security
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configuración principal de seguridad
     */
    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            FiltroJWT filtroJWT,
            GestorExcepcionesSeguridad gestorExcepciones
    ) throws Exception {

        http
            // ==================== CORS ====================
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // ==================== CSRF ====================
            .csrf(csrf -> csrf.disable())

            // ==================== SESSION ====================
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // ==================== HEADERS ====================
            .headers(headers -> headers
                .contentSecurityPolicy(csp ->
                    csp.policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'")
                )
                .frameOptions(frame -> frame.deny())
                .httpStrictTransportSecurity(hsts ->
                    hsts.includeSubDomains(true)
                        .maxAgeInSeconds(31536000)
                )
            )

            // ==================== AUTHORIZATION ====================
            .authorizeHttpRequests(authz -> authz
                // ===== ENDPOINTS PÚBLICOS (SIN AUTENTICACIÓN) =====
                .requestMatchers(
                    // Autenticación y registro (estándar REST)
                    "/api/v1/auth/**",
                    "/api/auth/**",

                    // Alias por compatibilidad
                    "/api/autenticacion/**",
                    "/api/v1/autenticacion/**",
                    "/api/public/**",

                    // Raspberry Pi (dispositivos embebidos)
                    "/api/v1/raspberry/**",

                    // Mantenimiento público
                    "/api/mantenimiento/estado",
                    "/api/salud",

                    // Documentación
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/api-docs/**",

                    // Actuator y health checks
                    "/actuator/**",
                    "/actuator/health/**",

                    // H2 Console (solo desarrollo)
                    "/h2-console/**",

                    // Archivos estáticos
                    "/uploads/**",
                    "/static/**",

                    // Error handling
                    "/error",
                    "/favicon.ico"
                ).permitAll()

                // ===== ENDPOINTS ADMINISTRATIVOS =====
                .requestMatchers(
                    "/api/admin/**",
                    "/api/v1/admin/**",
                    "/api/mantenimiento/activar",
                    "/api/mantenimiento/desactivar"
                ).hasRole("ADMINISTRADOR")

                // ===== ENDPOINTS TÉCNICOS =====
                .requestMatchers(
                    "/api/tecnico/**",
                    "/api/v1/tecnico/**"
                ).hasAnyRole("ADMINISTRADOR", "TECNICO", "DESARROLLADOR")

                // ===== ENDPOINTS AUTENTICADOS =====
                .requestMatchers(
                    "/api/usuario/**",
                    "/api/v1/usuario/**",
                    "/api/campanas/**",
                    "/api/v1/campanas/**",
                    "/api/contenidos/**",
                    "/api/v1/contenidos/**",
                    "/api/pantallas/**",
                    "/api/v1/pantallas/**",
                    "/api/reportes/**",
                    "/api/v1/reportes/**",
                    "/api/campaigns/**",
                    "/api/v1/campaigns/**",
                    "/api/stats/**",
                    "/api/v1/stats/**"
                ).authenticated()

                // Resto requiere autenticación
                .anyRequest().authenticated()
            )

            // ==================== AUTHENTICATION PROVIDER ====================
            .authenticationProvider(authenticationProvider())

            // ==================== JWT FILTER ====================
            .addFilterBefore(filtroJWT, UsernamePasswordAuthenticationFilter.class)

            // ==================== EXCEPTION HANDLING ====================
            .exceptionHandling(exception ->
                exception.authenticationEntryPoint(gestorExcepciones)
            );

        return http.build();
    }

    /**
     * Configuración CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        // Origenes permitidos (desarrollo, servidor casero y emergencia cloud)
        config.setAllowedOriginPatterns(Arrays.asList(
            // Desarrollo local
            "http://localhost:*",
            "http://localhost",
            "http://127.0.0.1:*",
            // Servidor casero (redes privadas)
            "http://192.168.*:*",
            "http://192.168.*",
            "http://10.*:*",
            "http://10.*",
            "http://*.local:*",
            "http://*.local",
            // Emergencia cloud
            "https://*.netlify.app",
            "https://*.azurecontainerapps.io"
        ));

        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        config.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin"
        ));

        config.setExposedHeaders(Arrays.asList(
                "Authorization",
                "X-Total-Count",
                "X-Page-Number"
        ));

        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }

    /**
     * Rate limiter login
     */
    @Bean(name = "loginRateLimiter")
    public Bucket loginRateLimiter() {
        Bandwidth limit =
                Bandwidth.classic(5,
                        Refill.intervally(5, Duration.ofMinutes(15)));

        return Bucket4j.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Rate limiter API general
     */
    @Bean(name = "apiRateLimiter")
    public Bucket apiRateLimiter() {
        Bandwidth limit =
                Bandwidth.classic(100,
                        Refill.intervally(100, Duration.ofMinutes(1)));

        return Bucket4j.builder()
                .addLimit(limit)
                .build();
    }
}
