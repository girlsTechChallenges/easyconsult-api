package com.fiap.easyconsult.core.domain.model;

import com.fiap.easyconsult.core.domain.valueobject.ConsultId;
import com.fiap.easyconsult.core.domain.valueobject.ConsultStatus;
import com.fiap.easyconsult.core.exception.DomainException;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class UpdateConsult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final ConsultId id;
    private Professional professional;
    private String reason;
    private LocalDate date;
    private LocalTime time;
    private ConsultStatus status;

    private UpdateConsult(Builder builder) {
        this.id = builder.id;
        this.professional = builder.professional;
        this.reason = builder.reason;
        this.date = builder.date;
        this.time = builder.time;
        this.status = builder.status;
        validate();
    }

    private void validate() {
        if (id == null) {
            throw new DomainException("Update consult ID cannot be null", "CONSTRAINT_VIOLATION");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public ConsultId getId() {
        return id;
    }
    
    public Professional getProfessional() {
        return professional;
    }

    public String getReason() {
        return reason;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public ConsultStatus getStatus() {
        return status;
    }

    public static class Builder {
        private ConsultId id;
        private Professional professional;
        private String reason;
        private LocalDate date;
        private LocalTime time;
        private ConsultStatus status;

        public Builder id(Long id) {
            this.id = ConsultId.of(id);
            return this;
        }

        public Builder professional(Professional professional) {
            this.professional = professional;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public Builder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public Builder time(LocalTime time) {
            this.time = time;
            return this;
        }

        public Builder status(ConsultStatus status) {
            this.status = status;
            return this;
        }

        public UpdateConsult build() {
            return new UpdateConsult(this);
        }
    }
}
