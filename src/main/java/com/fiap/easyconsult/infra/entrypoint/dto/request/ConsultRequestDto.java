package com.fiap.easyconsult.infra.entrypoint.dto.request;

import com.fiap.easyconsult.infra.entrypoint.dto.data.PatientDataDto;
import com.fiap.easyconsult.infra.entrypoint.dto.data.ProfessionalDataDto;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.LocalTime;

public record ConsultRequestDto(
       String reason,
       @Valid
       PatientDataDto patient,
       @Valid
       ProfessionalDataDto professional,
       LocalDate date,
       LocalTime localTime
) {}