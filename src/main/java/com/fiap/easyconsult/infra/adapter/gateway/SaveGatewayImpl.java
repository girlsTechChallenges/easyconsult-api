package com.fiap.easyconsult.infra.adapter.gateway;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.core.outputport.SaveGateway;
import com.fiap.easyconsult.infra.entrypoint.mapper.ConsultationMapper;
import com.fiap.easyconsult.infra.persistence.repository.ConsultationRepository;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

@Service
public class SaveGatewayImpl implements SaveGateway {

    private final ConsultationRepository repository;
    private final ConsultationMapper mapper;

    public SaveGatewayImpl(ConsultationRepository repository, ConsultationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @CachePut(value = "consults", key = "#result.id")
    public Consult save(Consult consult) {
        //TODO: validar se o paciente e o profissional existem antes de salvar a consulta
        // Se não existirem, lançar uma exceção
        // Se existirem, salvar a consulta
        // regra do status da consulta
        var resp = repository.save(mapper.toConsultationEntity(consult));
        return mapper.toConsultation(resp);
    }
}
