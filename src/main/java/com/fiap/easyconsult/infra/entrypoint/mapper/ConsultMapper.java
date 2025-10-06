package com.fiap.easyconsult.infra.entrypoint.mapper;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.core.domain.model.ConsultFilter;
import com.fiap.easyconsult.core.domain.model.Patient;
import com.fiap.easyconsult.core.domain.model.Professional;
import com.fiap.easyconsult.core.domain.model.UpdateConsult;
import com.fiap.easyconsult.core.domain.valueobject.ConsultDateTime;
import com.fiap.easyconsult.core.domain.valueobject.ConsultStatus;
import com.fiap.easyconsult.infra.entrypoint.dto.data.PatientDataDto;
import com.fiap.easyconsult.infra.entrypoint.dto.request.ConsultFilterRequestDto;
import com.fiap.easyconsult.infra.entrypoint.dto.request.ConsultRequestDto;
import com.fiap.easyconsult.infra.entrypoint.dto.request.ConsultUpdateRequestDto;
import com.fiap.easyconsult.infra.entrypoint.dto.response.ConsultResponseDto;
import com.fiap.easyconsult.infra.persistence.entity.ConsultEntity;
import com.fiap.easyconsult.infra.persistence.entity.PatientEntity;
import com.fiap.easyconsult.infra.persistence.entity.ProfessionalEntity;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.fiap.easyconsult.infra.entrypoint.dto.enums.StatusConsult.SCHEDULED;
import static com.fiap.easyconsult.infra.entrypoint.dto.enums.StatusConsult.valueOf;

@Component
public class ConsultMapper {

    public Consult toConsult(ConsultRequestDto request) {
        var dateTime = ConsultDateTime.of(request.date(), request.localTime());
        dateTime.validateFutureDateTime();

        return new Consult.Builder()
                .dateTime(dateTime.getDate(), dateTime.getTime())
                .patient(new Patient.Builder()
                        .name(request.patient().name())
                        .email(request.patient().email())
                        .build())
                .professional(new Professional.Builder()
                        .name(request.professional().name())
                        .email(request.professional().email())
                        .build())
                .reason(request.reason()).build();
    }

    public ConsultEntity toConsultEntity(Consult consult){
        var patientEntity = new PatientEntity(null, consult.getPatient().getName(), consult.getPatient().getEmail());
        var professionalEntity = new ProfessionalEntity(
                null,
                consult.getProfessional().getName(),
                consult.getProfessional().getEmail());

        var entity = new ConsultEntity();
        entity.setReason(consult.getReason());
        entity.setPatient(patientEntity);
        entity.setProfessional(professionalEntity);
        entity.setLocalTime(consult.getTime());
        entity.setLocalDate(consult.getDate());
        entity.setStatus(SCHEDULED.name());
        return entity;

    }

    public Consult toConsult(ConsultEntity entity){
        var patient = new Patient.Builder()
                .id(entity.getId())
                .name(entity.getPatient().getName())
                .email(entity.getPatient().getEmail())
                .build();
        var professional = new Professional.Builder()
                .id(entity.getId())
                .name(entity.getProfessional().getName())
                .email(entity.getProfessional().getEmail())
                .build();
        return new Consult.Builder()
                .id(entity.getId())
                .dateTime(entity.getLocalDate(), entity.getLocalTime())
                .patient(patient)
                .professional(professional)
                .reason(entity.getReason())
                .status(ConsultStatus.valueOf(entity.getStatus()))
                .build();
    }

    public ConsultFilter toConsultFilter(ConsultFilterRequestDto request) {
        return new ConsultFilter(
                null,
                request.patientEmail(),
                request.professionalEmail(),
                request.status() != null ? ConsultStatus.valueOf(request.status().name()) : null,
                request.localTime(),
                request.date()
        );
    }

    public List<ConsultResponseDto> toConsultResponse(List<Consult> consults) {
        return consults.stream().map(this::toConsultResponse).toList();
    }


    public ConsultResponseDto toConsultResponse(Consult consult) {
        return new ConsultResponseDto(
                consult.getId().getValue(),
                new PatientDataDto(
                        consult.getPatient().getName(),
                        consult.getPatient().getEmail()),
                consult.getProfessional().getName(),
                consult.getTime(),
                consult.getDate(),
                valueOf(consult.getStatus().name()),
                consult.getReason()
        );
    }

    public UpdateConsult toUpdateConsult(ConsultUpdateRequestDto request) {
        var builder = UpdateConsult.builder()
                .id(request.id());

        if (request.reason() != null) {
            builder.reason(request.reason());
        }
        if (request.date() != null) {
            builder.date(request.date());
        }
        if (request.localTime() != null) {
            builder.time(request.localTime());
        }
        if (request.status() != null) {
            builder.status(ConsultStatus.valueOf(request.status().name()));
        }

        return builder.build();
    }
}