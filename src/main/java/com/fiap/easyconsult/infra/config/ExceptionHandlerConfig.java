package com.fiap.easyconsult.infra.config;

import com.fiap.easyconsult.infra.exception.GraphQLExceptionHandler;
import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ExceptionHandlerConfig {

    @Bean
    public GraphQlSourceBuilderCustomizer customExceptionHandler(GraphQLExceptionHandler handler) {
        return builder -> builder.configureGraphQl(graphQlBuilder ->
                graphQlBuilder.defaultDataFetcherExceptionHandler(handler)
        );
    }
}
