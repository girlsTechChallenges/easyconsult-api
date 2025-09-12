package com.fiap.easyconsult.core.inputport;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.infra.entrypoint.dto.enums.StatusConsultation;

import java.time.LocalDate;
import java.util.Optional;

public interface ConsultQueryUseCase {

    Optional<Consult> findByConsultId(Long consultId);
    Optional<Consult> findByPatientEmail(String email);
    Optional<Consult> findByProfessionalEmail(String email);
    Optional<Consult> findByStatus(StatusConsultation status);
    Optional<Consult> findByConsultationDateBetween(LocalDate start, LocalDate end);

}
