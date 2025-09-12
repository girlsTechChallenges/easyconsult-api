package com.fiap.easyconsult.core.inputport;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.core.domain.model.Patient;
import com.fiap.easyconsult.core.domain.model.Professional;

import java.util.Optional;

public interface ConsultQueryUseCase {

    Optional<Consult> findByConsultId(Long consultId);

    Optional<Patient> findByPatientId(String patientId);

    Optional<Professional> findByProfessionalId(String professionalId);

    Optional<Consult> findAllByConsult(String consultId);

    Optional<Patient> findAllByPatient(String patientId);

    Optional<Professional> findAllByProfessional(String professionalId);

    Optional<Consult> findAll(String consultationId);

}
