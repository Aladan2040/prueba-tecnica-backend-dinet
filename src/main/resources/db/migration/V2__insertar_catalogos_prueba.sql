-- V2__insertar_catalogos_prueba.sql

-- Insertar Clientes
INSERT INTO clientes (id, activo) VALUES
        ('CLI-123', true),
        ('CLI-456', true),
        ('CLI-999', false);

-- Insertar Zonas
INSERT INTO zonas (id, soporte_refrigeracion) VALUES
        ('ZONA1', true),
        ('ZONA2', false),
        ('ZONA5', true);