package com.fiap.easyconsult.infra.kafka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fiap.easyconsult.infra.kafka.service.KafkaMessageService;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador REST para publicar mensagens de consultas no Kafka caso queira testar
 * esse produtor manualmente.
 * 
 * Este controlador fornece endpoint específico para o tópico de consultas
 * que será consumido pelo notification-api.
 */
@Slf4j
@RestController
@RequestMapping("/api/kafka")
public class KafkaController {

    private final KafkaMessageService kafkaMessageService;
    
    @Autowired
    public KafkaController(KafkaMessageService kafkaMessageService) {
        this.kafkaMessageService = kafkaMessageService;
    }

    @PostMapping("/publish-consult")
    public ResponseEntity<String> publishConsultMessage(@RequestParam String message) {
        try {
            kafkaMessageService.publishConsultMessage(message);
            log.info("Mensagem de consulta publicada com sucesso: {}", message);
            return ResponseEntity.ok(
                String.format("Mensagem de consulta publicada com sucesso: %s", message));
        } catch (Exception e) {
            log.error("Erro ao publicar mensagem de consulta: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body("Erro ao publicar mensagem de consulta: " + e.getMessage());
        }
    }
}