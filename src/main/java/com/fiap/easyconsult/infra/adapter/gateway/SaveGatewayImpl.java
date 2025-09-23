package com.fiap.easyconsult.infra.adapter.gateway;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.core.outputport.SaveGateway;
import com.fiap.easyconsult.infra.entrypoint.mapper.ConsultationMapper;
import com.fiap.easyconsult.infra.exception.GatewayException;
import com.fiap.easyconsult.infra.persistence.entity.ConsultationEntity;
import com.fiap.easyconsult.infra.persistence.repository.ConsultationRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CachePut;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class SaveGatewayImpl implements SaveGateway {

    private final ConsultationRepository repository;
    private final ConsultationMapper mapper;

    public SaveGatewayImpl(ConsultationRepository repository, ConsultationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @CachePut(value = "consults", key = "#result.id")
    public Consult save(Consult consult) {
        log.info("Saving consultation: {}", consult);

        if (consult.getPatient() == null || consult.getProfessional() == null) {
            throw new GatewayException("Patient or Professional information is missing.", "CONSULT_VALIDATION_ERROR");
        }

        List<ConsultationEntity> existingConsults = repository.findAllByPatientEmail(consult.getPatient().getEmail());

        if (!existingConsults.isEmpty()) {
            throw new GatewayException("Scheduling with email " + consult.getPatient().getEmail() + " already exists.", "CONSULT_VALIDATION_ERROR");
        }

        try {
            var entity = mapper.toConsultationEntity(consult);
            var saved = repository.save(entity);

            log.info("Saved consultation: {}", saved);

            return  mapper.toConsultation(saved);

        } catch (DataAccessException ex) {
            log.error("Database error while saving consultation", ex);
            throw new GatewayException("Failed to persist consultation.", "DATABASE_ERROR");
        }
    }
}
