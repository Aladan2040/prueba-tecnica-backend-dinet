package com.prueba.pruebatecnica.domain.model;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ResumenCarga {

    private int totalProcesados;
    private int totalGuardados;
    private int totalConError;

    @Builder.Default
    private List<DetalleError> errores = new ArrayList<>();

    @Data
    @Builder
    public static class DetalleError {
        private int fila;
        private String motivo;
        private String tipoError;
    }
}
