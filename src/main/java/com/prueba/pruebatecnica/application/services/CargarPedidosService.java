package com.prueba.pruebatecnica.application.services;

import com.opencsv.bean.CsvToBeanBuilder;
import com.prueba.pruebatecnica.application.dto.PedidoCsvRow;
import com.prueba.pruebatecnica.domain.model.*;
import com.prueba.pruebatecnica.domain.ports.input.CargarPedidosUseCase;
import com.prueba.pruebatecnica.domain.ports.output.CatalogoRepositoryPort;
import com.prueba.pruebatecnica.domain.ports.output.IdempotenciaRepositoryPort;
import com.prueba.pruebatecnica.domain.ports.output.PedidoRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CargarPedidosService implements CargarPedidosUseCase {

    private final PedidoRepositoryPort pedidoRepository;
    private final CatalogoRepositoryPort catalogoRepository;
    private final IdempotenciaRepositoryPort idempotenciaRepository;

    private static final ZoneId ZONA_HORARIA = ZoneId.of("America/Lima");

    @Override
    @Transactional
    public ResumenCarga ejecutar(InputStream archivoCsv, String idempotencyKey, String archivoHash) {
        log.info("Iniciando carga. Key: {}, Hash:{}", idempotencyKey, archivoHash);

        if(idempotenciaRepository.existeProcesamiento(idempotencyKey, archivoHash)){
            log.warn("Intento de carga duplicada detectado. Key: {}", idempotencyKey);
            throw new IllegalStateException("Esta carga ya fue procesada anteriormente (Idempotency Key duplicada)");
        }

        //Parsear CSV a Objetos
        List<PedidoCsvRow> filasCsv;
        try {
            filasCsv = new CsvToBeanBuilder<PedidoCsvRow>(new InputStreamReader(archivoCsv, StandardCharsets.UTF_8))
                    .withType(PedidoCsvRow.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();
        } catch (Exception e) {
            log.error("Error al leer CSV", e);
            throw new RuntimeException("El archivo CSV tiene un formato inválido: " + e.getMessage());
        }

        //Preparar Carga batch (Traer datos de referencia en una sola consulta)
        Set<String> clientesIds = filasCsv.stream().map(PedidoCsvRow::getClienteId).collect(Collectors.toSet());
        Set<String> zonasIds = filasCsv.stream().map(PedidoCsvRow::getZonaEntrega).collect(Collectors.toSet());
        Set<String> numerosPedidoCsv = filasCsv.stream().map(PedidoCsvRow::getNumeroPedido).collect(Collectors.toSet());

        Map<String, Cliente> clientesMap = catalogoRepository.buscarClientePorIds(clientesIds);
        Map<String, Zona> zonasMap = catalogoRepository.buscarZonaPorIds(zonasIds);
        Set<String> pedidosExistentes = pedidoRepository.buscarNumerosPedidoExistentes(numerosPedidoCsv);

        //Validar fila por fila
        List<Pedido> pedidosValidos = new ArrayList<>();
        List<ResumenCarga.DetalleError> errores = new ArrayList<>();

        int numeroLinea = 1;
        LocalDate hoyEnLima = LocalDate.now(ZONA_HORARIA);

        for(PedidoCsvRow fila : filasCsv){
            numeroLinea++;
            List<String> motivosRechazo = new ArrayList<>();

            //Validaciones
            //Numero pedido unico
            if (pedidosExistentes.contains(fila.getNumeroPedido())){
                motivosRechazo.add("DUPLICADO: El número de pedido ya existe");
            }

            //Cliente existe
            Cliente cliente = clientesMap.get(fila.getClienteId());
            if (cliente == null || !cliente.isActivo()){
                motivosRechazo.add("CLIENTE_NO_ENCONTRADO O INACTIVO: " + fila.getClienteId());
            }

            //Zona existe
            Zona zona = zonasMap.get(fila.getZonaEntrega());
            if (zona == null){
                motivosRechazo.add("ZONA_INVALIDA: " + fila.getZonaEntrega());
            }

            //Fecha entrega
            LocalDate fechaEntrega = null;
            try {
                fechaEntrega = LocalDate.parse(fila.getFechaEntrega());
                if(fechaEntrega.isBefore(hoyEnLima)){
                    motivosRechazo.add("FECHA_INVALIDA: La fecha es pasada");
                }
            } catch (Exception e) {
                motivosRechazo.add("FECHA_FORMATO_INVALIDO: Use YYYY-MM-DD");
            }

            //Estado valido
            if(!EstadoPedido.esValido(fila.getEstado())){
                motivosRechazo.add("ESTADO_INVALIDO: " + fila.getEstado());
            }

            //Cadena de frio
            if(fila.isRequiereRefrigeracion() && zona != null && !zona.puedeManejarFrio()) {
                motivosRechazo.add("CADENA_FRIO_NO_SOPORTADA: " + zona.getId() + "no tiene refrigeracion");
            }

            //Guardar o Reportar error
            if(motivosRechazo.isEmpty()){
                Pedido pedido = Pedido.builder()
                        .id(UUID.randomUUID())
                        .numeroPedido(fila.getNumeroPedido())
                        .clienteId(fila.getClienteId())
                        .zonaId(fila.getZonaEntrega())
                        .fechaEntrega(fechaEntrega)
                        .estado(EstadoPedido.valueOf(fila.getEstado()))
                        .requiereRefrigeracion(fila.isRequiereRefrigeracion())
                        .build();
                pedidosValidos.add(pedido);
            } else {
                errores.add(ResumenCarga.DetalleError.builder()
                        .fila(numeroLinea)
                        .motivo(String.join(", ", motivosRechazo))
                        .tipoError("VALIDACION_NEGOCIO")
                        .build());
            }
        }

        //Batch save
        if(!pedidosValidos.isEmpty()){
            pedidoRepository.guardarTodo(pedidosValidos);
        }

        idempotenciaRepository.guardarProcesamiento(idempotencyKey, archivoHash);

        //Guardar idempotencia (Hash + Key)

        return ResumenCarga.builder()
                .totalProcesados(filasCsv.size())
                .totalGuardados(pedidosValidos.size())
                .totalConError(errores.size())
                .errores(errores)
                .build();
    }
}
