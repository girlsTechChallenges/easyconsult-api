package com.fiap.easyconsult.infra.entrypoint.dto.data;

import jakarta.validation.constraints.Email;

public record PatientDataDto (
        String name,
        @Email
        String email) {
}
