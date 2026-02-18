package com.innoad.modules.auth.repository;

import com.innoad.modules.auth.domain.CodigoVerificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RepositorioCodigoVerificacion extends JpaRepository<CodigoVerificacion, Long> {

    /**
     * Encuentra el código más reciente no usado para un email y tipo específicos
     */
    Optional<CodigoVerificacion> findFirstByEmailAndTipoAndUsadoFalseOrderByIdDesc(
        String email,
        CodigoVerificacion.TipoVerificacion tipo
    );

    /**
     * Encuentra un código específico por email, código y tipo
     */
    Optional<CodigoVerificacion> findByEmailAndCodigoAndTipo(
        String email,
        String codigo,
        CodigoVerificacion.TipoVerificacion tipo
    );

    /**
     * Encuentra un código específico por email y código (para cualquier tipo)
     */
    Optional<CodigoVerificacion> findFirstByEmailAndCodigoOrderByIdDesc(
        String email,
        String codigo
    );

    /**
     * Cuenta códigos válidos (no expirados, no usados) para un email
     */
    @Query("SELECT COUNT(c) FROM CodigoVerificacion c WHERE c.email = :email AND c.tipo = :tipo AND c.usado = false AND c.expiraEn > :ahora")
    int countCodigosValidos(
        @Param("email") String email,
        @Param("tipo") CodigoVerificacion.TipoVerificacion tipo,
        @Param("ahora") LocalDateTime ahora
    );

    /**
     * Elimina códigos expirados
     */
    @Modifying
    @Query("DELETE FROM CodigoVerificacion c WHERE c.expiraEn < :ahora")
    void deleteCodigosExpirados(@Param("ahora") LocalDateTime ahora);

    /**
     * Marca todos los códigos previos como usados para un email y tipo
     */
    @Modifying
    @Query("UPDATE CodigoVerificacion c SET c.usado = true WHERE c.email = :email AND c.tipo = :tipo AND c.usado = false")
    void marcarComoUsadosPrevios(
        @Param("email") String email,
        @Param("tipo") CodigoVerificacion.TipoVerificacion tipo
    );
}
