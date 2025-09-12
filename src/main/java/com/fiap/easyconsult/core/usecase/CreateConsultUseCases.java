package com.fiap.easyconsult.core.usecase;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.core.domain.model.UpdateConsult;
import com.fiap.easyconsult.core.inputport.ConsultCommandUseCase;
import com.fiap.easyconsult.core.outputport.SaveGateway;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CreateConsultUseCases implements ConsultCommandUseCase {

    private final SaveGateway gateway;

    public CreateConsultUseCases(SaveGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public Consult createConsultation(Consult consult) {
        //TODO: validar se objeto está preenchido corretamente
        return gateway.save(consult);
    }

    @Override
    public Optional<UpdateConsult> updateConsultation(UpdateConsult updateConsult) {
        //TODO: implementar updateConsultation
        // validar se objeto está preenchido corretamente

        //TODO: primeiro verificar se há no banco através da consulta por id
        //TODO: se existir, atualizar

        return Optional.empty();
    }
}
