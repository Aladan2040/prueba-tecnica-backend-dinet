package com.prueba.pruebatecnica.application.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class PedidoCsvRow {

    @CsvBindByName(column = "numeroPedido", required = true)
    private String numeroPedido;

    @CsvBindByName(column = "clienteId", required = true)
    private String clienteId;

    @CsvBindByName(column = "fechaEntrega", required = true)
    private String fechaEntrega; //Se lee como string para luego parsear a LocalDate

    @CsvBindByName(column = "estado", required = true)
    private String estado;

    @CsvBindByName(column = "zonaEntrega", required = true)
    private String zonaEntrega;

    @CsvBindByName(column = "requiereRefrigeracion", required = true)
    private boolean requiereRefrigeracion;
}
