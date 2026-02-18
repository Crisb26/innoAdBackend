package com.innoad.modules.publicaciones.servicio;

import com.innoad.modules.publicaciones.domain.Publicacion;
import com.innoad.modules.publicaciones.repository.PublicacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PublicacionServicio {
    
    @Autowired
    private PublicacionRepository publicacionRepository;
    
    /**
     * Obtiene una publicación por ID
     */
    public Optional<Publicacion> obtenerPorId(Long id) {
        log.info("Obteniendo publicación con ID: {}", id);
        return publicacionRepository.findById(id);
    }
    
    /**
     * Obtiene todas las publicaciones en un estado específico
     */
    public List<Publicacion> obtenerPorEstado(Publicacion.EstadoPublicacion estado) {
        log.info("Obteniendo publicaciones con estado: {}", estado);
        return publicacionRepository.findByEstado(estado);
    }
    
    /**
     * Guarda una publicación
     */
    public Publicacion guardar(Publicacion publicacion) {
        log.info("Guardando publicación: {}", publicacion.getTitulo());
        if (publicacion.getFechaCreacion() == null) {
            publicacion.setFechaCreacion(LocalDateTime.now());
        }
        return publicacionRepository.save(publicacion);
    }
    
    /**
     * Obtiene publicaciones por ubicación
     */
    public List<Publicacion> obtenerPorUbicacion(String ubicacion) {
        log.info("Obteniendo publicaciones de ubicación: {}", ubicacion);
        return publicacionRepository.findByUbicacion(ubicacion);
    }
    
    /**
     * Obtiene publicaciones en rango de fechas (activas)
     */
    public List<Publicacion> obtenerActivas(LocalDateTime desde, LocalDateTime hasta) {
        log.info("Obteniendo publicaciones activas entre {} y {}", desde, hasta);
        return publicacionRepository.findByEstadoAndFechaInicioBeforeAndFechaFinAfter(
            Publicacion.EstadoPublicacion.ACTIVA, desde, hasta
        );
    }
}
