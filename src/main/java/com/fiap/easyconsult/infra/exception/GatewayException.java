package com.fiap.easyconsult.infra.exception;

public class GatewayException extends GraphQLException {

    public GatewayException(String message, String code) {
        super(message, code);
    }
}
