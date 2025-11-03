package com.innoad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Clase principal de la aplicación InnoAd Backend.
 * Sistema de gestión de publicidad con inteligencia artificial integrada.
 * 
 * @author Equipo InnoAd
 * @version 2.0.0
 * @since 2025
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableScheduling
public class InnoAdApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(InnoAdApplication.class, args);
    }
}
