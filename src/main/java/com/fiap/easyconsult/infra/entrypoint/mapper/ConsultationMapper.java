package com.fiap.easyconsult.infra.entrypoint.mapper;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.core.domain.model.Patient;
import com.fiap.easyconsult.core.domain.model.Professional;
import com.fiap.easyconsult.core.domain.valueobject.DateAndTime;
import com.fiap.easyconsult.infra.entrypoint.dto.data.PatientDataDto;
import com.fiap.easyconsult.infra.entrypoint.dto.enums.StatusConsultation;
import com.fiap.easyconsult.infra.entrypoint.dto.request.ConsultationRequestDto;
import com.fiap.easyconsult.infra.entrypoint.dto.response.ConsultationResponseDto;
import com.fiap.easyconsult.infra.persistence.entity.ConsultationEntity;
import com.fiap.easyconsult.infra.persistence.entity.PatientEntity;
import com.fiap.easyconsult.infra.persistence.entity.ProfessionalEntity;
import org.springframework.stereotype.Component;

@Component
public class ConsultationMapper {

    // método para converter de ConsultationRequestDto para Consultation - controller
    public Consult toConsultation(ConsultationRequestDto request) {
        var patientData = new Patient(null, request.patient().name(), request.patient().email());
        var professionalData = new Professional(null, request.professional().name(), request.professional().email());

        return new Consult(
                null,
                request.reason(),
                patientData,
                professionalData,
                new DateAndTime(request.dateTime()));
    }

    // método para converter de Consultation para ConsultationEntity - gateway
    public ConsultationEntity toConsultationEntity(Consult consult){
        var patientEntity = new PatientEntity(null, consult.getPatient().getName(), consult.getPatient().getEmail());
        var professionalEntity = new ProfessionalEntity(null, consult.getProfessional().getName(), consult.getProfessional().getEmail());

        var entity = new ConsultationEntity();
        entity.setReason(consult.getReason());
        entity.setPatient(patientEntity);
        entity.setProfessional(professionalEntity);
        entity.setDateTime(consult.getDateAndTime().getValue());

        return entity;

    }

    // método para converter de ConsultationEntity para Consultation - gateway
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

        return new Consult(entity.getId(), entity.getReason(), patient, professional, dateAndTime);
    }

    // método para converter de Consultation para ConsultationResponseDto - controller
    //TODO: ajustar status
    public ConsultationResponseDto toConsultationResponse(Consult consult) {
        return new ConsultationResponseDto(
                consult.getId(),
                new PatientDataDto(
                        consult.getPatient().getName(),
                        consult.getPatient().getEmail()),
                consult.getProfessional().getName(),
                consult.getDateAndTime().getValue(),
                StatusConsultation.SCHEDULED,
                consult.getReason()
        );
    }
}
