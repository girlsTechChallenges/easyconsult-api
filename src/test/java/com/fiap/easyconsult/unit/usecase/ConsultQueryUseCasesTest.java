package com.fiap.easyconsult.unit.usecase;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.core.domain.model.ConsultationFilter;
import com.fiap.easyconsult.core.domain.model.Patient;
import com.fiap.easyconsult.core.domain.model.Professional;
import com.fiap.easyconsult.core.outputport.FindByGateway;
import com.fiap.easyconsult.core.usecase.ConsultQueryUseCases;
import com.fiap.easyconsult.infra.exception.GatewayException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConsultQueryUseCases Tests")
class ConsultQueryUseCasesTest {

    @Mock
    private FindByGateway findByGateway;

    private ConsultQueryUseCases consultQueryUseCases;
    private List<Consult> validConsultList;
    private ConsultationFilter validFilter;

    @BeforeEach
    void setUp() {
        consultQueryUseCases = new ConsultQueryUseCases(findByGateway);
        
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

        Consult consult = Consult.builder()
                .id(1L)
                .reason("Consulta de rotina")
                .patient(patient)
                .professional(professional)
                .dateTime(LocalDate.now().plusDays(7), LocalTime.of(14, 30))
                .build();

        validConsultList = List.of(consult);
        validFilter = mock(ConsultationFilter.class);
    }

    @Nested
    @DisplayName("Find With Filters Tests")
    class FindWithFiltersTests {

        @Test
        @DisplayName("Should return consultations when filter matches results")
        void shouldReturnConsultationsWhenFilterMatchesResults() {
            // Given
            when(findByGateway.findWithFilters(any(ConsultationFilter.class)))
                    .thenReturn(validConsultList);

            // When
            List<Consult> result = consultQueryUseCases.findWithFilters(validFilter);

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(validConsultList.get(0).getId(), result.get(0).getId());
            assertEquals(validConsultList.get(0).getReason(), result.get(0).getReason());
            
            verify(findByGateway, times(1)).findWithFilters(validFilter);
        }

        @Test
        @DisplayName("Should throw exception when no consultations found with filter")
        void shouldThrowExceptionWhenNoConsultationsFoundWithFilter() {
            // Given
            when(findByGateway.findWithFilters(any(ConsultationFilter.class)))
                    .thenReturn(Collections.emptyList());

            // When & Then
            GatewayException exception = assertThrows(GatewayException.class, () ->
                    consultQueryUseCases.findWithFilters(validFilter)
            );

            assertEquals("No consultations found for the given filter.", exception.getMessage());
            assertEquals("CONSULT_NOT_FOUND", exception.getCode());
            verify(findByGateway, times(1)).findWithFilters(validFilter);
        }

        @Test
        @DisplayName("Should throw exception when gateway returns null")
        void shouldThrowExceptionWhenGatewayReturnsNull() {
            // Given
            when(findByGateway.findWithFilters(any(ConsultationFilter.class)))
                    .thenReturn(null);

            // When & Then
            GatewayException exception = assertThrows(GatewayException.class, () ->
                    consultQueryUseCases.findWithFilters(validFilter)
            );

            assertEquals("No consultations found for the given filter.", exception.getMessage());
            assertEquals("CONSULT_NOT_FOUND", exception.getCode());
            verify(findByGateway, times(1)).findWithFilters(validFilter);
        }

        @Test
        @DisplayName("Should propagate exception when gateway fails")
        void shouldPropagateExceptionWhenGatewayFails() {
            // Given
            RuntimeException gatewayException = new RuntimeException("Database error");
            when(findByGateway.findWithFilters(any(ConsultationFilter.class)))
                    .thenThrow(gatewayException);

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    consultQueryUseCases.findWithFilters(validFilter)
            );

            assertEquals("Database error", exception.getMessage());
            verify(findByGateway, times(1)).findWithFilters(validFilter);
        }

        @Test
        @DisplayName("Should call gateway with correct filter")
        void shouldCallGatewayWithCorrectFilter() {
            // Given
            when(findByGateway.findWithFilters(any(ConsultationFilter.class)))
                    .thenReturn(validConsultList);

            // When
            consultQueryUseCases.findWithFilters(validFilter);

            // Then
            verify(findByGateway, times(1)).findWithFilters(eq(validFilter));
        }
    }

    @Nested
    @DisplayName("Find All Tests")
    class FindAllTests {

        @Test
        @DisplayName("Should return all consultations when found")
        void shouldReturnAllConsultationsWhenFound() {
            // Given
            when(findByGateway.findAll()).thenReturn(validConsultList);

            // When
            List<Consult> result = consultQueryUseCases.findAll();

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(validConsultList.get(0).getId(), result.get(0).getId());
            assertEquals(validConsultList.get(0).getReason(), result.get(0).getReason());
            
            verify(findByGateway, times(1)).findAll();
        }

        @Test
        @DisplayName("Should throw exception when no consultations found")
        void shouldThrowExceptionWhenNoConsultationsFound() {
            // Given
            when(findByGateway.findAll()).thenReturn(Collections.emptyList());

            // When & Then
            GatewayException exception = assertThrows(GatewayException.class, () ->
                    consultQueryUseCases.findAll()
            );

            assertEquals("No consultations found.", exception.getMessage());
            assertEquals("CONSULT_NOT_FOUND", exception.getCode());
            verify(findByGateway, times(1)).findAll();
        }

        @Test
        @DisplayName("Should throw exception when gateway returns null")
        void shouldThrowExceptionWhenGatewayReturnsNullInFindAll() {
            // Given
            when(findByGateway.findAll()).thenReturn(null);

            // When & Then
            GatewayException exception = assertThrows(GatewayException.class, () ->
                    consultQueryUseCases.findAll()
            );

            assertEquals("No consultations found.", exception.getMessage());
            assertEquals("CONSULT_NOT_FOUND", exception.getCode());
            verify(findByGateway, times(1)).findAll();
        }

        @Test
        @DisplayName("Should propagate exception when findAll gateway fails")
        void shouldPropagateExceptionWhenFindAllGatewayFails() {
            // Given
            RuntimeException gatewayException = new RuntimeException("Connection error");
            when(findByGateway.findAll()).thenThrow(gatewayException);

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    consultQueryUseCases.findAll()
            );

            assertEquals("Connection error", exception.getMessage());
            verify(findByGateway, times(1)).findAll();
        }

        @Test
        @DisplayName("Should call findAll exactly once")
        void shouldCallFindAllExactlyOnce() {
            // Given
            when(findByGateway.findAll()).thenReturn(validConsultList);

            // When
            consultQueryUseCases.findAll();

            // Then
            verify(findByGateway, times(1)).findAll();
            verifyNoMoreInteractions(findByGateway);
        }
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with required dependency")
        void shouldCreateInstanceWithRequiredDependency() {
            // Given & When
            ConsultQueryUseCases useCases = new ConsultQueryUseCases(findByGateway);

            // Then
            assertNotNull(useCases);
        }
    }
}