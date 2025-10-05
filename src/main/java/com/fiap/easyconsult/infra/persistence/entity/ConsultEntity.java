package com.fiap.easyconsult.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "consults")
public class ConsultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "patient" , referencedColumnName = "id" )
    private PatientEntity patient;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "professional" , referencedColumnName = "id" )
    private ProfessionalEntity professional;

    @Column(name = "local_time")
    private LocalTime localTime;
    @Column(name = "local_date")
    private LocalDate localDate;
    private String reason;
    private String status;

    public ConsultEntity(Long id, PatientEntity patient, ProfessionalEntity professional, LocalTime localTime,
                              LocalDate localDate, String reason, String status) {
        this.id = id;
        this.patient = patient;
        this.professional = professional;
        this.localDate = localDate;
        this.localTime = localTime;
        this.reason = reason;
        this.status = status;
    }

    public ConsultEntity() {}
}