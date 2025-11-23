package com.prueba.pruebatecnica.domain.ports.output;

import com.prueba.pruebatecnica.domain.model.Cliente;
import com.prueba.pruebatecnica.domain.model.Zona;

import java.util.Map;
import java.util.Set;

public interface CatalogoRepositoryPort {

    //Busca multiples clientes por id y devuelve un mapa para acceso rapido
    Map<String, Cliente> buscarClientePorIds(Set<String> ids);

    //Busca multiples zonas por id
    Map<String, Zona> buscarZonaPorIds(Set<String> ids);
}
