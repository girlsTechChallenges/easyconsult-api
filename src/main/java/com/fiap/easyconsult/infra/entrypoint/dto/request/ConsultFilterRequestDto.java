package com.fiap.easyconsult.infra.entrypoint.dto.request;

import com.fiap.easyconsult.infra.entrypoint.dto.enums.StatusConsult;

import java.time.LocalDate;
import java.time.LocalTime;

public record ConsultFilterRequestDto(
        String patientEmail,
        String professionalEmail,
        LocalDate date,
        LocalTime localTime,
        StatusConsult status
) {}