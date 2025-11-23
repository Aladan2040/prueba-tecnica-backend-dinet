package com.prueba.pruebatecnica.infrastucture.output.persistence.repository;

import com.prueba.pruebatecnica.infrastucture.output.persistence.entity.ZonaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZonaJpaRepository extends JpaRepository<ZonaEntity, String> {
}
