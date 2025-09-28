package com.fiap.easyconsult.core.exception;

import com.fiap.easyconsult.infra.exception.GatewayException;
import graphql.GraphQLError;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.ConstraintViolationException;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class GraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        String traceId = UUID.randomUUID().toString();
        String timestamp = LocalDateTime.now().toString();
        List<Object> path = env.getExecutionStepInfo().getPath().toList();

        Throwable rootCause = getRootCause(ex);

        if (rootCause instanceof ConstraintViolationException cve) {
            return handleConstraintViolation(cve, path, timestamp, traceId);
        }

        if (rootCause instanceof MethodArgumentNotValidException manve) {
            return handleMethodArgumentNotValid(manve, path, timestamp, traceId);
        }

        if (rootCause instanceof GatewayException ge) {
            return buildBusinessError(ge.getMessage(), ge.getCode(), path, timestamp, traceId, ErrorCode.BUSINESS_RULE);
        }

        if (rootCause instanceof DomainException de) {
            return buildBusinessError(de.getMessage(), de.getCode(), path, timestamp, traceId, ErrorCode.BUSINESS_RULE);
        }

        if (rootCause instanceof IllegalArgumentException iae) {
            return buildBusinessError(iae.getMessage(), "INVALID_ARGUMENT", path, timestamp, traceId, ErrorCode.INVALID_ARGUMENT);
        }

        return buildInternalError(path, timestamp, traceId);
    }

    private GraphQLError handleConstraintViolation(ConstraintViolationException ex, List<Object> path, String timestamp, String traceId) {
        String message = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .findFirst()
                .orElse("Erro de validação");
        return buildBusinessError(message, "VALIDATION_ERROR", path, timestamp, traceId, ErrorCode.VALIDATION_ERROR);
    }

    private GraphQLError handleMethodArgumentNotValid(MethodArgumentNotValidException ex, List<Object> path, String timestamp, String traceId) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Erro de validação");
        return buildBusinessError(message, "VALIDATION_ERROR", path, timestamp, traceId, ErrorCode.VALIDATION_ERROR);
    }

    private GraphQLError buildBusinessError(String message, String code, List<Object> path, String timestamp, String traceId, ErrorCode classification) {
        return new CustomGraphQLError(
                message,
                path,
                Map.of(
                        "code", code,
                        "classification", classification.name(),
                        "timestamp", timestamp,
                        "traceId", traceId
                ),
                classification
        );
    }

    private GraphQLError buildInternalError(List<Object> path, String timestamp, String traceId) {
        return new CustomGraphQLError(
                "Ocorreu um erro inesperado. Tente novamente mais tarde.",
                path,
                Map.of(
                        "classification", ErrorCode.INTERNAL_ERROR.name(),
                        "timestamp", timestamp,
                        "traceId", traceId
                ),
                ErrorCode.INTERNAL_ERROR
        );
    }

    private Throwable getRootCause(Throwable throwable) {
        Throwable cause = throwable.getCause();
        if (cause != null && cause != throwable) {
            return getRootCause(cause);
        }
        return throwable;
    }
}