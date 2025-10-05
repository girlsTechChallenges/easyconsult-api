package com.fiap.easyconsult.core.domain.model;

import com.fiap.easyconsult.core.domain.valueobject.ConsultStatus;
import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public class ConsultFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String patientEmail;
    private String professionalEmail;
    private ConsultStatus status;
    private LocalTime localTime;
    private LocalDate date;

    public ConsultFilter(Long id, String patientEmail, String professionalEmail, ConsultStatus status,
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

    public ConsultStatus getStatus() {
        return status;
    }

    public void setStatus(ConsultStatus status) {
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