package com.fiap.easyconsult.core.domain.model;

import com.fiap.easyconsult.infra.entrypoint.dto.enums.StatusConsultation;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public class UpdateConsult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String professionalName;
    private LocalDate consultDate;
    private String reason;
    private StatusConsultation consultStatus;

    public UpdateConsult(String professionalName, LocalDate consultDate, String reason, StatusConsultation consultStatus) {
        this.professionalName = professionalName;
        this.consultDate = consultDate;
        this.reason = reason;
        this.consultStatus = consultStatus;
    }

    public String getProfessionalName() {
        return professionalName;
    }

    public void setProfessionalName(String professionalName) {
        this.professionalName = professionalName;
    }

    public LocalDate getConsultDate() {
        return consultDate;
    }

    public void setConsultDate(LocalDate consultDate) {
        this.consultDate = consultDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public StatusConsultation getConsultStatus() {
        return consultStatus;
    }

    public void setConsultStatus(StatusConsultation consultStatus) {
        this.consultStatus = consultStatus;
    }
}
