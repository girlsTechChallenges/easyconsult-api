package com.fiap.easyconsult.infra.kafka.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.infra.kafka.dto.ConsultKafkaMessage;
import com.fiap.easyconsult.infra.kafka.dto.PatientData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;


@Slf4j
@Service
public class KafkaMessageService {
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${app.kafka.topics.consult}")
    private String consultTopic;
    
    @Autowired
    public KafkaMessageService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        log.info("KafkaMessageService inicializado para publicar no tópico de consultas");
    }

    public void publishConsultMessage(String message) {
        try {
            log.info("Publicando mensagem no tópico '{}': {}", consultTopic, message);
            
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(consultTopic, message);
            
            future.whenComplete((result, exception) -> {
                if (exception == null) {
                    log.info("✅ Mensagem publicada com sucesso! Tópico: {}, Partição: {}, Offset: {}", 
                               result.getRecordMetadata().topic(), 
                               result.getRecordMetadata().partition(), 
                               result.getRecordMetadata().offset());
                } else {
                    log.error("❌ Erro ao publicar mensagem no tópico: {}", consultTopic, exception);
                }
            });
            
        } catch (Exception e) {
            log.error("❌ Erro inesperado ao publicar mensagem no tópico: {}", consultTopic, e);
            throw new KafkaException("Falha ao publicar mensagem de consulta", e);
        }
    }

    public void publishConsultEvent(Consult consult) {
        try {            
            var consultMessage = buildConsultMessage(consult);

            String jsonMessage = objectMapper.writeValueAsString(consultMessage);
            
            log.info("📤 Publicando consulta no Kafka - ID: {}, Paciente: {}, Profissional: {}", 
                    consult.getId().getValue(), consult.getPatient().getName(), consult.getProfessional().getName());
            
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(
                consultTopic, 
                consult.getId().getValue().toString(), 
                jsonMessage
            );
            
            future.whenComplete((result, exception) -> {
                if (exception == null) {
                    log.info("✅ Consulta publicada com sucesso no Kafka! ID: {}, Tópico: {}, Partição: {}, Offset: {}", 
                               consult.getId().getValue(),
                               result.getRecordMetadata().topic(), 
                               result.getRecordMetadata().partition(), 
                               result.getRecordMetadata().offset());
                } else {
                    log.error("❌ Erro ao publicar consulta no Kafka - ID: {}", consult.getId().getValue(), exception);
                }
            });
            
        } catch (Exception e) {
            log.error("❌ Erro inesperado ao publicar consulta no Kafka - ID: {}", consult.getId().getValue(), e);
            throw new KafkaException("Falha ao publicar evento de consulta no Kafka", e);
        }
    }

    private ConsultKafkaMessage buildConsultMessage(Consult consult) {
        return new ConsultKafkaMessage(
            consult.getId().getValue().toString(),
            consult.getProfessional().getName(),
            new PatientData(
                consult.getPatient().getName(),
                consult.getPatient().getEmail()
            ),
            consult.getTime().toString(),
            consult.getDate().toString(),
            consult.getReason(),
            consult.getStatus().name()
        );
    }
}