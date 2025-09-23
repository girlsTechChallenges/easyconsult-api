package com.fiap.easyconsult.infra.exception;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public abstract class GraphQLException extends RuntimeException implements GraphQLError {

    private final Map<String, Object> extensions = new LinkedHashMap<>();

    public GraphQLException(String message, String code) {
        super(message);
        extensions.put("message", message);
        extensions.put("code", code);
        extensions.put("timestamp", System.currentTimeMillis());
    }

    @Override
    public Map<String, Object> getExtensions() {
        return extensions;
    }

    @Override
    public ErrorType getErrorType() {
        return null;
    }

    @Override
    public String getMessage() {
        return (String) extensions.get("message");
    }

    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }
}