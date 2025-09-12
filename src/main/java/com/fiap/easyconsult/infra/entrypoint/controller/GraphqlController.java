package com.fiap.easyconsult.infra.entrypoint.controller;

import com.fiap.easyconsult.core.inputport.ConsultCommandUseCase;
import com.fiap.easyconsult.core.inputport.ConsultQueryUseCase;
import com.fiap.easyconsult.infra.entrypoint.dto.request.ConsultationRequestDto;
import com.fiap.easyconsult.infra.entrypoint.dto.response.ConsultationResponseDto;
import com.fiap.easyconsult.infra.entrypoint.mapper.ConsultationMapper;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
public class GraphqlController {

    private final ConsultCommandUseCase consultCommandUseCase;
    private final ConsultQueryUseCase consultQueryUseCase;
    private final ConsultationMapper mapper;


    public GraphqlController(ConsultCommandUseCase consultCommandUseCase, ConsultQueryUseCase consultQueryUseCase, ConsultationMapper mapper) {
        this.consultCommandUseCase = consultCommandUseCase;
        this.consultQueryUseCase = consultQueryUseCase;
        this.mapper = mapper;
    }

    @QueryMapping
    public Optional<ConsultationResponseDto> getConsultationById(@Argument Long id) {
        var rep = consultQueryUseCase.findByConsultId(id);
        return rep.map(mapper::toConsultationResponse);
    }

    @MutationMapping
    public ConsultationResponseDto createFullConsultation(@Argument ConsultationRequestDto input) {
        var consultationDto = mapper.toConsultation(input);
        var consultationResponse = consultCommandUseCase.createConsultation(consultationDto);
        return mapper.toConsultationResponse(consultationResponse);
    }


}
