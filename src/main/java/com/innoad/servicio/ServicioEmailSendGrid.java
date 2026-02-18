package com.innoad.servicio;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServicioEmailSendGrid {
    
    private final WebClient webClient = WebClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${spring.sendgrid.api-key:}")
    private String sendgridApiKey;
    
    @Value("${spring.sendgrid.from-email:noreply@innoad.com}")
    private String fromEmail;
    
    @Value("${spring.sendgrid.from-name:InnoAd}")
    private String fromName;
    
    /**
     * Envía un email a través de SendGrid de forma asíncrona
     */
    @Async
    public void enviarEmailHTML(String para, String asunto, String contenido) {
        try {
            if (sendgridApiKey == null || sendgridApiKey.isEmpty()) {
                log.error("SENDGRID_API_KEY no configurada. No se puede enviar email.");
                return;
            }
            
            // Construir el payload para SendGrid
            Map<String, Object> emailRequest = construirPayloadSendGrid(para, asunto, contenido);
            
            // Enviar a través de WebClient
            webClient.post()
                    .uri("https://api.sendgrid.com/v3/mail/send")
                    .header("Authorization", "Bearer " + sendgridApiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(emailRequest)
                    .retrieve()
                    .toBodilessEntity()
                    .subscribe(
                        response -> {
                            int status = response.getStatusCode().value();
                            if (status == 202) {
                                log.info("✅ Email enviado exitosamente a {} vía SendGrid", para);
                            } else {
                                log.error("❌ Error al enviar email con SendGrid. Código HTTP: {}", status);
                            }
                        },
                        error -> log.error("❌ Error en SendGrid al enviar email: {}", error.getMessage(), error)
                    );
            
        } catch (Exception e) {
            log.error("❌ Error en ServicioEmailSendGrid.enviarEmailHTML: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Construye el payload JSON para la API de SendGrid
     */
    private Map<String, Object> construirPayloadSendGrid(String para, String asunto, String contenido) {
        Map<String, Object> payload = new HashMap<>();
        
        // From
        Map<String, String> from = new HashMap<>();
        from.put("email", fromEmail);
        from.put("name", fromName);
        payload.put("from", from);
        
        // Subject
        payload.put("subject", asunto);
        
        // Recipients - Personalizations
        Map<String, Object> personalization = new HashMap<>();
        Map<String, String> toEmail = new HashMap<>();
        toEmail.put("email", para);
        personalization.put("to", List.of(toEmail));
        payload.put("personalizations", List.of(personalization));
        
        // Content
        Map<String, String> content = new HashMap<>();
        content.put("type", "text/html");
        content.put("value", contenido);
        payload.put("content", List.of(content));
        
        return payload;
    }
    
    /**
     * Verifica si SendGrid está configurado
     */
    public boolean estaConfigurado() {
        return sendgridApiKey != null && !sendgridApiKey.isEmpty();
    }
}
