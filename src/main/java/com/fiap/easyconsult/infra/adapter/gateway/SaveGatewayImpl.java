package com.fiap.easyconsult.infra.adapter.gateway;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.core.outputport.SaveGateway;
import com.fiap.easyconsult.infra.entrypoint.mapper.ConsultationMapper;
import com.fiap.easyconsult.infra.exception.GatewayException;
import com.fiap.easyconsult.infra.persistence.entity.ConsultationEntity;
import com.fiap.easyconsult.infra.persistence.repository.ConsultationRepository;
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
    private final ConsultationRepository repository;
    private final ConsultationMapper mapper;
    private final CacheManager cacheManager;

    public SaveGatewayImpl(ConsultationRepository repository,
                           ConsultationMapper mapper,
                           CacheManager cacheManager) {
        this.repository = repository;
        this.mapper = mapper;
        this.cacheManager = cacheManager;
    }

    @Override
    @CachePut(value = "consults", key = "#result.id.value")
    public Consult save(Consult consult) {
        log.info("Saving consultation: {}", consult);

        if (consult.getPatient() == null || consult.getProfessional() == null) {
            throw new GatewayException("Patient or Professional information is missing.", "CONSULT_VALIDATION_ERROR");
        }

        List<ConsultationEntity> existingConsults = repository.findAllByPatientEmail(consult.getPatient().getEmail());

        var dateConflicts = existingConsults.stream()
                .anyMatch(c -> c.getLocalDate().isEqual(consult.getDate()) && c.getLocalTime().equals(consult.getTime()));

        if (dateConflicts) {
            throw new GatewayException("It is not permitted to schedule a new appointment for a date and time that already has an appointment registered.", "CONSULT_VALIDATION_ERROR");
        }

        try {
            var entity = mapper.toConsultationEntity(consult);
            var saved = repository.save(entity);
            var result = mapper.toConsultation(saved);

            log.info("Saved consultation: {}", saved);

            updateAllConsultsCache(result);

            return result;

        } catch (DataAccessException ex) {
            log.error("Database error while saving consultation", ex);
            throw new GatewayException("Failed to persist consultation.", "DATABASE_ERROR");
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
                log.info("Consultation added to cache");
            } else {
                log.info("Consultation already exists in cache");
            }
        } else {
            cache.put(CACHE_ALL_CONSULTS_KEY, List.of(result));
            log.info("Cache initialized with first consultation");
        }
    }
}