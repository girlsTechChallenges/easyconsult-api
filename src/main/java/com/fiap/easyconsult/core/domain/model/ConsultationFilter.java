package com.fiap.easyconsult.core.domain.model;

import com.fiap.easyconsult.infra.entrypoint.dto.enums.StatusConsultation;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class ConsultationFilter implements Serializable {

    private Long id;
    private String patientEmail;
    private String professionalEmail;
    private StatusConsultation status;
    private LocalTime localTime;
    private LocalDate date;

    public ConsultationFilter(Long id, String patientEmail, String professionalEmail, StatusConsultation status,
                              LocalTime localTime, LocalDate date) {
        this.patientEmail = patientEmail;
        this.professionalEmail = professionalEmail;
        this.status = status;
        this.localTime = localTime;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    public String getProfessionalEmail() {
        return professionalEmail;
    }

    public void setProfessionalEmail(String professionalEmail) {
        this.professionalEmail = professionalEmail;
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

    public StatusConsultation getStatus() {
        return status;
    }

    public void setStatus(StatusConsultation status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("filter[%s-%s-%s-%s-%s-%s]",
                patientEmail,
                professionalEmail,
                status != null ? status.name() : "null",
                localTime != null ? localTime.toString() : "null",
                date != null ? date.toString() : "null",
                id != null ? id.toString() : "null"
        );
    }

}
