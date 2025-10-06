package com.fiap.easyconsult.integration.config;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.infra.kafka.service.KafkaMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
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

    @Bean
    @Primary
    public CacheManager testCacheManager() {
        return new ConcurrentMapCacheManager("consults", "allConsults", "consultsByFilter");
    }

    /**
     * Mock do KafkaMessageService para testes.
     * Substitui o serviço real durante os testes de integração para evitar
     * dependência de um servidor Kafka real.
     */
    @Bean
    @Primary
    public KafkaMessageService testKafkaMessageService() {
        return new TestKafkaMessageService();
    }

    /**
     * Implementação mock do KafkaMessageService que simula o comportamento
     * sem realmente enviar mensagens para o Kafka.
     */
    @Slf4j
    public static class TestKafkaMessageService extends KafkaMessageService {
        
        public TestKafkaMessageService() {
            super(null, null);
        }

        @Override
        public void publishConsultMessage(String message) {
            log.info("🧪 [TEST] Simulando publicação de mensagem no Kafka: {}", message);
            // Não faz nada - apenas simula o comportamento
        }

        @Override
        public void publishConsultEvent(Consult consult) {
            log.info("🧪 [TEST] Simulando publicação de consulta no Kafka - ID: {}, Paciente: {}, Profissional: {}", 
                    consult.getId() != null ? consult.getId().getValue() : "N/A", 
                    consult.getPatient().getName(), 
                    consult.getProfessional().getName());
            // Não faz nada - apenas simula o comportamento
        }
    }

}