package com.fiap.easyconsult.infra.kafka.dto;

public record ConsultationKafkaMessage(
        String id,
        String nameProfessional,
        PatientData patient,
        String localTime,
        String date,
        String reason,
        String statusConsulation
) {}
