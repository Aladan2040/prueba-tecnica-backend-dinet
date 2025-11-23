package com.prueba.pruebatecnica.infrastucture.input.rest;

import com.prueba.pruebatecnica.domain.model.ResumenCarga;
import com.prueba.pruebatecnica.domain.ports.input.CargarPedidosUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Pedidos", description = "Operaciones de carga y gestion de pedidos")
public class PedidoController {

    private final CargarPedidosUseCase cargarPedidosUseCase;

    @Operation(summary = "Cargar pedidos desde CSV",
    description = "Procesa un archivo CSV, valida reglas de negocio y guarda pedidos. Es idempotente.")
    @ApiResponse(responseCode = "200", description = "Procesamiento completado",
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResumenCarga.class)))
    @ApiResponse(responseCode = "400", description = "Archivo inválido o error de negocio")
    @ApiResponse(responseCode = "409", description = "Conflicto de idempotencia")
    @PostMapping(value = "/cargar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResumenCarga> cargarPedidos(
            @Parameter(description = "Archivo CSV con los pedidos")
            @RequestParam("file")MultipartFile file,

            @Parameter(description = "Clave única para asegurar idempotencia", required = true)
            @RequestHeader("Idempotency-Key") String idempotencyKey
            ){
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            //Calculamos el hash SHA-256 del contenido del archivo
            String archivoHash = DigestUtils.md5DigestAsHex(file.getInputStream());

            String sha256Hex = calcularSha256(file);
            ResumenCarga resumen = cargarPedidosUseCase.ejecutar(file.getInputStream(), idempotencyKey, sha256Hex);
            return ResponseEntity.ok(resumen);
        } catch (IllegalStateException e) {
            log.warn("Error de idempotencia: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (Exception e) {
            log.error("Error procesando archivo", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private String calcularSha256(MultipartFile file) throws IOException {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(file.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("Error calculando hash SHA-256", e);
        }
    }
}
