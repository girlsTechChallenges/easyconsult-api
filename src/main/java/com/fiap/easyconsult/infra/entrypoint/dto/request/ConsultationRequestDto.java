package com.fiap.easyconsult.infra.entrypoint.dto.request;

import com.fiap.easyconsult.infra.entrypoint.dto.data.PatientDataDto;
import com.fiap.easyconsult.infra.entrypoint.dto.data.ProfessionalDataDto;

import java.time.LocalDateTime;

public record ConsultationRequestDto(
       String reason,
       PatientDataDto patient,
       ProfessionalDataDto professional,
       LocalDateTime dateTime
) {
}
