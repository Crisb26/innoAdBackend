package com.innoad.modules.auth.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Entidad para códigos de verificación de 6 dígitos
 * Usada tanto para registro como para recuperación de contraseña
 */
@Entity
@Table(name = "codigos_verificacion", indexes = {
    @Index(name = "idx_email_tipo", columnList = "email, tipo"),
    @Index(name = "idx_codigo_tipo", columnList = "codigo, tipo"),
    @Index(name = "idx_expira_en", columnList = "expira_en")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodigoVerificacion {

    private static final int TIEMPO_EXPIRACION_MINUTOS = 15;
    private static final int MAX_INTENTOS_FALLIDOS = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 6)
    private String codigo; // Código de 6 dígitos

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoVerificacion tipo; // REGISTRO o RECUPERACION

    @Column(nullable = false)
    private LocalDateTime creadoEn;

    @Column(nullable = false)
    private LocalDateTime expiraEn;

    @Column(nullable = false)
    private Boolean usado;

    @Column(nullable = false)
    private Integer intentosFallidos;

    @PrePersist
    protected void onCreate() {
        if (creadoEn == null) {
            creadoEn = LocalDateTime.now();
        }
        if (expiraEn == null) {
            expiraEn = LocalDateTime.now().plusMinutes(TIEMPO_EXPIRACION_MINUTOS);
        }
        if (usado == null) {
            usado = false;
        }
        if (intentosFallidos == null) {
            intentosFallidos = 0;
        }
        if (codigo == null) {
            codigo = generarCodigo6Digitos();
        }
    }

    public enum TipoVerificacion {
        REGISTRO,
        RECUPERACION
    }

    /**
     * Genera un código de 6 dígitos aleatorio
     */
    public static String generarCodigo6Digitos() {
        int codigo = new Random().nextInt(999999) + 1; // Genera un número entre 1 y 999999
        return String.format("%06d", codigo); // Padea con ceros a la izquierda
    }

    /**
     * Verifica si el código ha expirado
     */
    public boolean estaExpirado() {
        return LocalDateTime.now().isAfter(expiraEn);
    }

    /**
     * Verifica si el código ya ha sido usado
     */
    public boolean yaFueUsado() {
        return usado;
    }

    /**
     * Verifica si se ha excedido el máximo de intentos fallidos
     */
    public boolean excedioIntentosMaximos() {
        return intentosFallidos >= MAX_INTENTOS_FALLIDOS;
    }

    /**
     * Marca el código como usado
     */
    public void marcarComoUsado() {
        this.usado = true;
    }

    /**
     * Incrementa el contador de intentos fallidos
     */
    public void incrementarIntentosFallidos() {
        this.intentosFallidos++;
    }
}
