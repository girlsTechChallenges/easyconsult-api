package com.fiap.easyconsult.infra.entrypoint.dto.response;

import com.fiap.easyconsult.infra.entrypoint.dto.data.PatientDataDto;
import com.fiap.easyconsult.infra.entrypoint.dto.enums.StatusConsultation;

import java.time.LocalDateTime;

public record ConsultationResponseDto(
        Long id,
        PatientDataDto patient,
        String nameProfessional,
        LocalDateTime consultationDate,
        StatusConsultation statusConsultation,
        String reason
) {
}
