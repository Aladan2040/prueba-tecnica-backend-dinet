package com.prueba.pruebatecnica.infrastucture.output.persistence.repository;

import com.prueba.pruebatecnica.infrastucture.output.persistence.entity.CargaIdempotenciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CargaIdempotenciaJpaRepository extends JpaRepository<CargaIdempotenciaEntity, UUID> {
    boolean existsByIdempotencyKeyAndArchivoHash(String idempotencyKey, String archivoHash);
}
