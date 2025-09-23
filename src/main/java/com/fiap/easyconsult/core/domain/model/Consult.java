package com.fiap.easyconsult.core.domain.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class Consult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String reason;
    private String status;
    private Patient patient;
    private Professional professional;
    private LocalTime localTime;
    private LocalDate date;

    public Consult(Long id, String reason, String status, Patient patient, Professional professional, LocalTime localTime, LocalDate date) {
        this.id = id;
        this.reason = reason;
        this.status = status;
        this.patient = patient;
        this.professional = professional;
        this.localTime = localTime;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Professional getProfessional() {
        return professional;
    }

    public void setProfessional(Professional professional) {
        this.professional = professional;
    }

    public LocalTime getLocalTime() {
        return localTime;
    }

    public void setLocalTime(LocalTime localTime) {
        this.localTime = localTime;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Consult{id=" + id +
                ", patient=" + (patient != null ? patient.getEmail() : "null") +
                ", professional=" + (professional != null ? professional.getEmail() : "null") +
                ", date=" + date +
                ", time=" + localTime +
                ", status=" + status + "}";
    }
}
