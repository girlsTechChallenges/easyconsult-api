package com.fiap.easyconsult.core.domain.model;

import com.fiap.easyconsult.core.domain.exception.ConsultationInvalidException;
import com.fiap.easyconsult.core.domain.valueobject.DateAndTime;

public class Consult {
    private Long id;
    private String reason;
    private Patient patient;
    private Professional professional;
    private final DateAndTime dateAndTime;

    public Consult(Long id, String reason, Patient patient, Professional professional, DateAndTime dateAndTime) {
        this.id = id;
        this.reason = reason;
        this.patient = patient;
        this.professional = professional;
        this.dateAndTime = dateAndTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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

    public DateAndTime getDateAndTime() {

        if (dateAndTime.isPast()){
            throw new ConsultationInvalidException("Cannot get date and time of a past consultation.");
        }

        return dateAndTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
