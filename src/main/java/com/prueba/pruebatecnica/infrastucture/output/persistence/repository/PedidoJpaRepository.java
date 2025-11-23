package com.prueba.pruebatecnica.infrastucture.output.persistence.repository;

import com.prueba.pruebatecnica.infrastucture.output.persistence.entity.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;
import java.util.UUID;

public interface PedidoJpaRepository extends JpaRepository<PedidoEntity, UUID> {

    //Consulta para validar duplicados masivamente
    @Query("SELECT p.numeroPedido FROM PedidoEntity p WHERE p.numeroPedido IN :numeros")
    Set<String> findNumerosPedidoByNumeroPedidoIn(@Param("numeros") Set<String> numeros);
}
