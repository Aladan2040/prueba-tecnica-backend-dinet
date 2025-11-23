package com.prueba.pruebatecnica.infrastucture.output.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "zonas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZonaEntity {
    @Id
    private String id;

    @Column(name = "soporte_refrigeracion")
    private boolean soporteRefrigeracion;
}
