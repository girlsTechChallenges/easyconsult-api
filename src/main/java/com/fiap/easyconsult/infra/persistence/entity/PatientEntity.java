package com.fiap.easyconsult.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "patient")
public class PatientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;

    public PatientEntity(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public PatientEntity() {}
}
