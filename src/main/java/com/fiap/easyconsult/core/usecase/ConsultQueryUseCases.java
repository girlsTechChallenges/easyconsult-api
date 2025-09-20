package com.fiap.easyconsult.core.usecase;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.core.domain.model.ConsultationFilter;
import com.fiap.easyconsult.core.inputport.ConsultQueryUseCase;
import com.fiap.easyconsult.core.outputport.FindByGateway;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConsultQueryUseCases implements ConsultQueryUseCase {

    private final FindByGateway gateway;

    public ConsultQueryUseCases(FindByGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public List<Consult> findWithFilters(ConsultationFilter consultationFilter) {
        return gateway.findWithFilters(consultationFilter);
    }

    @Override
    public List<Consult> findAll() {
        return gateway.findAll();
    }

}
