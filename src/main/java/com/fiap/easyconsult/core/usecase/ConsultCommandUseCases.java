package com.fiap.easyconsult.core.usecase;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.core.inputport.ConsultCommandUseCase;
import com.fiap.easyconsult.core.outputport.SaveGateway;
import org.springframework.stereotype.Component;

@Component
public class ConsultCommandUseCases implements ConsultCommandUseCase {

    private final SaveGateway gateway;

    public ConsultCommandUseCases(SaveGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public Consult createConsultation(Consult consult) {
        return gateway.save(consult);
    }

}
