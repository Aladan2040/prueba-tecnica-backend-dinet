package com.prueba.pruebatecnica.domain.ports.input;

import com.prueba.pruebatecnica.domain.model.ResumenCarga;

import java.io.InputStream;

public interface CargarPedidosUseCase {

    //Ejecuta la logica principal para cargar pedidos desde un archivo CSV
    //Leer CSV, validar reglas de negocio, guardar pedidos validos y devolver un resumen
    ResumenCarga ejecutar (InputStream archivoCsv, String idempotencyKey, String archivoHash);
}
