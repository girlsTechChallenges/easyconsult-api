package com.fiap.easyconsult.unit.controller;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.core.domain.model.ConsultationFilter;
import com.fiap.easyconsult.core.domain.model.Patient;
import com.fiap.easyconsult.core.domain.model.Professional;
import com.fiap.easyconsult.core.domain.model.UpdateConsult;
import com.fiap.easyconsult.core.inputport.ConsultCommandUseCase;
import com.fiap.easyconsult.core.inputport.ConsultQueryUseCase;
import com.fiap.easyconsult.infra.entrypoint.controller.GraphqlController;
import com.fiap.easyconsult.infra.entrypoint.dto.request.ConsultationFilterRequestDto;
import com.fiap.easyconsult.infra.entrypoint.dto.request.ConsultationRequestDto;
import com.fiap.easyconsult.infra.entrypoint.dto.request.ConsultationUpdateRequestDto;
import com.fiap.easyconsult.infra.entrypoint.dto.response.ConsultationResponseDto;
import com.fiap.easyconsult.infra.entrypoint.mapper.ConsultationMapper;
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
    private ConsultationMapper mapper;

    private GraphqlController graphqlController;

    private Consult validConsult;
    private ConsultationResponseDto validResponseDto;
    private ConsultationRequestDto validRequestDto;
    private ConsultationFilterRequestDto validFilterDto;
    private ConsultationUpdateRequestDto validUpdateDto;

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
                .build();

        validResponseDto = mock(ConsultationResponseDto.class);
        validRequestDto = mock(ConsultationRequestDto.class);
        validFilterDto = mock(ConsultationFilterRequestDto.class);
        validUpdateDto = mock(ConsultationUpdateRequestDto.class);
    }

    @Nested
    @DisplayName("Query Mapping Tests")
    class QueryMappingTests {

        @Test
        @DisplayName("Should return filtered consultations successfully")
        void shouldReturnFilteredConsultationsSuccessfully() {
            // Given
            List<Consult> consultList = List.of(validConsult);
            List<ConsultationResponseDto> expectedResponse = List.of(validResponseDto);
            
            when(mapper.toConsultationFilter(any(ConsultationFilterRequestDto.class)))
                    .thenReturn(mock(ConsultationFilter.class));
            when(consultQueryUseCase.findWithFilters(any(ConsultationFilter.class)))
                    .thenReturn(consultList);
            when(mapper.toConsultationResponse(any(List.class)))
                    .thenReturn(expectedResponse);

            // When
            List<ConsultationResponseDto> result = graphqlController.getFilteredConsultations(validFilterDto);

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(expectedResponse, result);
            
            verify(mapper, times(1)).toConsultationFilter(validFilterDto);
            verify(consultQueryUseCase, times(1)).findWithFilters(any(ConsultationFilter.class));
            verify(mapper, times(1)).toConsultationResponse(consultList);
        }

        @Test
        @DisplayName("Should return all consultations successfully")
        void shouldReturnAllConsultationsSuccessfully() {
            // Given
            List<Consult> consultList = List.of(validConsult);
            
            when(consultQueryUseCase.findAll()).thenReturn(consultList);
            when(mapper.toConsultationResponse(any(Consult.class)))
                    .thenReturn(validResponseDto);

            // When
            List<ConsultationResponseDto> result = graphqlController.getAllConsultations();

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(validResponseDto, result.get(0));
            
            verify(consultQueryUseCase, times(1)).findAll();
            verify(mapper, times(1)).toConsultationResponse(validConsult);
        }

        @Test
        @DisplayName("Should propagate exception when query use case fails")
        void shouldPropagateExceptionWhenQueryUseCaseFails() {
            // Given
            RuntimeException useCaseException = new RuntimeException("Query failed");
            when(mapper.toConsultationFilter(any(ConsultationFilterRequestDto.class)))
                    .thenReturn(mock(ConsultationFilter.class));
            when(consultQueryUseCase.findWithFilters(any(ConsultationFilter.class)))
                    .thenThrow(useCaseException);

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    graphqlController.getFilteredConsultations(validFilterDto)
            );

            assertEquals("Query failed", exception.getMessage());
            verify(consultQueryUseCase, times(1)).findWithFilters(any(ConsultationFilter.class));
        }
    }

    @Nested
    @DisplayName("Mutation Mapping Tests")
    class MutationMappingTests {

        @Test
        @DisplayName("Should create consultation successfully")
        void shouldCreateConsultationSuccessfully() {
            // Given
            when(mapper.toConsultation(any(ConsultationRequestDto.class)))
                    .thenReturn(validConsult);
            when(consultCommandUseCase.createConsultation(any(Consult.class)))
                    .thenReturn(validConsult);
            when(mapper.toConsultationResponse(any(Consult.class)))
                    .thenReturn(validResponseDto);

            // When
            ConsultationResponseDto result = graphqlController.createFullConsultation(validRequestDto);

            // Then
            assertNotNull(result);
            assertEquals(validResponseDto, result);
            
            verify(mapper, times(1)).toConsultation(validRequestDto);
            verify(consultCommandUseCase, times(1)).createConsultation(validConsult);
            verify(mapper, times(1)).toConsultationResponse(validConsult);
        }

        @Test
        @DisplayName("Should update consultation successfully")
        void shouldUpdateConsultationSuccessfully() {
            // Given
            UpdateConsult updateConsult = mock(UpdateConsult.class);
            
            when(mapper.toUpdateConsult(any(ConsultationUpdateRequestDto.class)))
                    .thenReturn(updateConsult);
            when(consultCommandUseCase.updateConsultation(any(UpdateConsult.class)))
                    .thenReturn(validConsult);
            when(mapper.toConsultationResponse(any(Consult.class)))
                    .thenReturn(validResponseDto);

            // When
            ConsultationResponseDto result = graphqlController.updateConsultation(validUpdateDto);

            // Then
            assertNotNull(result);
            assertEquals(validResponseDto, result);
            
            verify(mapper, times(1)).toUpdateConsult(validUpdateDto);
            verify(consultCommandUseCase, times(1)).updateConsultation(updateConsult);
            verify(mapper, times(1)).toConsultationResponse(validConsult);
        }

        @Test
        @DisplayName("Should delete consultation successfully")
        void shouldDeleteConsultationSuccessfully() {
            // Given
            Long consultId = 1L;
            doNothing().when(consultCommandUseCase).deleteConsultation(any(Long.class));

            // When
            Boolean result = graphqlController.deleteConsultation(consultId);

            // Then
            assertNotNull(result);
            assertTrue(result);
            
            verify(consultCommandUseCase, times(1)).deleteConsultation(consultId);
        }

        @Test
        @DisplayName("Should propagate exception when create consultation fails")
        void shouldPropagateExceptionWhenCreateConsultationFails() {
            // Given
            RuntimeException useCaseException = new RuntimeException("Creation failed");
            when(mapper.toConsultation(any(ConsultationRequestDto.class)))
                    .thenReturn(validConsult);
            when(consultCommandUseCase.createConsultation(any(Consult.class)))
                    .thenThrow(useCaseException);

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    graphqlController.createFullConsultation(validRequestDto)
            );

            assertEquals("Creation failed", exception.getMessage());
            verify(consultCommandUseCase, times(1)).createConsultation(validConsult);
        }

        @Test
        @DisplayName("Should propagate exception when update consultation fails")
        void shouldPropagateExceptionWhenUpdateConsultationFails() {
            // Given
            UpdateConsult updateConsult = mock(UpdateConsult.class);
            RuntimeException useCaseException = new RuntimeException("Update failed");
            
            when(mapper.toUpdateConsult(any(ConsultationUpdateRequestDto.class)))
                    .thenReturn(updateConsult);
            when(consultCommandUseCase.updateConsultation(any(UpdateConsult.class)))
                    .thenThrow(useCaseException);

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    graphqlController.updateConsultation(validUpdateDto)
            );

            assertEquals("Update failed", exception.getMessage());
            verify(consultCommandUseCase, times(1)).updateConsultation(updateConsult);
        }

        @Test
        @DisplayName("Should propagate exception when delete consultation fails")
        void shouldPropagateExceptionWhenDeleteConsultationFails() {
            // Given
            Long consultId = 1L;
            RuntimeException useCaseException = new RuntimeException("Delete failed");
            doThrow(useCaseException).when(consultCommandUseCase).deleteConsultation(any(Long.class));

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    graphqlController.deleteConsultation(consultId)
            );

            assertEquals("Delete failed", exception.getMessage());
            verify(consultCommandUseCase, times(1)).deleteConsultation(consultId);
        }
    }

    @Nested
    @DisplayName("Mapper Integration Tests")
    class MapperIntegrationTests {

        @Test
        @DisplayName("Should call mapper methods with correct parameters")
        void shouldCallMapperMethodsWithCorrectParameters() {
            // Given
            when(mapper.toConsultation(any(ConsultationRequestDto.class)))
                    .thenReturn(validConsult);
            when(consultCommandUseCase.createConsultation(any(Consult.class)))
                    .thenReturn(validConsult);
            when(mapper.toConsultationResponse(any(Consult.class)))
                    .thenReturn(validResponseDto);

            // When
            graphqlController.createFullConsultation(validRequestDto);

            // Then
            verify(mapper, times(1)).toConsultation(eq(validRequestDto));
            verify(mapper, times(1)).toConsultationResponse(eq(validConsult));
        }

        @Test
        @DisplayName("Should handle mapper exception gracefully")
        void shouldHandleMapperExceptionGracefully() {
            // Given
            RuntimeException mapperException = new RuntimeException("Mapping error");
            when(mapper.toConsultation(any(ConsultationRequestDto.class)))
                    .thenThrow(mapperException);

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    graphqlController.createFullConsultation(validRequestDto)
            );

            assertEquals("Mapping error", exception.getMessage());
            verify(mapper, times(1)).toConsultation(validRequestDto);
            verifyNoInteractions(consultCommandUseCase);
        }
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create controller with all required dependencies")
        void shouldCreateControllerWithAllRequiredDependencies() {
            // Given & When
            GraphqlController controller = new GraphqlController(
                    consultCommandUseCase, consultQueryUseCase, mapper
            );

            // Then
            assertNotNull(controller);
        }
    }
}