package com.fiap.easyconsult.core.domain.valueobject;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class DateAndTime implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final LocalDateTime dataHora;

    public DateAndTime(LocalDateTime dataHora) {
        this.dataHora = Objects.requireNonNull(dataHora, "dataHora não pode ser nulo");
    }

    public LocalDateTime getValue() {
        return dataHora;
    }

    public LocalDateTime endOfDay() {
        return dataHora.withHour(23).withMinute(59).withSecond(59);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DateAndTime that)) return false;
        return dataHora.equals(that.dataHora);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataHora);
    }
}