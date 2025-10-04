package com.fiap.easyconsult.infra.adapter.gateway;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.core.domain.model.UpdateConsult;
import com.fiap.easyconsult.core.outputport.UpdateGateway;
import com.fiap.easyconsult.infra.entrypoint.mapper.ConsultationMapper;
import com.fiap.easyconsult.infra.exception.GatewayException;
import com.fiap.easyconsult.infra.kafka.service.KafkaMessageService;
import com.fiap.easyconsult.infra.persistence.entity.ConsultationEntity;
import com.fiap.easyconsult.infra.persistence.repository.ConsultationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UpdateGatewayImpl implements UpdateGateway {

    private static final String CACHE_ALL_CONSULTS_KEY = "all-consults";

    private final ConsultationRepository repository;
    private final ConsultationMapper mapper;
    private final CacheManager cacheManager;
    private final KafkaMessageService kafkaMessageService;

    public UpdateGatewayImpl(ConsultationRepository repository, 
                            ConsultationMapper mapper,
                            CacheManager cacheManager,
                            KafkaMessageService kafkaMessageService) {
        this.repository = repository;
        this.mapper = mapper;
        this.cacheManager = cacheManager;
        this.kafkaMessageService = kafkaMessageService;
    }

    @Override
    @CachePut(value = "consults", key = "#result.id.value")
    public Consult update(UpdateConsult updateConsult) {
        log.info("Updating consultation with ID: {}", updateConsult.getId().getValue());

        try {
            var existingEntity = repository.findById(updateConsult.getId().getValue())
                .orElseThrow(() -> new GatewayException(
                    "Consultation not found with ID: " + updateConsult.getId().getValue(), 
                    "CONSULT_NOT_FOUND"));

            applyUpdateConsultData(updateConsult, existingEntity);

            var savedEntity = repository.save(existingEntity);
            var result = mapper.toConsultation(savedEntity);
            kafkaMessageService.publishConsultationEvent(result);

            log.info("Successfully updated consultation: {}", result);
            
            updateAllConsultsCache(result);
            
            return result;

        } catch (DataAccessException ex) {
            log.error("Database error while updating consultation", ex);
            throw new GatewayException("Failed to update consultation.", "DATABASE_ERROR");
        }
    }

    private void applyUpdateConsultData(UpdateConsult updateConsult, ConsultationEntity existingEntity) {
        // Update only non-null fields
        if (updateConsult.getReason() != null) {
            existingEntity.setReason(updateConsult.getReason());
        }
        if (updateConsult.getDate() != null) {
            existingEntity.setLocalDate(updateConsult.getDate());
        }
        if (updateConsult.getTime() != null) {
            existingEntity.setLocalTime(updateConsult.getTime());
        }
        if (updateConsult.getStatus() != null) {
            existingEntity.setStatus(updateConsult.getStatus().name());
        }
    }

    private void updateAllConsultsCache(Consult result) {
        var cache = cacheManager.getCache(CACHE_ALL_CONSULTS_KEY);
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
            // Remove old version and add updated version
            List<Consult> updatedList = new ArrayList<>(currentList);
            updatedList.removeIf(c -> c.getId().equals(result.getId()));
            updatedList.add(result);
            cache.put(CACHE_ALL_CONSULTS_KEY, updatedList);
            log.info("Consultation updated in cache");
        } else {
            cache.put(CACHE_ALL_CONSULTS_KEY, List.of(result));
            log.info("Cache initialized with updated consultation");
        }

        // Clear filters cache since the consultation was updated
        var filterCache = cacheManager.getCache("consultsByFilter");
        if (filterCache != null) {
            filterCache.clear();
            log.info("Cleared consultsByFilter cache due to consultation update");
        }
    }
}
