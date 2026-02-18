package com.innoad.servicio;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServicioEmailResend {
    
    private final RestTemplate restTemplate;
    
    @Value("${spring.brevo.api-key:}")
    private String brevoApiKey;
    
    @Value("${spring.brevo.from-email:noreply@innoad.com}")
    private String fromEmail;
    
    @Value("${spring.brevo.from-name:InnoAd}")
    private String fromName;
    
    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";
    
    /**
     * Envía un email a través de Brevo de forma asíncrona
     */
    @Async
    public void enviarEmailHTML(String para, String asunto, String contenido) {
        try {
            if (brevoApiKey == null || brevoApiKey.isEmpty()) {
                log.error("BREVO_API_KEY no configurada. No se puede enviar email.");
                return;
            }
            
            // Construir el payload para Brevo
            Map<String, Object> payload = new HashMap<>();
            
            // From
            Map<String, String> sender = new HashMap<>();
            sender.put("name", fromName);
            sender.put("email", fromEmail);
            payload.put("sender", sender);
            
            // To
            Map<String, String> to = new HashMap<>();
            to.put("email", para);
            payload.put("to", List.of(to));
            
            // Subject
            payload.put("subject", asunto);
            
            // HTML content
            payload.put("htmlContent", contenido);
            
            // Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(BREVO_API_URL, request, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("✅ Email enviado exitosamente a {} vía Brevo", para);
            } else {
                log.error("❌ Error al enviar email con Brevo: {}", response.getBody());
            }
            
        } catch (Exception e) {
            log.error("❌ Error en ServicioEmailResend.enviarEmailHTML: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Verifica si Brevo está configurado
     */
    public boolean estaConfigurado() {
        return brevoApiKey != null && !brevoApiKey.isEmpty();
    }
}
