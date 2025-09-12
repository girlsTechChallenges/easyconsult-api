package com.fiap.easyconsult.infra.entrypoint.dto.response;

import com.fiap.easyconsult.infra.entrypoint.dto.data.Consult;

import java.util.List;

public record PatientConsultResponse(
        Long id,
        String name,
        String email,
        List<Consult> listConsult) {
}
