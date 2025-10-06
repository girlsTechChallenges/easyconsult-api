package com.fiap.easyconsult.infra.adapter.gateway;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.core.domain.model.ConsultFilter;
import com.fiap.easyconsult.core.outputport.FindByGateway;
import com.fiap.easyconsult.infra.entrypoint.mapper.ConsultMapper;
import com.fiap.easyconsult.infra.persistence.entity.ConsultEntity;
import com.fiap.easyconsult.infra.persistence.repository.ConsultRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FindByGatewayImpl implements FindByGateway {

    @PersistenceContext
    private EntityManager entityManager;

    private final ConsultRepository repository;
    private final ConsultMapper mapper;

    public FindByGatewayImpl(ConsultRepository repository, ConsultMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Cacheable(value = "consultsByFilter", key = "#filter.hashCode()")
    public List<Consult> findWithFilters(ConsultFilter filter) {
        log.info("Searching consults with filters: {}", filter);

        try {
            String consultBase = "SELECT c FROM ConsultEntity c WHERE 1=1";
            Map<String, Object> parameter = new HashMap<>();
            List<String> condition = new ArrayList<>();

            addCondition(filter.getPatientEmail(), "c.patient.email", "patientEmail", condition, parameter);
            addCondition(filter.getProfessionalEmail(), "c.professional.email", "profEmail", condition, parameter);
            addCondition(filter.getStatus(), "c.status", "status", condition, parameter, true);
            addCondition(filter.getDate(), "c.localDate", "localDate", condition, parameter);

            String jpql = condition.isEmpty() ? consultBase : consultBase + " AND " + String.join(" AND ", condition);
            TypedQuery<ConsultEntity> query = entityManager.createQuery(jpql, ConsultEntity.class);
            parameter.forEach(query::setParameter);

            List<Consult> result = query.getResultList().stream()
                    .map(mapper::toConsult)
                    .toList();

            log.info("Found {} consults with filters", result.size());
            return result;

        } catch (Exception e) {
            log.error("Error while filtering consults", e);
            throw e;
        }
    }

    @Override
    public List<Consult> findAll() {
        log.info("Searching all consults with details");

        try {
            var entities = repository.findAllWithDetails();
            log.info("Found {} consults", entities.size());

            return entities.stream()
                    .map(mapper::toConsult)
                    .toList();

        } catch (Exception e) {
            log.error("Error while fetching all consults", e);
            throw e;
        }
    }

    private void addCondition(Object valor, String campo, String nameParameter,
                              List<String> condition, Map<String, Object> parameter) {
        addCondition(valor, campo, nameParameter, condition, parameter, false);
    }

    private void addCondition(Object valor, String campo, String nameParameter,
                              List<String> condition, Map<String, Object> parameter, boolean converterParaString) {
        if (valor != null) {
            condition.add(campo + " = :" + nameParameter);
            parameter.put(nameParameter, converterParaString ? valor.toString() : valor);
        }
    }
}