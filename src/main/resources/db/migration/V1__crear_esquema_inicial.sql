-- V1__crear_esquema_inicial.sql

-- Tabla CLIENTES
CREATE TABLE clientes (
    id VARCHAR(50) PRIMARY KEY,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

-- Tabla ZONAS
CREATE TABLE zonas (
    id VARCHAR(50) PRIMARY KEY,
    soporte_refrigeracion BOOLEAN NOT NULL DEFAULT FALSE
);

-- Tabla CARGAS_IDEMPOTENCIA
CREATE TABLE cargas_idempotencia (
    id UUID PRIMARY KEY,
    idempotency_key VARCHAR(255) NOT NULL,
    archivo_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
        CONSTRAINT uk_cargas_key_hash UNIQUE (idempotency_key, archivo_hash)
);

-- 4. Tabla PEDIDOS
CREATE TABLE pedidos (
    id UUID PRIMARY KEY,
    numero_pedido VARCHAR(50) NOT NULL,
    cliente_id VARCHAR(50) NOT NULL, -- FK lógico a clientes.id
    zona_id VARCHAR(50) NOT NULL,    -- FK lógico a zonas.id
    fecha_entrega DATE NOT NULL,
    estado VARCHAR(20) NOT NULL,
    requiere_refrigeracion BOOLEAN NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
        CONSTRAINT chk_estado_valido CHECK (estado IN ('PENDIENTE', 'CONFIRMADO', 'ENTREGADO')),
        CONSTRAINT uk_numero_pedido UNIQUE (numero_pedido)
);

-- IDX(estado, fecha_entrega)
CREATE INDEX idx_pedidos_estado_fecha ON pedidos(estado, fecha_entrega);

-- Comentarios descriptivos
COMMENT ON TABLE pedidos IS 'Almacena los pedidos procesados desde el archivo CSV';