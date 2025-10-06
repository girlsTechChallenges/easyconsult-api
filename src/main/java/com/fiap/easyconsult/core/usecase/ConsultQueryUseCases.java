package com.fiap.easyconsult.core.usecase;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.core.domain.model.ConsultFilter;
import com.fiap.easyconsult.core.inputport.ConsultQueryUseCase;
import com.fiap.easyconsult.core.outputport.FindByGateway;
import com.fiap.easyconsult.infra.exception.GatewayException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ConsultQueryUseCases implements ConsultQueryUseCase {

    private final FindByGateway gateway;

    public ConsultQueryUseCases(FindByGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public List<Consult> findWithFilters(ConsultFilter consultFilter) {
        return Optional.ofNullable(gateway.findWithFilters(consultFilter))
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new GatewayException("No consults found for the given filter.", "CONSULT_NOT_FOUND"));
    }

    @Override
    public List<Consult> findAll() {
        return Optional.ofNullable(gateway.findAll())
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new GatewayException("No consults found.", "CONSULT_NOT_FOUND"));
    }
}
