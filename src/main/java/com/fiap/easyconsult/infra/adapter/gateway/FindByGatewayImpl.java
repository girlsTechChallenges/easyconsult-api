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
import org.springframework.transaction.annotation.Transactional;

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
//    @Transactional(readOnly = true)
//    @Cacheable(value = "consults-filtered", key = "#filter.toString()")
    public List<Consult> findWithFilters(ConsultationFilter filter) {
        log.info("Finding consultations with filters: {}", filter);

        StringBuilder jpql = new StringBuilder("SELECT c FROM ConsultationEntity c WHERE 1=1");
        Map<String, Object> params = new HashMap<>();

        //TODO: preciso corrigir os filtros
//
//        if (filter.getPatientEmail() != null) {
//            jpql.append(" AND c.patient.email = :patientEmail");
//            params.put("patientEmail", filter.getPatientEmail());
//        }
//
//        if (filter.getProfessionalEmail() != null) {
//            jpql.append(" AND c.professional.email = :profEmail");
//            params.put("profEmail", filter.getProfessionalEmail());
//        }
//
//        if (filter.getStatus() != null) {
//            jpql.append(" AND c.status = :status");
//            params.put("status", filter.getStatus().toString());
//        }

        TypedQuery<ConsultationEntity> query = entityManager.createQuery(jpql.toString(), ConsultationEntity.class);
        params.forEach(query::setParameter);

        List<ConsultationEntity> entities = query.getResultList();
        log.info("Consultations found: {}", entities.size());
        entities.forEach(e -> log.info("Consulta ID: {}, Profissional: {}", e.getId(), e.getProfessional().getEmail()));

        log.info("JPQL: {}", jpql);
        params.forEach((k, v) -> log.info("Param {} = {}", k, v));

        return query.getResultList().stream()
                .map(mapper::toConsultation)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "consults-all")
    public List<Consult> findAll() {
        log.info("Searching all consultations with details");

        var entities = repository.findAllWithDetails();

        return entities.stream()
                .map(mapper::toConsultation)
                .toList();
    }
}