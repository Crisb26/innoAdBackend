package com.innoad.configuracion;

import com.innoad.modelo.entidades.Usuario;
import com.innoad.modelo.enumeraciones.RolUsuario;
import com.innoad.repositorio.RepositorioUsuario;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Inicializa datos básicos al arrancar la aplicación si no existen usuarios.
 * Crea cuentas por defecto para facilitar pruebas locales y la integración con el frontend.
 * No sobrescribe datos existentes.
 */
@Component
@RequiredArgsConstructor
public class InicializadorDatos implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(InicializadorDatos.class);

    private final RepositorioUsuario repositorioUsuario;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        long total = repositorioUsuario.count();
        if (total > 0) {
            log.info("InicializadorDatos: usuarios existentes detectados ({}). No se crean cuentas por defecto.", total);
            return;
        }

        log.info("InicializadorDatos: no hay usuarios. Creando cuentas por defecto para desarrollo...");

        crearSiNoExiste(
                "admin", "admin@innoad.com", "Admin123!", "Admin", "InnoAd",
                RolUsuario.ADMINISTRADOR
        );

        crearSiNoExiste(
                "tecnico", "tecnico@innoad.com", "Tecnico123!", "Tec", "Nico",
                RolUsuario.TECNICO
        );

        crearSiNoExiste(
                "dev", "dev@innoad.com", "Dev123!", "Devel", "Oper",
                RolUsuario.DESARROLLADOR
        );

        crearSiNoExiste(
                "usuario", "usuario@innoad.com", "Usuario123!", "Usu", "Ario",
                RolUsuario.USUARIO
        );

        log.info("InicializadorDatos: cuentas por defecto creadas. Puedes iniciar sesión con 'admin' / 'Admin123!'.");
    }

    private void crearSiNoExiste(String username, String email, String rawPassword,
                                 String nombre, String apellido, RolUsuario rol) {
        if (repositorioUsuario.existsByNombreUsuario(username) || repositorioUsuario.existsByEmail(email)) {
            return;
        }

        Usuario u = Usuario.builder()
                .nombre(nombre)
                .apellido(apellido)
                .email(email)
                .nombreUsuario(username)
                .contrasena(passwordEncoder.encode(rawPassword))
                .rol(rol)
                .activo(true)
                .verificado(true) // marcamos verificado para simplificar pruebas de login
                .build();

        repositorioUsuario.save(u);
        log.info(" - Usuario '{}' ({}) creado con rol {}", username, email, rol);
    }
}
