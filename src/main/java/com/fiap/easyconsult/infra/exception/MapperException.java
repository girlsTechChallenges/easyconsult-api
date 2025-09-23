package com.fiap.easyconsult.infra.exception;

public class MapperException extends GraphQLException {

    public MapperException(String message, String code) {
        super(message, code);
    }
}
