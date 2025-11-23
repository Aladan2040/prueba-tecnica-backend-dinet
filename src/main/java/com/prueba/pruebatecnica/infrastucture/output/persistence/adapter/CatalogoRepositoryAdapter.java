package com.prueba.pruebatecnica.infrastucture.output.persistence.adapter;

import com.prueba.pruebatecnica.domain.model.Cliente;
import com.prueba.pruebatecnica.domain.model.Zona;
import com.prueba.pruebatecnica.domain.ports.output.CatalogoRepositoryPort;
import com.prueba.pruebatecnica.infrastucture.output.persistence.entity.ClienteEntity;
import com.prueba.pruebatecnica.infrastucture.output.persistence.entity.ZonaEntity;
import com.prueba.pruebatecnica.infrastucture.output.persistence.repository.ClienteJpaRepository;
import com.prueba.pruebatecnica.infrastucture.output.persistence.repository.ZonaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class CatalogoRepositoryAdapter implements CatalogoRepositoryPort {

    private final ClienteJpaRepository clienteJpaRepository;
    private final ZonaJpaRepository zonaJpaRepository;

    @Override
    public Map<String, Cliente> buscarClientePorIds(Set<String> ids){
        return clienteJpaRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(
                        ClienteEntity::getId,
                        entity -> new Cliente(entity.getId(), entity.isActivo())
                ));
    }

    @Override
    public Map<String, Zona> buscarZonaPorIds(Set<String> ids){
        return zonaJpaRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(
                        ZonaEntity::getId,
                        entity -> new Zona(entity.getId(), entity.isSoporteRefrigeracion())
                ));
    }
}
