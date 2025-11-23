package com.prueba.pruebatecnica.infrastucture.output.persistence.adapter;

import com.prueba.pruebatecnica.domain.model.Pedido;
import com.prueba.pruebatecnica.domain.ports.output.PedidoRepositoryPort;
import com.prueba.pruebatecnica.infrastucture.output.persistence.entity.PedidoEntity;
import com.prueba.pruebatecnica.infrastucture.output.persistence.mapper.PedidoMapper;
import com.prueba.pruebatecnica.infrastucture.output.persistence.repository.PedidoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PedidoRepositoryAdapter implements PedidoRepositoryPort {

    private final PedidoJpaRepository pedidoJpaRepository;
    private final PedidoMapper pedidoMapper;

    @Override
    public void guardarTodo(List<Pedido> pedidos){
        //Convertir Dominio a Entidad JPA
        List<PedidoEntity> entidades = pedidos.stream()
                .map(pedidoMapper::toEntity)
                .collect(Collectors.toList());

        //Guardar en batch
        pedidoJpaRepository.saveAll(entidades);
    }

    @Override
    public Set<String> buscarNumerosPedidoExistentes(Set<String> numerosPedidos){
        return pedidoJpaRepository.findNumerosPedidoByNumeroPedidoIn(numerosPedidos);
    }
}
