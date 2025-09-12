package com.fiap.easyconsult.infra.adapter.gateway;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.core.inputport.ConsultQueryUseCase;
import com.fiap.easyconsult.infra.entrypoint.dto.enums.StatusConsultation;
import com.fiap.easyconsult.infra.entrypoint.mapper.ConsultationMapper;
import com.fiap.easyconsult.infra.persistence.repository.ConsultationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    public Optional<Consult> findByPatientEmail(String email) {
        //TODO: implementar busca por email do paciente
        // Se não encontrar, lançar uma exceção
        // Se encontrar, retornar a consulta
        return Optional.empty();
    }

    @Override
    public Optional<Consult> findByProfessionalEmail(String email) {
        //TODO: implementar busca por email do profissional
        // Se não encontrar, lançar uma exceção
        // Se encontrar, retornar a consulta
        return Optional.empty();
    }

    @Override
    public Optional<Consult> findByStatus(StatusConsultation status) {
        //TODO: implementar busca por status da consulta
        // Se não encontrar, lançar uma exceção
        // Se encontrar, retornar a consulta
        return Optional.empty();
    }

    @Override
    public Optional<Consult> findByConsultationDateBetween(LocalDate start, LocalDate end) {
        //TODO: implementar busca por data da consulta
        // Se não encontrar, lançar uma exceção
        // Se encontrar, retornar a consulta
        return Optional.empty();
    }
}
