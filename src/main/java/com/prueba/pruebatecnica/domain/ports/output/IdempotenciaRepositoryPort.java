package com.prueba.pruebatecnica.domain.ports.output;

public interface IdempotenciaRepositoryPort {

    //Verifica si ya existe un procesamiento exitoso con esta clave o hash
    boolean existeProcesamiento(String idempotencyKey, String archivoHash);

    //Registra un procesamiento exitoso con su clave y hash
    void guardarProcesamiento(String idempotencyKey, String archivoHash);
}
