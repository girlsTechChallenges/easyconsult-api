package com.fiap.easyconsult.core.domain.valueobject;

public enum ConsultStatus {
    SCHEDULED,
    CANCELLED,
    CARRIED_OUT;

    public boolean canBeCancelled() {
        return this == SCHEDULED;
    }

    public boolean isFinalized() {
        return switch (this) {
            case CANCELLED, CARRIED_OUT -> true;
            default -> false;
        };
    }
}