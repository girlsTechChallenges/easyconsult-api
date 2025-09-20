package com.fiap.easyconsult.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "consultations")
public class ConsultationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "patient" , referencedColumnName = "id" )
    private PatientEntity patient;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "professional" , referencedColumnName = "id" )
    private ProfessionalEntity professional;

    @Column(name = "date_time")
    LocalDateTime dateTime;
    private String reason;
    private String status;

    public ConsultationEntity(Long id, PatientEntity patient, ProfessionalEntity professional, LocalDateTime dateTime,
                              String reason, String status) {
        this.id = id;
        this.patient = patient;
        this.professional = professional;
        this.dateTime = dateTime;
        this.reason = reason;
        this.status = status;
    }

    public ConsultationEntity() {}
}
