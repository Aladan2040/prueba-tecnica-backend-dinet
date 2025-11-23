package com.prueba.pruebatecnica.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Zona {
    private String id;
    private boolean soporteRefrigeracion;

    public boolean puedeManejarFrio() {
        return soporteRefrigeracion;
    }
}
