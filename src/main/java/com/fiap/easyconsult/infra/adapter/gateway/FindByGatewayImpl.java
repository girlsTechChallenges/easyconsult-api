package com.fiap.easyconsult.infra.adapter.gateway;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.core.domain.model.Patient;
import com.fiap.easyconsult.core.domain.model.Professional;
import com.fiap.easyconsult.core.inputport.ConsultQueryUseCase;
import com.fiap.easyconsult.infra.entrypoint.mapper.ConsultationMapper;
import com.fiap.easyconsult.infra.persistence.repository.ConsultationRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FindByGatewayImpl implements ConsultQueryUseCase {

    private final ConsultationRepository repository;
    private final ConsultationMapper mapper;

    public FindByGatewayImpl(ConsultationRepository repository, ConsultationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Consult> findByConsultId(Long consultId) {
        //TODO: validar se o paciente e o profissional existem antes de buscar a consulta
        // Se não existirem, lançar uma exceção
        // Se existirem, buscar a consulta
        // tratar exceção de consulta não encontrada
        var resp = repository.findById(consultId).orElseThrow(() -> new RuntimeException("Consulta não encontrada"));
        return Optional.ofNullable(mapper.toConsultation(resp));
    }

    @Override
    public Optional<Patient> findByPatientId(String patientId) {
        //TODO: implementar busca de paciente por ID
        // Se não existir, lançar uma exceção
        // Se existir, retornar o paciente
        return Optional.empty();
    }

    @Override
    public Optional<Professional> findByProfessionalId(String professionalId) {
        //TODO: implementar busca de profissional por ID
        // Se não existir, lançar uma exceção
        // Se existir, retornar o profissional
        return Optional.empty();
    }

    @Override
    public Optional<Consult> findAllByConsult(String consultId) {
        //TODO: implementar busca de todas as consultas
        // Se não existir, lançar uma exceção
        // Se existir, retornar a lista de consultas
        return Optional.empty();
    }

    @Override
    public Optional<Patient> findAllByPatient(String patientId) {
        //TODO: implementar busca de todos os pacientes
        // Se não existir, lançar uma exceção
        // Se existir, retornar a lista de pacientes
        return Optional.empty();
    }

    @Override
    public Optional<Professional> findAllByProfessional(String professionalId) {
        //TODO: implementar busca de todos os profissionais
        // Se não existir, lançar uma exceção
        // Se existir, retornar a lista de profissionais
        return Optional.empty();
    }

    @Override
    public Optional<Consult> findAll(String consultationId) {
        //TODO: implementar busca de todas as consultas
        // Se não existir, lançar uma exceção
        // Se existir, retornar a lista de consultas
        return Optional.empty();
    }
}
