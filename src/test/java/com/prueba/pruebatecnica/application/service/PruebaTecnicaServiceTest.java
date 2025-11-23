package com.prueba.pruebatecnica.application.service;

import com.prueba.pruebatecnica.application.services.CargarPedidosService;
import com.prueba.pruebatecnica.domain.model.Cliente;
import com.prueba.pruebatecnica.domain.model.ResumenCarga;
import com.prueba.pruebatecnica.domain.model.Zona;
import com.prueba.pruebatecnica.domain.ports.output.CatalogoRepositoryPort;
import com.prueba.pruebatecnica.domain.ports.output.IdempotenciaRepositoryPort;
import com.prueba.pruebatecnica.domain.ports.output.PedidoRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PruebaTecnicaServiceTest {

    @Mock
    private PedidoRepositoryPort pedidoRepository;
    @Mock
    private CatalogoRepositoryPort catalogoRepository;
    @Mock
    private IdempotenciaRepositoryPort idempotenciaRepository;

    @InjectMocks
    private CargarPedidosService service;

    private final String IDEMPOTENCY_KEY = "test-key-1";
    private final String ARCHIVO_HASH = "hash123";

    @BeforeEach
    void setup() {

    }

    @Test
    void ejecutar_debeProcesarCargaCorrectamente_CuandoDatosSonValidos() {
        //Un CSV valido en memoria
        String csvContent = "numeroPedido,clienteId,fechaEntrega,estado,zonaEntrega,requiereRefrigeracion\n" +
                "P001,CLI-1,2030-01-01,PENDIENTE,ZONA-1,false";
        InputStream  fileStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        //Mocks
        when(catalogoRepository.buscarClientePorIds(any())).thenReturn(Map.of("CLI-1", new Cliente("CLI-1", true)));
        when(catalogoRepository.buscarZonaPorIds(any())).thenReturn(Map.of("ZONA-1", new Zona("ZONA-1", false)));
        when(pedidoRepository.buscarNumerosPedidoExistentes(any())).thenReturn(Collections.emptySet());
        when(idempotenciaRepository.existeProcesamiento(any(), any())).thenReturn(false);

        //When
        ResumenCarga resumen = service.ejecutar(fileStream, IDEMPOTENCY_KEY, ARCHIVO_HASH);

        //THEN
        assertEquals(1, resumen.getTotalProcesados());
        assertEquals(1, resumen.getTotalGuardados());
        assertEquals(0, resumen.getTotalConError());

        //Verificamos que se llamo a guardar
        verify(pedidoRepository, times(1)).guardarTodo(any());
        verify(idempotenciaRepository).guardarProcesamiento(IDEMPOTENCY_KEY, ARCHIVO_HASH);
    }

    @Test
    void ejecutar_deberiaDetectarErroresDeNegocio(){
        //Un CSV con 2 lineas: una valida y una con error (cliente inactivo)
        String csvContent = "numeroPedido,clienteId,fechaEntrega,estado,zonaEntrega,requiereRefrigeracion\n" +
                "P001,CLI-1,2030-01-01,PENDIENTE,ZONA-1,false\n" +
                "P002,CLI-INACTIVO,2030-01-01,PENDIENTE,ZONA-1,false";
        InputStream fileStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        //Mocks
        when(catalogoRepository.buscarClientePorIds(any())).thenReturn(Map.of(
                "CLI-1", new Cliente("CLI-1", true),
                "CLI-INACTIVO", new Cliente("CLI-INACTIVO", false) // Inactivo
        ));
        when(catalogoRepository.buscarZonaPorIds(any())).thenReturn(Map.of("ZONA-1", new Zona("ZONA-1", false)));
        when(idempotenciaRepository.existeProcesamiento(any(), any())).thenReturn(false);

        //WHEN
        ResumenCarga resumen = service.ejecutar(fileStream, IDEMPOTENCY_KEY, ARCHIVO_HASH);

        //THEN
        assertEquals(2, resumen.getTotalProcesados());
        assertEquals(1, resumen.getTotalGuardados()); //Solo P001
        assertEquals(1, resumen.getTotalConError()); //P002 con error

        // Verificamos el mensaje de error
        assertTrue(resumen.getErrores().get(0).getMotivo().contains("INACTIVO"));
    }

    @Test
    void ejecutar_DeberiaLanzarExcepcion_SiIdempotenciaExiste() {
        //Se simula que ya existe el procesamiento
        when(idempotenciaRepository.existeProcesamiento(IDEMPOTENCY_KEY, ARCHIVO_HASH)).thenReturn(true);
        InputStream fileStream = new ByteArrayInputStream("".getBytes());

        //Esperamos una excepción
        assertThrows(IllegalStateException.class, () ->
                service.ejecutar(fileStream, IDEMPOTENCY_KEY, ARCHIVO_HASH)
        );

        //Aseguramos que nunca se intento leer el archivo ni guardar nada
        verify(pedidoRepository, never()).guardarTodo(any());
    }

}
