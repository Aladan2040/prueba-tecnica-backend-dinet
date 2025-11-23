package com.prueba.pruebatecnica.domain.model;

public enum EstadoPedido {
    PENDIENTE,
    CONFIRMADO,
    ENTREGADO;

    public static boolean esValido(String estado) {
        try {
            EstadoPedido.valueOf(estado);
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }
}
