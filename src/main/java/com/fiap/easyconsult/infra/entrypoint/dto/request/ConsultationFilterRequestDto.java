package com.fiap.easyconsult.infra.entrypoint.dto.request;

import com.fiap.easyconsult.infra.entrypoint.dto.enums.StatusConsultation;

import java.time.LocalDate;
import java.time.LocalTime;

public record ConsultationFilterRequestDto(
        String patientEmail,
        String professionalEmail,
        LocalDate date,
        LocalTime localTime,
        StatusConsultation status
) {

}
