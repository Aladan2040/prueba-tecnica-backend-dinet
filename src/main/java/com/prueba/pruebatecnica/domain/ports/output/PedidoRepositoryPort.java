package com.prueba.pruebatecnica.domain.ports.output;

import com.prueba.pruebatecnica.domain.model.Pedido;

import java.util.List;
import java.util.Set;

public interface PedidoRepositoryPort {

    //Guarda una lista de pedidos en la bd
    void guardarTodo(List<Pedido> pedidos);

    //Verifica que numeros de pedido ya existen en la bd
    Set<String> buscarNumerosPedidoExistentes(Set<String> numerosPedido);
}
