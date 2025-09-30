package com.fiap.easyconsult.core.exception;

import graphql.ErrorClassification;

public enum ErrorCode implements ErrorClassification {
    BUSINESS_RULE("Regra de negócio violada"),
    VALIDATION_ERROR("Erro de validação"),
    INTERNAL_ERROR("Erro interno inesperado"),
    INVALID_ARGUMENT("Argumento inválido"),
    FORBIDDEN("Acesso negado");

    private final String defaultMessage;

    ErrorCode(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}