package com.fiap.easyconsult.infra.config;

import graphql.scalars.ExtendedScalars;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class CustomScalarConfig {

    @Bean
    public RuntimeWiringConfigurer dateTimeScalarConfigurer() {
        return wiringBuilder -> wiringBuilder
                .scalar(ExtendedScalars.LocalTime);
    }

    @Bean
    public RuntimeWiringConfigurer dateScalarConfigurer() {
        return wiringBuilder -> wiringBuilder
                .scalar(ExtendedScalars.Date);
    }
}
