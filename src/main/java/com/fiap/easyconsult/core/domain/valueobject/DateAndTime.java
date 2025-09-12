package com.fiap.easyconsult.core.domain.valueobject;

import java.time.LocalDateTime;

public class DateAndTime {

    private final LocalDateTime dataHora;

    public DateAndTime(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public boolean isPast() {
        return dataHora.isBefore(LocalDateTime.now());
    }

    public LocalDateTime getValue() {
        return dataHora;
    }

}
