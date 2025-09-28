package com.fiap.easyconsult.integration.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * Configuração de teste para simplificar autenticação e autorização nos testes de integração.
 * Esta configuração fornece usuários de teste em memória para os testes de integração.
 * 
 * IMPORTANTE: Não sobrescreve SecurityFilterChain para evitar conflitos com SecurityConfig principal.
 * A configuração principal já permite acesso ao GraphQL durante os testes.
 */
@TestConfiguration
@Profile("test")
public class TestConfig {



    @Bean
    @Primary
    public UserDetailsService testUserDetailsService() {
        UserDetails medico = User.builder()
                .username("medico@test.com")
                .password(passwordEncoder().encode("password"))
                .authorities("SCOPE_medico")
                .build();

        UserDetails enfermeiro = User.builder()
                .username("enfermeiro@test.com")
                .password(passwordEncoder().encode("password"))
                .authorities("SCOPE_enfermeiro")
                .build();

        UserDetails paciente = User.builder()
                .username("paciente@test.com")
                .password(passwordEncoder().encode("password"))
                .authorities("SCOPE_paciente")
                .build();

        return new InMemoryUserDetailsManager(medico, enfermeiro, paciente);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}