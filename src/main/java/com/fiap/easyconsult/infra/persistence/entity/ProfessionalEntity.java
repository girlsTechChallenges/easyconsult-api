package com.fiap.easyconsult.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "professional")
public class ProfessionalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;

    public ProfessionalEntity(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public ProfessionalEntity() {}

}
