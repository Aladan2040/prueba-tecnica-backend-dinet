package com.prueba.pruebatecnica.infrastucture.output.persistence.adapter;

import com.prueba.pruebatecnica.domain.ports.output.IdempotenciaRepositoryPort;
import com.prueba.pruebatecnica.infrastucture.output.persistence.entity.CargaIdempotenciaEntity;
import com.prueba.pruebatecnica.infrastucture.output.persistence.repository.CargaIdempotenciaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class IdempotenciaRepositoryAdapter implements IdempotenciaRepositoryPort {

    private final CargaIdempotenciaJpaRepository repository;

    @Override
    public boolean existeProcesamiento(String idempotencyKey, String archivoHash) {
        return repository.existsByIdempotencyKeyAndArchivoHash(idempotencyKey, archivoHash);
    }

    @Override
    public void guardarProcesamiento(String idempotencyKey, String archivoHash){
        CargaIdempotenciaEntity entidad = CargaIdempotenciaEntity.builder()
                .id(UUID.randomUUID())
                .idempotencyKey(idempotencyKey)
                .archivoHash(archivoHash)
                .build();

        repository.save(entidad);
    }
}
