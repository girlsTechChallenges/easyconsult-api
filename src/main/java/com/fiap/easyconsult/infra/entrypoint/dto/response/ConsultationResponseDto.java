package com.fiap.easyconsult.infra.entrypoint.dto.response;

import com.fiap.easyconsult.infra.entrypoint.dto.data.PatientDataDto;
import com.fiap.easyconsult.infra.entrypoint.dto.enums.StatusConsultation;

import java.time.LocalDate;
import java.time.LocalTime;

public record ConsultationResponseDto(
        Long id,
        PatientDataDto patient,
        String nameProfessional,
        LocalTime localTime,
        LocalDate date,
        StatusConsultation statusConsultation,
        String reason
) {
}
