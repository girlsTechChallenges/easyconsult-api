package com.fiap.easyconsult.infra.entrypoint.dto.response;

import com.fiap.easyconsult.infra.entrypoint.dto.data.PatientDataDto;
import com.fiap.easyconsult.infra.entrypoint.dto.enums.StatusConsult;

import java.time.LocalDate;
import java.time.LocalTime;

public record ConsultResponseDto(
        Long id,
        PatientDataDto patient,
        String nameProfessional,
        LocalTime localTime,
        LocalDate date,
        StatusConsult statusConsult,
        String reason
) {}