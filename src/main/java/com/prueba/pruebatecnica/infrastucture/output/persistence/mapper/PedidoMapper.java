package com.prueba.pruebatecnica.infrastucture.output.persistence.mapper;

import com.prueba.pruebatecnica.domain.model.Pedido;
import com.prueba.pruebatecnica.infrastucture.output.persistence.entity.PedidoEntity;
import org.springframework.stereotype.Component;

@Component
public class PedidoMapper {

    public PedidoEntity toEntity(Pedido domain) {
        if(domain == null) return null;

        return PedidoEntity.builder()
                .id(domain.getId())
                .numeroPedido(domain.getNumeroPedido())
                .clienteId(domain.getClienteId())
                .zonaId(domain.getZonaId())
                .fechaEntrega(domain.getFechaEntrega())
                .estado(domain.getEstado())
                .requiereRefrigeracion(domain.isRequiereRefrigeracion())
                .build();
    }
}
