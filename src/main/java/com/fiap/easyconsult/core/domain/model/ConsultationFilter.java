package com.fiap.easyconsult.core.domain.model;

import com.fiap.easyconsult.core.domain.valueobject.DateAndTime;
import com.fiap.easyconsult.infra.entrypoint.dto.enums.StatusConsultation;

import java.io.Serializable;

public class ConsultationFilter implements Serializable {

    private Long id;
    private String patientEmail;
    private String professionalEmail;
    private StatusConsultation status;
    private DateAndTime startDate;
    private DateAndTime endDate;

    public ConsultationFilter(Long id, String patientEmail, String professionalEmail, StatusConsultation status, DateAndTime startDate, DateAndTime endDate) {
        this.patientEmail = patientEmail;
        this.professionalEmail = professionalEmail;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public DateAndTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateAndTime startDate) {
        this.startDate = startDate;
    }

    public DateAndTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateAndTime endDate) {
        this.endDate = endDate;
    }

    public StatusConsultation getStatus() {
        return status;
    }

    public void setStatus(StatusConsultation status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("filter[%s-%s-%s-%s-%s]",
                patientEmail,
                professionalEmail,
                status != null ? status.name() : "null",
                startDate,
                endDate
        );
    }

}
