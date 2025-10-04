package com.fiap.easyconsult.infra.kafka.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuração Spring para o Kafka Producer
 * 
 * Esta classe configura o Kafka Producer usando Spring Kafka,
 * que fornece abstrações mais robustas e integração nativa com Spring Boot.
 * Detecta automaticamente se está rodando em Docker ou localmente.
 * 
 * @author EasyConsult Team
 * @version 1.0
 */
@Configuration
public class KafkaProducerConfig {
    
    /**
     * Configura e cria o ProducerFactory para o Kafka.
     * 
     * @return ProducerFactory configurado para envio de mensagens
     */
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        
        // Detecta automaticamente se está rodando em Docker ou localmente
        String kafkaBootstrapServers = detectKafkaBootstrapServers();
        
        // Configurações essenciais
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        
        // Configurações de confiabilidade
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        
        // Configurações de performance
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 5);
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        
        // Configurações de retry
        configProps.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);
        
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Configura e cria o KafkaTemplate para envio de mensagens.
     * 
     * @return KafkaTemplate configurado
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
    
    /**
     * Detecta automaticamente o endereço do Kafka baseado no ambiente.
     * Em Docker usa 'kafka:29092', localmente usa 'localhost:9092'.
     * 
     * @return String com o endereço do bootstrap servers
     */
    private String detectKafkaBootstrapServers() {
        // Verifica se está rodando em Docker através de variáveis de ambiente
        String dockerEnv = System.getenv("DOCKER_ENV");
        String springProfile = System.getProperty("spring.profiles.active");
        
        if ("true".equals(dockerEnv) || "prod".equals(springProfile)) {
            return "kafka:29092"; // Endereço interno do Docker
        }
        
        // Verifica se existe o hostname 'kafka' (indicativo de Docker)
        try {
            java.net.InetAddress.getByName("kafka");
            return "kafka:29092"; // Se conseguiu resolver, usa endereço Docker
        } catch (java.net.UnknownHostException e) {
            return "localhost:9092"; // Senão usa localhost
        }
    }
}