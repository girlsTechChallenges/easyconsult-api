package com.fiap.easyconsult.core.domain.valueobject;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

public class ConsultDateTime implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final LocalDate date;
    private final LocalTime time;

    private ConsultDateTime(LocalDate date, LocalTime time) {
        if (date == null || time == null) {
            throw new IllegalArgumentException("Date and time cannot be null");
        }
        this.date = date;
        this.time = time;
    }

    public static ConsultDateTime of(LocalDate date, LocalTime time) {
        return new ConsultDateTime(date, time);
    }

    /**
     * Validates that the date/time is in the future.
     * Should be called explicitly when scheduling a new consultation.
     */
    public void validateFutureDateTime() {
        if (toLocalDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Consult date/time cannot be in the past");
        }
    }

    public boolean isPast() {
        return toLocalDateTime().isBefore(LocalDateTime.now());
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public LocalDateTime toLocalDateTime() {
        return LocalDateTime.of(date, time);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConsultDateTime)) return false;
        ConsultDateTime that = (ConsultDateTime) o;
        return Objects.equals(date, that.date) && Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, time);
    }

    @Override
    public String toString() {
        return toLocalDateTime().toString();
    }
}