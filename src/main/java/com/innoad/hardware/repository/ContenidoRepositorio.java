package com.innoad.hardware.repository;

import com.innoad.hardware.model.ContenidoRemoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContenidoRepositorio extends JpaRepository<ContenidoRemoto, String> {
}
