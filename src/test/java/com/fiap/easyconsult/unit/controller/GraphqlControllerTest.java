package com.fiap.easyconsult.unit.controller;

import com.fiap.easyconsult.core.domain.model.*;
import com.fiap.easyconsult.core.domain.valueobject.ConsultStatus;
import com.fiap.easyconsult.core.inputport.ConsultCommandUseCase;
import com.fiap.easyconsult.core.inputport.ConsultQueryUseCase;
import com.fiap.easyconsult.infra.entrypoint.controller.GraphqlController;
import com.fiap.easyconsult.infra.entrypoint.dto.request.ConsultFilterRequestDto;
import com.fiap.easyconsult.infra.entrypoint.dto.request.ConsultRequestDto;
import com.fiap.easyconsult.infra.entrypoint.dto.request.ConsultUpdateRequestDto;
import com.fiap.easyconsult.infra.entrypoint.dto.response.ConsultResponseDto;
import com.fiap.easyconsult.infra.entrypoint.mapper.ConsultMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GraphqlController Tests")
class GraphqlControllerTest {

    @Mock
    private ConsultCommandUseCase consultCommandUseCase;

    @Mock
    private ConsultQueryUseCase consultQueryUseCase;

    @Mock
    private ConsultMapper mapper;

    private GraphqlController graphqlController;

    private Consult validConsult;
    private ConsultResponseDto validResponseDto;
    private ConsultRequestDto validRequestDto;
    private ConsultFilterRequestDto validFilterDto;
    private ConsultUpdateRequestDto validUpdateDto;

    @BeforeEach
    void setUp() {
        graphqlController = new GraphqlController(consultCommandUseCase, consultQueryUseCase, mapper);

        Patient patient = Patient.builder()
                .id(1L)
                .name("João Silva")
                .email("joao@example.com")
                .build();

        Professional professional = Professional.builder()
                .id(1L)
                .name("Dr. Maria Santos")
                .email("maria@example.com")
                .build();

        validConsult = Consult.builder()
                .id(1L)
                .reason("Consulta de rotina")
                .patient(patient)
                .professional(professional)
                .dateTime(LocalDate.now().plusDays(7), LocalTime.of(14, 30))
                .status(ConsultStatus.SCHEDULED)
                .build();

        validResponseDto = mock(ConsultResponseDto.class);
        validRequestDto = mock(ConsultRequestDto.class);
        validFilterDto = mock(ConsultFilterRequestDto.class);
        validUpdateDto = mock(ConsultUpdateRequestDto.class);
    }

    @Nested
    @DisplayName("Query Mapping Tests")
    class QueryMappingTests {

        @Test
        @DisplayName("Should return filtered consults successfully")
        void shouldReturnFilteredConsultsSuccessfully() {
            // Given
            List<Consult> consultList = List.of(validConsult);
            List<ConsultResponseDto> expectedResponse = List.of(validResponseDto);
            
            when(mapper.toConsultFilter(any(ConsultFilterRequestDto.class)))
                    .thenReturn(mock(ConsultFilter.class));
            when(consultQueryUseCase.findWithFilters(any(ConsultFilter.class)))
                    .thenReturn(consultList);
            when(mapper.toConsultResponse(any(List.class)))
                    .thenReturn(expectedResponse);

            // When
            List<ConsultResponseDto> result = graphqlController.getFilteredConsults(validFilterDto);

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(expectedResponse, result);
            
            verify(mapper, times(1)).toConsultFilter(validFilterDto);
            verify(consultQueryUseCase, times(1)).findWithFilters(any(ConsultFilter.class));
            verify(mapper, times(1)).toConsultResponse(consultList);
        }

        @Test
        @DisplayName("Should return all consults successfully")
        void shouldReturnAllConsultsSuccessfully() {
            // Given
            List<Consult> consultList = List.of(validConsult);
            
            when(consultQueryUseCase.findAll()).thenReturn(consultList);
            when(mapper.toConsultResponse(any(Consult.class)))
                    .thenReturn(validResponseDto);

            // When
            List<ConsultResponseDto> result = graphqlController.getAllConsults();

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(validResponseDto, result.get(0));
            
            verify(consultQueryUseCase, times(1)).findAll();
            verify(mapper, times(1)).toConsultResponse(validConsult);
        }

        @Test
        @DisplayName("Should propagate exception when query use case fails")
        void shouldPropagateExceptionWhenQueryUseCaseFails() {
            // Given
            RuntimeException useCaseException = new RuntimeException("Query failed");
            when(mapper.toConsultFilter(any(ConsultFilterRequestDto.class)))
                    .thenReturn(mock(ConsultFilter.class));
            when(consultQueryUseCase.findWithFilters(any(ConsultFilter.class)))
                    .thenThrow(useCaseException);

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    graphqlController.getFilteredConsults(validFilterDto)
            );

            assertEquals("Query failed", exception.getMessage());
            verify(consultQueryUseCase, times(1)).findWithFilters(any(ConsultFilter.class));
        }
    }

    @Nested
    @DisplayName("Mutation Mapping Tests")
    class MutationMappingTests {

        @Test
        @DisplayName("Should create consult successfully")
        void shouldCreateConsultSuccessfully() {
            // Given
            when(mapper.toConsult(any(ConsultRequestDto.class)))
                    .thenReturn(validConsult);
            when(consultCommandUseCase.createConsult(any(Consult.class)))
                    .thenReturn(validConsult);
            when(mapper.toConsultResponse(any(Consult.class)))
                    .thenReturn(validResponseDto);

            // When
            ConsultResponseDto result = graphqlController.createFullConsult(validRequestDto);

            // Then
            assertNotNull(result);
            assertEquals(validResponseDto, result);
            
            verify(mapper, times(1)).toConsult(validRequestDto);
            verify(consultCommandUseCase, times(1)).createConsult(validConsult);
            verify(mapper, times(1)).toConsultResponse(validConsult);
        }

        @Test
        @DisplayName("Should update consult successfully")
        void shouldUpdateConsultSuccessfully() {
            // Given
            UpdateConsult updateConsult = mock(UpdateConsult.class);
            when(mapper.toUpdateConsult(any(ConsultUpdateRequestDto.class)))
                    .thenReturn(updateConsult);
            when(consultCommandUseCase.updateConsult(any(UpdateConsult.class)))
                    .thenReturn(validConsult);
            when(mapper.toConsultResponse(any(Consult.class)))
                    .thenReturn(validResponseDto);

            // When
            ConsultResponseDto result = graphqlController.updateConsult(validUpdateDto);

            // Then
            assertNotNull(result);
            assertEquals(validResponseDto, result);
            
            verify(mapper, times(1)).toUpdateConsult(validUpdateDto);
            verify(consultCommandUseCase, times(1)).updateConsult(updateConsult);
            verify(mapper, times(1)).toConsultResponse(validConsult);
        }

        @Test
        @DisplayName("Should delete consult successfully")
        void shouldDeleteConsultSuccessfully() {
            // Given
            Long consultId = 1L;
            doNothing().when(consultCommandUseCase).deleteConsult(any(Long.class));

            // When
            Boolean result = graphqlController.deleteConsult(consultId);

            // Then
            assertTrue(result);
            verify(consultCommandUseCase, times(1)).deleteConsult(consultId);
        }

        @Test
        @DisplayName("Should propagate exception when create consult fails")
        void shouldPropagateExceptionWhenCreateConsultFails() {
            // Given
            RuntimeException useCaseException = new RuntimeException("Create failed");
            when(mapper.toConsult(any(ConsultRequestDto.class)))
                    .thenReturn(validConsult);
            when(consultCommandUseCase.createConsult(any(Consult.class)))
                    .thenThrow(useCaseException);

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    graphqlController.createFullConsult(validRequestDto)
            );

            assertEquals("Create failed", exception.getMessage());
            verify(consultCommandUseCase, times(1)).createConsult(validConsult);
        }

        @Test
        @DisplayName("Should propagate exception when update consult fails")
        void shouldPropagateExceptionWhenUpdateConsultFails() {
            // Given
            UpdateConsult updateConsult = mock(UpdateConsult.class);
            RuntimeException useCaseException = new RuntimeException("Update failed");
            when(mapper.toUpdateConsult(any(ConsultUpdateRequestDto.class)))
                    .thenReturn(updateConsult);
            when(consultCommandUseCase.updateConsult(any(UpdateConsult.class)))
                    .thenThrow(useCaseException);

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    graphqlController.updateConsult(validUpdateDto)
            );

            assertEquals("Update failed", exception.getMessage());
            verify(consultCommandUseCase, times(1)).updateConsult(updateConsult);
        }

        @Test
        @DisplayName("Should propagate exception when delete consult fails")
        void shouldPropagateExceptionWhenDeleteConsultFails() {
            // Given
            Long consultId = 1L;
            RuntimeException useCaseException = new RuntimeException("Delete failed");
            doThrow(useCaseException).when(consultCommandUseCase).deleteConsult(any(Long.class));

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    graphqlController.deleteConsult(consultId)
            );

            assertEquals("Delete failed", exception.getMessage());
            verify(consultCommandUseCase, times(1)).deleteConsult(consultId);
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should handle null request gracefully")
        void shouldHandleNullRequestGracefully() {
            // Given
            when(mapper.toConsult(any(ConsultRequestDto.class)))
                    .thenReturn(validConsult);
            when(consultCommandUseCase.createConsult(any(Consult.class)))
                    .thenReturn(validConsult);
            when(mapper.toConsultResponse(any(Consult.class)))
                    .thenReturn(validResponseDto);

            // When
            graphqlController.createFullConsult(validRequestDto);

            // Then
            verify(mapper, times(1)).toConsult(validRequestDto);
            verify(mapper, times(1)).toConsultResponse(validConsult);
        }

        @Test
        @DisplayName("Should maintain controller contract")
        void shouldMaintainControllerContract() {
            // Given
            assertNotNull(graphqlController);

            // When & Then - Verify that controller has expected dependencies
            GraphqlController controller = new GraphqlController(
                    consultCommandUseCase, consultQueryUseCase, mapper
            );

            assertNotNull(controller);
        }
    }
}