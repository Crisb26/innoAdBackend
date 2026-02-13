package com.innoad.config.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
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

    /**
     * Encriptador BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
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
                .requestMatchers(
                    "/api/public/**",
                    "/api/autenticacion/registrar",
                    "/api/autenticacion/login",
                    "/api/autenticacion/refresh-token",
                    "/api/salud",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()

                .requestMatchers("/api/admin/**")
                .hasRole("ADMIN")

                .requestMatchers(
                    "/api/usuario/**",
                    "/api/campanas/**",
                    "/api/contenidos/**",
                    "/api/pantallas/**",
                    "/api/reportes/**"
                ).authenticated()

                .anyRequest().authenticated()
            )

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

        config.setAllowedOrigins(Arrays.asList(
                "https://innoadfrontend.netlify.app",
                "http://localhost:4200",
                "https://localhost:4200"
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
