package com.fiap.easyconsult.infra.entrypoint.mapper;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.core.domain.model.ConsultationFilter;
import com.fiap.easyconsult.core.domain.model.Patient;
import com.fiap.easyconsult.core.domain.model.Professional;
import com.fiap.easyconsult.infra.entrypoint.dto.data.PatientDataDto;
import com.fiap.easyconsult.infra.entrypoint.dto.request.ConsultationFilterRequestDto;
import com.fiap.easyconsult.infra.entrypoint.dto.request.ConsultationRequestDto;
import com.fiap.easyconsult.infra.entrypoint.dto.response.ConsultationResponseDto;
import com.fiap.easyconsult.infra.exception.MapperException;
import com.fiap.easyconsult.infra.persistence.entity.ConsultationEntity;
import com.fiap.easyconsult.infra.persistence.entity.PatientEntity;
import com.fiap.easyconsult.infra.persistence.entity.ProfessionalEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

import static com.fiap.easyconsult.infra.entrypoint.dto.enums.StatusConsultation.SCHEDULED;
import static com.fiap.easyconsult.infra.entrypoint.dto.enums.StatusConsultation.valueOf;

@Component
public class ConsultationMapper {

    public Consult toConsultation(ConsultationRequestDto request) {
        var patientData = new Patient(null, request.patient().email(), request.patient().name());
        var professionalData = new Professional(null, request.professional().email(), request.professional().name());

        return new Consult(
                null,
                request.reason(),
                null,
                patientData,
                professionalData,
                request.localTime(),
                verifyDate(request.date()));
    }

    public ConsultationEntity toConsultationEntity(Consult consult){
        var patientEntity = new PatientEntity(null, consult.getPatient().getName(), consult.getPatient().getEmail());
        var professionalEntity = new ProfessionalEntity(
                null,
                consult.getProfessional().getName(),
                consult.getProfessional().getEmail());

        var entity = new ConsultationEntity();
        entity.setReason(consult.getReason());
        entity.setPatient(patientEntity);
        entity.setProfessional(professionalEntity);
        entity.setLocalTime(consult.getLocalTime());
        entity.setLocalDate(consult.getDate());
        entity.setStatus(SCHEDULED.name());
        return entity;

    }

    public Consult toConsultation(ConsultationEntity entity){
        var patient = new Patient(
                entity.getPatient().getId(),
                entity.getPatient().getEmail(),
                entity.getPatient().getName());
        var professional = new Professional(
                entity.getProfessional().getId(),
                entity.getProfessional().getEmail(),
                entity.getProfessional().getName());

        return new Consult(
                entity.getId(),
                entity.getReason(),
                entity.getStatus(),
                patient,
                professional,
                entity.getLocalTime(),
                entity.getLocalDate());
    }

    public ConsultationFilter toConsultationFilter(ConsultationFilterRequestDto request) {
        return new ConsultationFilter(
                null,
                request.patientEmail(),
                request.professionalEmail(),
                request.status(),
                request.localTime(),
                request.date()
        );
    }

    public List<ConsultationResponseDto> toConsultationResponse(List<Consult> consults) {
        return consults.stream().map(this::toConsultationResponse).toList();
    }


    public ConsultationResponseDto toConsultationResponse(Consult consult) {
        return new ConsultationResponseDto(
                consult.getId(),
                new PatientDataDto(
                        consult.getPatient().getName(),
                        consult.getPatient().getEmail()),
                consult.getProfessional().getName(),
                consult.getLocalTime(),
                consult.getDate(),
                valueOf(consult.getStatus()),
                consult.getReason()
        );
    }

    private LocalDate verifyDate(LocalDate date) {
        if (date.isBefore(LocalDate.now())) {
            throw new MapperException("Query data cannot be older than current data", "ERROR_INCONSISTENT_DATE");
        }
        return date;
    }
}
