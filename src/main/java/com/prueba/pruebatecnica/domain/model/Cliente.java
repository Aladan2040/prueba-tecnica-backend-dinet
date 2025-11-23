package com.prueba.pruebatecnica.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Cliente {
    private String id;
    private boolean activo;
}
