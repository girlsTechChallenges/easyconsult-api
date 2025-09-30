package com.fiap.easyconsult.infra.exception;

public class GatewayException extends RuntimeException {
    private final String code;

    public GatewayException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}


