package com.fiap.easyconsult.core.domain.valueobject;

public enum ConsultStatus {
    SCHEDULED,
    CANCELLED,
    COMPLETED,
    NO_SHOW;

    public boolean canBeCancelled() {
        return this == SCHEDULED;
    }

    public boolean isFinalized() {
        return this == COMPLETED || this == CANCELLED || this == NO_SHOW;
    }
}
