package com.fiap.easyconsult.infra.entrypoint.mapper;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.core.domain.model.ConsultationFilter;
import com.fiap.easyconsult.core.domain.model.Patient;
import com.fiap.easyconsult.core.domain.model.Professional;
import com.fiap.easyconsult.core.domain.valueobject.DateAndTime;
import com.fiap.easyconsult.infra.entrypoint.dto.data.PatientDataDto;
import com.fiap.easyconsult.infra.entrypoint.dto.enums.StatusConsultation;
import com.fiap.easyconsult.infra.entrypoint.dto.request.ConsultationFilterRequestDto;
import com.fiap.easyconsult.infra.entrypoint.dto.request.ConsultationRequestDto;
import com.fiap.easyconsult.infra.entrypoint.dto.response.ConsultationResponseDto;
import com.fiap.easyconsult.infra.persistence.entity.ConsultationEntity;
import com.fiap.easyconsult.infra.persistence.entity.PatientEntity;
import com.fiap.easyconsult.infra.persistence.entity.ProfessionalEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConsultationMapper {

    public Consult toConsultation(ConsultationRequestDto request) {
        var patientData = new Patient(null, request.patient().name(), request.patient().email());
        var professionalData = new Professional(null, request.professional().name(), request.professional().email());

        return new Consult(
                null,
                request.reason(),
                null,
                patientData,
                professionalData,
                new DateAndTime(request.dateTime()));
    }

    public ConsultationEntity toConsultationEntity(Consult consult){
        var patientEntity = new PatientEntity(null, consult.getPatient().getName(), consult.getPatient().getEmail());
        var professionalEntity = new ProfessionalEntity(null, consult.getProfessional().getName(), consult.getProfessional().getEmail());

        var entity = new ConsultationEntity();
        entity.setReason(consult.getReason());
        entity.setPatient(patientEntity);
        entity.setProfessional(professionalEntity);
        entity.setDateTime(consult.getDateAndTime().getValue());
        entity.setStatus(consult.getStatus());
        return entity;

    }

    public Consult toConsultation(ConsultationEntity entity){
        var patient = new Patient(
                entity.getId(),
                entity.getPatient().getName(),
                entity.getPatient().getEmail());
        var professional = new Professional(
                entity.getProfessional().getId(),
                entity.getProfessional().getName(),
                entity.getProfessional().getEmail());
        var dateAndTime = new DateAndTime(entity.getDateTime());

        return new Consult(entity.getId(), entity.getReason(), entity.getStatus(), patient, professional, dateAndTime);
    }

    public ConsultationFilter toConsultationFilter(ConsultationFilterRequestDto request) {
        return new ConsultationFilter(
                null,
                request.patientEmail(),
                request.professionalEmail(),
                request.status(), null, null
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
                consult.getDateAndTime().getValue(),
                StatusConsultation.SCHEDULED,     //TODO: ajustar status de acordo com a lógica de negócio
                consult.getReason()
        );
    }
}
