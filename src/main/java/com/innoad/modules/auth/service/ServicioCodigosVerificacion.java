package com.innoad.modules.auth.service;

import com.innoad.modules.auth.domain.CodigoVerificacion;
import com.innoad.modules.auth.repository.RepositorioCodigoVerificacion;
import com.innoad.servicio.ServicioEmail;
import com.innoad.servicio.ServicioEmailResend;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Servicio para generar y validar códigos de verificación de 6 dígitos
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ServicioCodigosVerificacion {

    private final RepositorioCodigoVerificacion repositorioCodigoVerificacion;
    private final ServicioEmail servicioEmail;
    private final ServicioEmailResend servicioEmailResend;

    /**
     * Genera un código de verificación de 6 dígitos para un email
     */
    @Transactional
    public String generarCodigoVerificacion(String email, CodigoVerificacion.TipoVerificacion tipo) {
        try {
            // Marcar códigos previos como usados
            repositorioCodigoVerificacion.marcarComoUsadosPrevios(email, tipo);

            // Generar nuevo código
            String codigo = CodigoVerificacion.generarCodigo6Digitos();

            // Crear entidad
            CodigoVerificacion codigoVerificacion = CodigoVerificacion.builder()
                    .email(email)
                    .codigo(codigo)
                    .tipo(tipo)
                    .build();

            repositorioCodigoVerificacion.save(codigoVerificacion);

            log.info("Código de verificación generado para {}: {} (tipo: {})", email, codigo, tipo);

            // Enviar email de forma asíncrona
            enviarCodigoVerificacion(email, codigo, tipo);

            return codigo; // En desarrollo, para pruebas
        } catch (Exception e) {
            log.error("Error generando código de verificación para {}: {}", email, e.getMessage());
            throw new RuntimeException("Error generando código de verificación: " + e.getMessage());
        }
    }

    /**
     * Valida un código de verificación
     */
    @Transactional
    public boolean validarCodigoVerificacion(String email, String codigo, CodigoVerificacion.TipoVerificacion tipo) {
        try {
            // Buscar el código
            var codigoOpt = repositorioCodigoVerificacion.findByEmailAndCodigoAndTipo(email, codigo, tipo);

            if (codigoOpt.isEmpty()) {
                log.warn("Código no encontrado o tipo no coincide para {} (tipo: {})", email, tipo);
                return false;
            }

            CodigoVerificacion codigoVerificacion = codigoOpt.get();

            // Verificar si ya fue usado
            if (codigoVerificacion.yaFueUsado()) {
                log.warn("Código ya fue usado para {}", email);
                return false;
            }

            // Verificar si ha expirado
            if (codigoVerificacion.estaExpirado()) {
                log.warn("Código expirado para {}", email);
                return false;
            }

            // Verificar si se excedie el máximo de intentos
            if (codigoVerificacion.excedioIntentosMaximos()) {
                log.warn("Máximo de intentos fallidos excedido para {}", email);
                return false;
            }

            // Marcar como usado
            codigoVerificacion.marcarComoUsado();
            repositorioCodigoVerificacion.save(codigoVerificacion);

            log.info("Código validado exitosamente para {}", email);
            return true;

        } catch (Exception e) {
            log.error("Error validando código para {}: {}", email, e.getMessage());
            return false;
        }
    }

    /**
     * Incrementa el contador de intentos fallidos para un código
     */
    @Transactional
    public void incrementarIntentosFallidos(String email, String codigo, CodigoVerificacion.TipoVerificacion tipo) {
        try {
            var codigoOpt = repositorioCodigoVerificacion.findByEmailAndCodigoAndTipo(email, codigo, tipo);

            if (codigoOpt.isPresent()) {
                CodigoVerificacion codigoVerificacion = codigoOpt.get();
                codigoVerificacion.incrementarIntentosFallidos();
                repositorioCodigoVerificacion.save(codigoVerificacion);
                log.info("Intentos fallidos incrementados para {}: {}", email, codigoVerificacion.getIntentosFallidos());
            }
        } catch (Exception e) {
            log.error("Error incrementando intentos fallidos para {}: {}", email, e.getMessage());
        }
    }

    /**
     * Envía el código de verificación por email (usa Resend si está disponible, sino SMTP)
     */
    @Async
    public void enviarCodigoVerificacion(String email, String codigo, CodigoVerificacion.TipoVerificacion tipo) {
        try {
            String asunto;
            String mensaje;

            if (tipo == CodigoVerificacion.TipoVerificacion.REGISTRO) {
                asunto = "Código de verificación - Registro en InnoAd";
                mensaje = construirEmailRegistro(codigo);
            } else {
                asunto = "Código de verificación - Recuperar contraseña";
                mensaje = construirEmailRecuperacion(codigo);
            }

            // Usar Resend si está configurado, si no, usar SMTP tradicional
            if (servicioEmailResend.estaConfigurado()) {
                servicioEmailResend.enviarEmailHTML(email, asunto, mensaje);
            } else {
                servicioEmail.enviarEmailHTML(email, asunto, mensaje);
            }
            log.info("Email de verificación enviado a {} (tipo: {})", email, tipo);

        } catch (Exception e) {
            log.error("Error enviando email de verificación a {}: {}", email, e.getMessage());
        }
    }

    /**
     * Construye el HTML del email de registro
     */
    private String construirEmailRegistro(String codigo) {
        String html = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>Código de Verificación - InnoAd</title>\n" +
            "</head>\n" +
            "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;\">\n" +
            "    <div style=\"background: linear-gradient(135deg, #0a0e27 0%, #1a1f3a 100%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;\">\n" +
            "        <h1 style=\"color: #00d9ff; margin: 0;\">InnoAd</h1>\n" +
            "        <p style=\"color: #b4b8d0; margin: 10px 0 0 0;\">Sistema de Gestión de Publicidad Digital</p>\n" +
            "    </div>\n" +
            "\n" +
            "    <div style=\"background-color: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px;\">\n" +
            "        <h2 style=\"color: #1a1f3a;\">Bienvenido a InnoAd!</h2>\n" +
            "\n" +
            "        <p>Gracias por registrarte! Para completar tu registro, utiliza el siguiente código de verificación:</p>\n" +
            "\n" +
            "        <div style=\"text-align: center; margin: 30px 0; background: white; padding: 20px; border-radius: 8px; border: 2px dashed #00d9ff;\">\n" +
            "            <p style=\"margin: 0; font-size: 12px; color: #666; margin-bottom: 10px;\">TU CÓDIGO DE VERIFICACIÓN</p>\n" +
            "            <p style=\"margin: 0; font-size: 48px; font-weight: bold; letter-spacing: 10px; color: #00d9ff; font-family: 'Courier New', monospace;\">" + codigo + "</p>\n" +
            "        </div>\n" +
            "\n" +
            "        <p style=\"text-align: center; color: #666; font-size: 14px;\">\n" +
            "            <strong>Este código expira en 15 minutos</strong>\n" +
            "        </p>\n" +
            "\n" +
            "        <p style=\"margin-top: 30px; padding-top: 20px; border-top: 1px solid #ddd; color: #666; font-size: 12px;\">\n" +
            "            Si no solicitaste este registro, por favor ignora este correo. Nunca compartiremos tu código de verificación.\n" +
            "        </p>\n" +
            "\n" +
            "        <div style=\"text-align: center; margin-top: 20px;\">\n" +
            "            <p style=\"color: #999; font-size: 11px; margin: 0;\">2026 InnoAd - Todos los derechos reservados</p>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>";
        return html;
    }

    /**
     * Construye el HTML del email de recuperación de contraseña
     */
    private String construirEmailRecuperacion(String codigo) {
        String html = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>Código de Recuperación - InnoAd</title>\n" +
            "</head>\n" +
            "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;\">\n" +
            "    <div style=\"background: linear-gradient(135deg, #0a0e27 0%, #1a1f3a 100%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;\">\n" +
            "        <h1 style=\"color: #00d9ff; margin: 0;\">InnoAd</h1>\n" +
            "        <p style=\"color: #b4b8d0; margin: 10px 0 0 0;\">Sistema de Gestión de Publicidad Digital</p>\n" +
            "    </div>\n" +
            "\n" +
            "    <div style=\"background-color: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px;\">\n" +
            "        <h2 style=\"color: #1a1f3a;\">Recuperar tu Contraseña</h2>\n" +
            "\n" +
            "        <p>Recibimos una solicitud para recuperar tu contraseña. Utiliza el siguiente código para continuar:</p>\n" +
            "\n" +
            "        <div style=\"text-align: center; margin: 30px 0; background: white; padding: 20px; border-radius: 8px; border: 2px dashed #ff006a;\">\n" +
            "            <p style=\"margin: 0; font-size: 12px; color: #666; margin-bottom: 10px;\">TU CÓDIGO DE RECUPERACIÓN</p>\n" +
            "            <p style=\"margin: 0; font-size: 48px; font-weight: bold; letter-spacing: 10px; color: #ff006a; font-family: 'Courier New', monospace;\">" + codigo + "</p>\n" +
            "        </div>\n" +
            "\n" +
            "        <p style=\"text-align: center; color: #666; font-size: 14px;\">\n" +
            "            <strong>Este código expira en 15 minutos</strong>\n" +
            "        </p>\n" +
            "\n" +
            "        <p style=\"margin-top: 30px; padding-top: 20px; border-top: 1px solid #ddd; color: #666; font-size: 12px;\">\n" +
            "            Si no solicitaste recuperar tu contraseña, por favor ignora este correo. Tu cuenta está segura.\n" +
            "        </p>\n" +
            "\n" +
            "        <div style=\"text-align: center; margin-top: 20px;\">\n" +
            "            <p style=\"color: #999; font-size: 11px; margin: 0;\">2026 InnoAd - Todos los derechos reservados</p>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>";
        return html;
    }

    /**
     * Limpia códigos expirados cada hora
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void limpiarCodigosExpirados() {
        try {
            repositorioCodigoVerificacion.deleteCodigosExpirados(LocalDateTime.now());
            log.info("Códigos de verificación expirados eliminados");
        } catch (Exception e) {
            log.error("Error limpiando códigos expirados: {}", e.getMessage());
        }
    }
}
