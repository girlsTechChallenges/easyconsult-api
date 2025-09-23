package com.fiap.easyconsult.infra.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class GraphQLExceptionHandler implements DataFetcherExceptionHandler {

    @Override
    public CompletableFuture<DataFetcherExceptionHandlerResult> handleException(DataFetcherExceptionHandlerParameters handlerParameters) {
        Throwable exception = handlerParameters.getException();

        GraphQLError error;
        if (exception instanceof GraphQLError graphqlError) {
            error = graphqlError;
        } else {
            error = GraphqlErrorBuilder.newError()
                    .message("Unexpected error occurred")
                    .location(handlerParameters.getSourceLocation())
                    .path(handlerParameters.getPath())
                    .build();
        }

        DataFetcherExceptionHandlerResult result = DataFetcherExceptionHandlerResult.newResult().error(error).build();
        return CompletableFuture.completedFuture(result);
    }
}

