package com.fiap.easyconsult.infra.adapter.gateway;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.core.outputport.DeleteGateway;
import com.fiap.easyconsult.infra.entrypoint.mapper.ConsultMapper;
import com.fiap.easyconsult.infra.exception.GatewayException;
import com.fiap.easyconsult.infra.persistence.repository.ConsultRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DeleteGatewayImpl implements DeleteGateway {

    private final ConsultRepository repository;
    private final ConsultMapper mapper;
    private final CacheManager cacheManager;

    public DeleteGatewayImpl(ConsultRepository repository, 
                            ConsultMapper mapper,
                            CacheManager cacheManager) {
        this.repository = repository;
        this.mapper = mapper;
        this.cacheManager = cacheManager;
    }

    @Override
    public void delete(Long consultId) {
        log.info("Deleting consult with ID: {}", consultId);

        try {
            var existingEntity = repository.findById(consultId)
                .orElseThrow(() -> new GatewayException(
                    "Consult not found with ID: " + consultId, 
                    "CONSULT_NOT_FOUND"));

            var consultToDelete = mapper.toConsult(existingEntity);
            
            repository.deleteById(consultId);
            
            log.info("Successfully deleted consult with ID: {}", consultId);
            
            removeFromCache(consultToDelete);

        } catch (DataAccessException ex) {
            log.error("Database error while deleting consult", ex);
            throw new GatewayException("Failed to delete consult.", "DATABASE_ERROR");
        }
    }

    private void removeFromCache(Consult deletedConsult) {
        // Remove from individual consult cache
        var cache = cacheManager.getCache("consults");
        if (cache != null) {
            cache.evict(deletedConsult.getId().getValue());
            log.info("Removed consult from individual cache");
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
                log.info("Removed consult from allConsults cache");
            }
        }

        // Clear filters cache since the consult was deleted
        var filterCache = cacheManager.getCache("consultsByFilter");
        if (filterCache != null) {
            filterCache.clear();
            log.info("Cleared consultsByFilter cache due to consult deletion");
        }
    }
}
