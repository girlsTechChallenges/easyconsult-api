package com.fiap.easyconsult.infra.adapter.gateway;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.core.outputport.SaveGateway;
import com.fiap.easyconsult.infra.entrypoint.mapper.ConsultMapper;
import com.fiap.easyconsult.infra.exception.GatewayException;
import com.fiap.easyconsult.infra.kafka.service.KafkaMessageService;
import com.fiap.easyconsult.infra.persistence.entity.ConsultEntity;
import com.fiap.easyconsult.infra.persistence.repository.ConsultRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class SaveGatewayImpl implements SaveGateway {

    private static final String CACHE_ALL_CONSULTS_KEY = "all-consults";
    private final ConsultRepository repository;
    private final ConsultMapper mapper;
    private final CacheManager cacheManager;
    private final KafkaMessageService kafkaMessageService;

    public SaveGatewayImpl(ConsultRepository repository,
                           ConsultMapper mapper,
                           CacheManager cacheManager,
                           KafkaMessageService kafkaMessageService) {
        this.repository = repository;
        this.mapper = mapper;
        this.cacheManager = cacheManager;
        this.kafkaMessageService = kafkaMessageService;
    }

    @Override
    @CachePut(value = "consults", key = "#result.id.value")
    public Consult save(Consult consult) {
        log.info("Saving consult: {}", consult);

        checkPatientAndProfessional(consult);

        List<ConsultEntity> existingConsults = repository.findAllByPatientEmail(consult.getPatient().getEmail());

        checkForDateConflicts(consult, existingConsults);

        try {
            var entity = mapper.toConsultEntity(consult);
            var saved = repository.save(entity);
            var result = mapper.toConsult(saved);

            log.info("Saved consult: {}", saved);
            kafkaMessageService.publishConsultEvent(result);

            updateAllConsultsCache(result);

            return result;

        } catch (DataAccessException ex) {
            log.error("Database error while saving consult", ex);
            throw new GatewayException("Failed to persist consult.", "DATABASE_ERROR");
        }
    }

    private void checkPatientAndProfessional(Consult consult) {
        if (consult.getPatient() == null || consult.getProfessional() == null) {
            throw new GatewayException("Patient or Professional information is missing.", "CONSULT_VALIDATION_ERROR");
        }
    }

    private void checkForDateConflicts(Consult consult, List<ConsultEntity> existingConsults) {
        var dateConflicts = existingConsults.stream()
                .anyMatch(c -> c.getLocalDate().isEqual(consult.getDate()) && c.getLocalTime().equals(consult.getTime()));

        if (dateConflicts) {
            throw new GatewayException("It is not permitted to schedule a new appointment for a date and time that already has an appointment registered.", "CONSULT_VALIDATION_ERROR");
        }
    }

    private void updateAllConsultsCache(Consult result) {
        var cache = cacheManager.getCache("allConsults");
        if (cache == null) {
            log.warn("Cache 'allConsults' not found");
            return;
        }

        Object raw = cache.get(CACHE_ALL_CONSULTS_KEY, Object.class);
        List<Consult> currentList = null;

        if (raw instanceof List<?> rawList) {
            currentList = rawList.stream()
                    .filter(Consult.class::isInstance)
                    .map(Consult.class::cast)
                    .toList();
        }

        if (currentList != null) {
            boolean alreadyExists = currentList.stream()
                    .anyMatch(c -> c.getId().equals(result.getId()));
            if (!alreadyExists) {
                List<Consult> updatedList = new ArrayList<>(currentList);
                updatedList.add(result);
                cache.put(CACHE_ALL_CONSULTS_KEY, updatedList);
                log.info("Consult added to cache");
            } else {
                log.info("Consult already exists in cache");
            }
        } else {
            cache.put(CACHE_ALL_CONSULTS_KEY, List.of(result));
            log.info("Cache initialized with first consult");
        }
    }
}