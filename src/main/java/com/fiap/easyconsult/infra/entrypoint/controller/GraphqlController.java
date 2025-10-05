package com.fiap.easyconsult.infra.entrypoint.controller;

import com.fiap.easyconsult.core.inputport.ConsultCommandUseCase;
import com.fiap.easyconsult.core.inputport.ConsultQueryUseCase;
import com.fiap.easyconsult.infra.entrypoint.dto.request.ConsultFilterRequestDto;
import com.fiap.easyconsult.infra.entrypoint.dto.request.ConsultRequestDto;
import com.fiap.easyconsult.infra.entrypoint.dto.request.ConsultUpdateRequestDto;
import com.fiap.easyconsult.infra.entrypoint.dto.response.ConsultResponseDto;
import com.fiap.easyconsult.infra.entrypoint.mapper.ConsultMapper;
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
    private final ConsultMapper mapper;


    public GraphqlController(ConsultCommandUseCase consultCommandUseCase, ConsultQueryUseCase consultQueryUseCase, ConsultMapper mapper) {
        this.consultCommandUseCase = consultCommandUseCase;
        this.consultQueryUseCase = consultQueryUseCase;
        this.mapper = mapper;
    }

    @QueryMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_medico','SCOPE_enfermeiro') or (hasAuthority('SCOPE_paciente') and #input.patientEmail == authentication.name)")
    public List<ConsultResponseDto> getFilteredConsults(@Argument("filter")  @Valid ConsultFilterRequestDto input) {
        var rep = consultQueryUseCase.findWithFilters(mapper.toConsultFilter(input));
        return mapper.toConsultResponse(rep);
    }

    @QueryMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_medico','SCOPE_enfermeiro')")
    public List<ConsultResponseDto> getAllConsults() {
        var rep = consultQueryUseCase.findAll();
        return rep.stream().map(mapper::toConsultResponse).toList();
    }

    @MutationMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_enfermeiro')")
    public ConsultResponseDto createFullConsult(@Argument("input") @Valid ConsultRequestDto input) {
        var consultDto = mapper.toConsult(input);
        var consultResponse = consultCommandUseCase.createConsult(consultDto);
        return mapper.toConsultResponse(consultResponse);
    }

    @MutationMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_medico')")
    public ConsultResponseDto updateConsult(@Argument("input") @Valid ConsultUpdateRequestDto input) {
        var updateConsultDto = mapper.toUpdateConsult(input);
        var consultResponse = consultCommandUseCase.updateConsult(updateConsultDto);
        return mapper.toConsultResponse(consultResponse);
    }

    @MutationMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_medico')")
    public Boolean deleteConsult(@Argument("id") Long id) {
        consultCommandUseCase.deleteConsult(id);
        return true;
    }
}
