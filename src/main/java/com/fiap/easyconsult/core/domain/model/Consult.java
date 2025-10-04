package com.fiap.easyconsult.core.domain.model;

import com.fiap.easyconsult.core.domain.valueobject.ConsultDateTime;
import com.fiap.easyconsult.core.domain.valueobject.ConsultId;
import com.fiap.easyconsult.core.domain.valueobject.ConsultStatus;
import com.fiap.easyconsult.core.exception.DomainException;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class Consult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final ConsultId id;
    private final String reason;
    private ConsultStatus status;
    private final Patient patient;
    private final Professional professional;
    private final ConsultDateTime dateTime;

    private Consult(Builder builder) {
        validateConsult(builder);
        this.id = builder.id;
        this.reason = builder.reason;
        this.status = builder.status;
        this.patient = builder.patient;
        this.professional = builder.professional;
        this.dateTime = builder.dateTime;
    }

    private void validateConsult(Builder builder) {
        if (builder.reason == null || builder.reason.trim().isEmpty()) {
            throw new DomainException("Consult reason cannot be empty", "CONSTRAINT_VIOLATION");
        }
        if (builder.patient == null) {
            throw new DomainException("Patient cannot be null", "CONSTRAINT_VIOLATION");
        }
        if (builder.professional == null) {
            throw new DomainException("Professional cannot be null", "CONSTRAINT_VIOLATION");
        }
        if (builder.dateTime == null) {
            throw new DomainException("DateTime cannot be null", "CONSTRAINT_VIOLATION");
        }
    }

    public void cancel() {
        if (!status.canBeCancelled()) {
            throw new DomainException("Cannot cancel a consult with status: " + status, "BUSINESS_RULE");
        }
        if (dateTime.isPast()) {
            throw new DomainException("Cannot cancel a past consult", "BUSINESS_RULE");
        }
        this.status = ConsultStatus.CANCELLED;
    }

    public ConsultId getId() {
        return id;
    }

    public String getReason() {
        return reason;
    }

    public ConsultStatus getStatus() {
        return status;
    }

    public Patient getPatient() {
        return patient;
    }

    public Professional getProfessional() {
        return professional;
    }

    public LocalDate getDate() {
        return dateTime.getDate();
    }

    public LocalTime getTime() {
        return dateTime.getTime();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ConsultId id;
        private String reason;
        private Patient patient;
        private Professional professional;
        private ConsultDateTime dateTime;
        private ConsultStatus status;

        public Builder id(Long id) {
            this.id = ConsultId.of(id);
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public Builder patient(Patient patient) {
            this.patient = patient;
            return this;
        }

        public Builder professional(Professional professional) {
            this.professional = professional;
            return this;
        }

        public Builder dateTime(LocalDate date, LocalTime time) {
            this.dateTime = ConsultDateTime.of(date, time);
            return this;
        }

        public Builder status(ConsultStatus status) {
            this.status = status;
            return this;
        }

        public Consult build() {
            return new Consult(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof Consult consult && id.equals(consult.id));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}