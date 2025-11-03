package com.innoad.configuracion;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Comprueba variables de entorno críticas al arrancar la aplicación.
 * Si falta una variable obligatoria en el entorno de ejecución, lanza RuntimeException
 * para evitar arrancar en un estado mal configurado.
 */
@Component
@RequiredArgsConstructor
public class StartupChecks implements ApplicationRunner {

    private final Environment env;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<String> missing = new ArrayList<>();

        // JWT secret (si no está, la aplicación funciona con fallback, pero en prod es obligatorio)
        String jwt = env.getProperty("JWT_SECRET", env.getProperty("jwt.secret"));
        if (jwt == null || jwt.isBlank()) {
            missing.add("JWT_SECRET (jwt.secret)");
        }

        // Frontend URL (para CORS y redirecciones)
        String frontend = env.getProperty("FRONTEND_URL", env.getProperty("innoad.frontend.url"));
        if (frontend == null || frontend.isBlank()) {
            missing.add("FRONTEND_URL (innoad.frontend.url)");
        }

        // Si perfil activo es 'prod', exigir DB y MAIL
        String[] profiles = env.getActiveProfiles();
        for (String p : profiles) {
            if ("prod".equalsIgnoreCase(p)) {
                String db = env.getProperty("DB_URL");
                if (db == null || db.isBlank()) missing.add("DB_URL");

                String mailUser = env.getProperty("MAIL_USERNAME");
                String mailPass = env.getProperty("MAIL_PASSWORD");
                if (mailUser == null || mailUser.isBlank()) missing.add("MAIL_USERNAME");
                if (mailPass == null || mailPass.isBlank()) missing.add("MAIL_PASSWORD");
            }
        }

        if (!missing.isEmpty()) {
            String msg = "Variables de entorno faltantes: " + String.join(", ", missing) + 
                    ". Define estas variables antes de arrancar la aplicación (ver DEPLOYMENT.md).";
            // Lanzar excepción para detener el inicio y mostrar el error claro
            throw new RuntimeException(msg);
        }
    }
}
