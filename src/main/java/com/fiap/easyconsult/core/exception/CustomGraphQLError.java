package com.fiap.easyconsult.core.exception;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CustomGraphQLError implements GraphQLError {

    private final String message;
    private final List<Object> path;
    private final Map<String, Object> extensions;
    private final ErrorClassification errorType;

    public CustomGraphQLError(String message, List<Object> path, Map<String, Object> extensions, ErrorClassification errorType) {
        this.message = message;
        this.path = path;
        this.extensions = extensions;
        this.errorType = errorType;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public List<Object> getPath() {
        return path;
    }

    @Override
    public Map<String, Object> getExtensions() {
        return extensions;
    }

    @Override
    public ErrorClassification getErrorType() {
        return errorType;
    }

    @Override
    public List<SourceLocation> getLocations() {
        return null; // remove "locations" do JSON
    }

    @Override
    public Map<String, Object> toSpecification() {
        Map<String, Object> spec = new LinkedHashMap<>();
        spec.put("message", getMessage());
        if (getPath() != null) spec.put("path", getPath());
        if (getExtensions() != null) spec.put("extensions", getExtensions());
        return spec;
    }
}