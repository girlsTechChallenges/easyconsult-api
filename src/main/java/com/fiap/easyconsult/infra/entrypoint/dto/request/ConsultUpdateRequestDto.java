package com.fiap.easyconsult.infra.entrypoint.dto.request;

import com.fiap.easyconsult.infra.entrypoint.dto.data.ProfessionalDataDto;
import com.fiap.easyconsult.infra.entrypoint.dto.enums.StatusConsult;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record ConsultUpdateRequestDto(
        @NotNull(message = "ID cannot be null")
        Long id,
        ProfessionalDataDto professional,
        String reason,
        LocalDate date,
        LocalTime localTime,
        StatusConsult status
) {}