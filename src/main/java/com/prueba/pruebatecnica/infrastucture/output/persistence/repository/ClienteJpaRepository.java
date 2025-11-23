package com.prueba.pruebatecnica.infrastucture.output.persistence.repository;

import com.prueba.pruebatecnica.infrastucture.output.persistence.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, String> {
}
