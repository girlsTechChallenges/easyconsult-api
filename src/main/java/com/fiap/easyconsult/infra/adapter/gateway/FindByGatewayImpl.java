package com.fiap.easyconsult.infra.adapter.gateway;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.core.domain.model.ConsultationFilter;
import com.fiap.easyconsult.core.outputport.FindByGateway;
import com.fiap.easyconsult.infra.entrypoint.mapper.ConsultationMapper;
import com.fiap.easyconsult.infra.persistence.entity.ConsultationEntity;
import com.fiap.easyconsult.infra.persistence.repository.ConsultationRepository;
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

    private final ConsultationRepository repository;
    private final ConsultationMapper mapper;

    public FindByGatewayImpl(ConsultationRepository repository, ConsultationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Cacheable(value = "consultsByFilter", key = "#filter.hashCode()")
    public List<Consult> findWithFilters(ConsultationFilter filter) {
        log.info("Searching consultations with filters: {}", filter);

        try {
            String consultBase = "SELECT c FROM ConsultationEntity c WHERE 1=1";
            Map<String, Object> parameter = new HashMap<>();
            List<String> condition = new ArrayList<>();

            addCondition(filter.getPatientEmail(), "c.patient.email", "patientEmail", condition, parameter);
            addCondition(filter.getProfessionalEmail(), "c.professional.email", "profEmail", condition, parameter);
            addCondition(filter.getStatus(), "c.status", "status", condition, parameter, true);
            addCondition(filter.getDate(), "c.localDate", "localDate", condition, parameter);

            String jpql = consultBase + String.join(" AND ", condition);
            TypedQuery<ConsultationEntity> query = entityManager.createQuery(jpql, ConsultationEntity.class);
            parameter.forEach(query::setParameter);

            List<Consult> result = query.getResultList().stream()
                    .map(mapper::toConsultation)
                    .toList();

            log.info("Found {} consultations with filters", result.size());
            return result;

        } catch (Exception e) {
            log.error("Error while filtering consultations", e);
            throw e;
        }
    }

    @Override
    @Cacheable("allConsults")
    public List<Consult> findAll() {
        log.info("Searching all consultations with details");

        try {
            var entities = repository.findAllWithDetails();
            log.info("Found {} consultations", entities.size());

            return entities.stream()
                    .map(mapper::toConsultation)
                    .toList();

        } catch (Exception e) {
            log.error("Error while fetching all consultations", e);
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
            condition.add(" AND " + campo + " = :" + nameParameter);
            parameter.put(nameParameter, converterParaString ? valor.toString() : valor);
        }
    }
}