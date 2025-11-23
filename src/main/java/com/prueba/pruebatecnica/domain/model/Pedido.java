package com.prueba.pruebatecnica.domain.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class Pedido {

    private UUID id;
    private String numeroPedido;
    private String clienteId;
    private String zonaId;
    private LocalDate fechaEntrega;
    private EstadoPedido estado;
    private boolean requiereRefrigeracion;
}
