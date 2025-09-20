package com.fiap.easyconsult.infra.entrypoint.dto.request;

import com.fiap.easyconsult.infra.entrypoint.dto.enums.StatusConsultation;

public record ConsultationFilterRequestDto(
        String patientEmail,
        String professionalEmail,
        StatusConsultation status
) {

}
