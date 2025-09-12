package com.fiap.easyconsult.infra.entrypoint.dto.data;

import com.fiap.easyconsult.infra.entrypoint.dto.enums.StatusConsultation;

import java.time.LocalDateTime;

public record Consult(Long id, String nameProfessional, LocalDateTime consultationDate, StatusConsultation statusConsultation, String reason) {
}
