package com.fiap.easyconsult.infra.entrypoint.dto.request;

import com.fiap.easyconsult.infra.entrypoint.dto.data.PatientDataDto;
import com.fiap.easyconsult.infra.entrypoint.dto.data.ProfessionalDataDto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ConsultationRequestDto(
       String reason,
       PatientDataDto patient,
       ProfessionalDataDto professional,
       LocalDate date,
       LocalTime localTime
) {
}
