package com.fiap.easyconsult.core.domain.valueobject;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class ConsultId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final Long value;

    private ConsultId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("Consult ID cannot be null");
        }
        this.value = value;
    }

    public static ConsultId of(Long value) {
        return new ConsultId(value);
    }

    public Long getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConsultId)) return false;
        ConsultId that = (ConsultId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}