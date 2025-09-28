package com.fiap.easyconsult.infra.adapter.gateway;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.core.outputport.DeleteGateway;
import com.fiap.easyconsult.infra.entrypoint.mapper.ConsultationMapper;
import com.fiap.easyconsult.infra.exception.GatewayException;
import com.fiap.easyconsult.infra.persistence.repository.ConsultationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DeleteGatewayImpl implements DeleteGateway {

    private final ConsultationRepository repository;
    private final ConsultationMapper mapper;
    private final CacheManager cacheManager;

    public DeleteGatewayImpl(ConsultationRepository repository, 
                            ConsultationMapper mapper,
                            CacheManager cacheManager) {
        this.repository = repository;
        this.mapper = mapper;
        this.cacheManager = cacheManager;
    }

    @Override
    public void delete(Long consultId) {
        log.info("Deleting consultation with ID: {}", consultId);

        try {
            var existingEntity = repository.findById(consultId)
                .orElseThrow(() -> new GatewayException(
                    "Consultation not found with ID: " + consultId, 
                    "CONSULT_NOT_FOUND"));

            var consultToDelete = mapper.toConsultation(existingEntity);
            
            repository.deleteById(consultId);
            
            log.info("Successfully deleted consultation with ID: {}", consultId);
            
            removeFromCache(consultToDelete);

        } catch (DataAccessException ex) {
            log.error("Database error while deleting consultation", ex);
            throw new GatewayException("Failed to delete consultation.", "DATABASE_ERROR");
        }
    }

    private void removeFromCache(Consult deletedConsult) {
        // Remove from individual consult cache
        var cache = cacheManager.getCache("consults");
        if (cache != null) {
            cache.evict(deletedConsult.getId().getValue());
            log.info("Removed consultation from individual cache");
        }

        // Remove from all consults cache
        var allConsultsCache = cacheManager.getCache("allConsults");
        if (allConsultsCache != null) {
            Object raw = allConsultsCache.get("all-consults", Object.class);
            
            if (raw instanceof List<?> rawList) {
                List<Consult> currentList = rawList.stream()
                        .filter(Consult.class::isInstance)
                        .map(Consult.class::cast)
                        .toList();
                
                List<Consult> updatedList = new ArrayList<>(currentList);
                updatedList.removeIf(c -> c.getId().equals(deletedConsult.getId()));
                allConsultsCache.put("all-consults", updatedList);
                log.info("Removed consultation from allConsults cache");
            }
        }

        // Clear filters cache since the consultation was deleted
        var filterCache = cacheManager.getCache("consultsByFilter");
        if (filterCache != null) {
            filterCache.clear();
            log.info("Cleared consultsByFilter cache due to consultation deletion");
        }
    }
}
