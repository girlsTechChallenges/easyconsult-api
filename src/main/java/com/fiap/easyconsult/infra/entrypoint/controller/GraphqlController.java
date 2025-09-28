package com.fiap.easyconsult.infra.entrypoint.controller;

import com.fiap.easyconsult.core.inputport.ConsultCommandUseCase;
import com.fiap.easyconsult.core.inputport.ConsultQueryUseCase;
import com.fiap.easyconsult.infra.entrypoint.dto.request.ConsultationFilterRequestDto;
import com.fiap.easyconsult.infra.entrypoint.dto.request.ConsultationRequestDto;
import com.fiap.easyconsult.infra.entrypoint.dto.response.ConsultationResponseDto;
import com.fiap.easyconsult.infra.entrypoint.mapper.ConsultationMapper;
import jakarta.validation.Valid;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
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
    @PreAuthorize("hasAnyAuthority('SCOPE_medico','SCOPE_enfermeiro') or (hasAuthority('SCOPE_paciente') and #input.patientEmail == authentication.name)")
    public List<ConsultationResponseDto> getFilteredConsultations(@Argument("filter")  @Valid ConsultationFilterRequestDto input) {
        var rep = consultQueryUseCase.findWithFilters(mapper.toConsultationFilter(input));
        return mapper.toConsultationResponse(rep);
    }

    @QueryMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_medico','SCOPE_enfermeiro')")
    public List<ConsultationResponseDto> getAllConsultations() {
        var rep = consultQueryUseCase.findAll();
        return rep.stream().map(mapper::toConsultationResponse).toList();
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public ConsultationResponseDto createFullConsultation(@Argument("input") @Valid ConsultationRequestDto input) {
        var consultationDto = mapper.toConsultation(input);
        var consultationResponse = consultCommandUseCase.createConsultation(consultationDto);
        return mapper.toConsultationResponse(consultationResponse);
    }
}
