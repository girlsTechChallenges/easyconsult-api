package com.fiap.easyconsult.integration.usecase;

import com.fiap.easyconsult.EasyconsultMain;
import com.fiap.easyconsult.core.domain.model.*;
import com.fiap.easyconsult.core.domain.valueobject.ConsultStatus;
import com.fiap.easyconsult.core.inputport.ConsultCommandUseCase;
import com.fiap.easyconsult.core.inputport.ConsultQueryUseCase;
import com.fiap.easyconsult.integration.config.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração para use cases de consultas.
 * 
 * Estes testes validam a integração completa desde use cases até persistência,
 * garantindo que todas as operações funcionam corretamente em conjunto.
 * 
 * Importante: Estes testes podem falhar se houver restrições de negócio
 * específicas implementadas nos gateways (ex: não permitir múltiplas 
 * consultas para o mesmo email).
 */
@SpringBootTest
@ContextConfiguration(classes = {EasyconsultMain.class, TestConfig.class})
@ActiveProfiles("test")
@Transactional
@DisplayName("Consult Use Cases Integration Tests")
class ConsultUseCasesIntegrationTest {

    @Autowired
    private ConsultCommandUseCase consultCommandUseCase;
    
    @Autowired
    private ConsultQueryUseCase consultQueryUseCase;

    private Patient testPatient;
    private Professional testProfessional;
    private LocalDate futureDate;
    private LocalTime consultTime;

    @BeforeEach
    void setUp() {
        // Create base test data
        testPatient = Patient.builder()
                .name("Test Patient Integration")
                .email("patient.integration@test.com")
                .build();

        testProfessional = Professional.builder()
                .name("Dr. Test Professional Integration")
                .email("professional.integration@test.com")
                .build();

        futureDate = LocalDate.now().plusDays(7);
        consultTime = LocalTime.of(14, 30);
    }

    @Nested
    @DisplayName("Create Consult Integration Tests")
    class CreateConsultIntegrationTests {

        @Test
        @DisplayName("Should create consult successfully")
        void shouldCreateConsultSuccessfully() {
            // Given
            Consult consult = Consult.builder()
                    .reason("Integration test consult")
                    .patient(testPatient)
                    .professional(testProfessional)
                    .dateTime(futureDate, consultTime)
                    .status(ConsultStatus.SCHEDULED)
                    .build();

            // When
            Consult createdConsult = consultCommandUseCase.createConsult(consult);

            // Then
            assertNotNull(createdConsult);
            assertNotNull(createdConsult.getId());
            assertEquals("Integration test consult", createdConsult.getReason());
            assertEquals(testPatient.getEmail(), createdConsult.getPatient().getEmail());
            assertEquals(testProfessional.getEmail(), createdConsult.getProfessional().getEmail());
            assertEquals(futureDate, createdConsult.getDate());
            assertEquals(consultTime, createdConsult.getTime());
            assertEquals(ConsultStatus.SCHEDULED, createdConsult.getStatus());
        }

        @Test
        @DisplayName("Should create and update consult successfully")
        void shouldCreateAndUpdateConsultSuccessfully() {
            // Given - Create a consult first
            Consult consult = Consult.builder()
                    .reason("Original reason")
                    .patient(testPatient)
                    .professional(testProfessional)
                    .dateTime(futureDate, consultTime)
                    .status(ConsultStatus.SCHEDULED)
                    .build();

            Consult createdConsult = consultCommandUseCase.createConsult(consult);
            
            // Create update object
            UpdateConsult updateConsult = UpdateConsult.builder()
                    .id(createdConsult.getId().getValue())
                    .reason("Updated reason")
                    .date(futureDate.plusDays(1))
                    .time(LocalTime.of(15, 0))
                    .status(ConsultStatus.CARRIED_OUT)
                    .build();

            // When
            Consult updatedConsult = consultCommandUseCase.updateConsult(updateConsult);

            // Then
            assertNotNull(updatedConsult);
            assertEquals(createdConsult.getId(), updatedConsult.getId());
            assertEquals("Updated reason", updatedConsult.getReason());
            assertEquals(futureDate.plusDays(1), updatedConsult.getDate());
            assertEquals(LocalTime.of(15, 0), updatedConsult.getTime());
            assertEquals(ConsultStatus.CARRIED_OUT, updatedConsult.getStatus());
        }

        @Test
        @DisplayName("Should create and delete consult successfully")
        void shouldCreateAndDeleteConsultSuccessfully() {
            // Given
            Consult consult = Consult.builder()
                    .reason("To be deleted")
                    .patient(testPatient)
                    .professional(testProfessional)
                    .dateTime(futureDate, consultTime)
                    .status(ConsultStatus.SCHEDULED)
                    .build();

            Consult createdConsult = consultCommandUseCase.createConsult(consult);
            Long consultId = createdConsult.getId().getValue();

            // When & Then
            assertDoesNotThrow(() -> consultCommandUseCase.deleteConsult(consultId));
        }
    }

    @Nested
    @DisplayName("Query Consult Integration Tests")  
    class QueryConsultIntegrationTests {

        @Test
        @DisplayName("Should find consults with filters")
        void shouldFindConsultsWithFilters() {
            // Given
            Consult consult = Consult.builder()
                    .reason("Filterable consult")
                    .patient(testPatient)
                    .professional(testProfessional)
                    .dateTime(futureDate, consultTime)
                    .status(ConsultStatus.SCHEDULED)
                    .build();

            consultCommandUseCase.createConsult(consult);

            ConsultFilter filter = ConsultFilter.builder()
                    .patientEmail(testPatient.getEmail())
                    .professionalEmail(testProfessional.getEmail())
                    .status(ConsultStatus.SCHEDULED)
                    .build();

            // When
            List<Consult> filteredConsults = consultQueryUseCase.findWithFilters(filter);

            // Then
            assertNotNull(filteredConsults);
            assertFalse(filteredConsults.isEmpty());
            
            Consult foundConsult = filteredConsults.get(0);
            assertEquals("Filterable consult", foundConsult.getReason());
            assertEquals(testPatient.getEmail(), foundConsult.getPatient().getEmail());
            assertEquals(testProfessional.getEmail(), foundConsult.getProfessional().getEmail());
            assertEquals(ConsultStatus.SCHEDULED, foundConsult.getStatus());
        }

        @Test
        @DisplayName("Should find consults by patient email")
        void shouldFindConsultsByPatientEmail() {
            // Given
            Patient specificPatient = Patient.builder()
                    .name("Specific Patient")
                    .email("specific.patient@test.com")
                    .build();

            Consult consult = Consult.builder()
                    .reason("Patient specific consult")
                    .patient(specificPatient)
                    .professional(testProfessional)
                    .dateTime(futureDate, consultTime)
                    .status(ConsultStatus.SCHEDULED)
                    .build();

            consultCommandUseCase.createConsult(consult);

            ConsultFilter filter = ConsultFilter.builder()
                    .patientEmail(specificPatient.getEmail())
                    .build();

            // When
            List<Consult> filteredConsults = consultQueryUseCase.findWithFilters(filter);

            // Then
            assertNotNull(filteredConsults);
            assertFalse(filteredConsults.isEmpty());
            
            filteredConsults.forEach(c -> 
                assertEquals(specificPatient.getEmail(), c.getPatient().getEmail())
            );
        }

        @Test
        @DisplayName("Should find all consults")
        void shouldFindAllConsults() {
            // Given
            Consult consult = Consult.builder()
                    .reason("All consults test")
                    .patient(testPatient)
                    .professional(testProfessional)
                    .dateTime(futureDate, consultTime)
                    .status(ConsultStatus.SCHEDULED)
                    .build();

            Consult createdConsult = consultCommandUseCase.createConsult(consult);

            // When
            List<Consult> allConsults = consultQueryUseCase.findAll();

            // Then
            assertNotNull(allConsults);
            assertFalse(allConsults.isEmpty());
            
            boolean foundCreatedConsult = allConsults.stream()
                    .anyMatch(c -> c.getId().equals(createdConsult.getId()));
            assertTrue(foundCreatedConsult);
        }
    }

    @Nested
    @DisplayName("Business Rules Integration Tests")
    class BusinessRulesIntegrationTests {

        @Test
        @DisplayName("Should handle empty filter results")
        void shouldHandleEmptyFilterResults() {
            // Given
            ConsultFilter nonExistentFilter = ConsultFilter.builder()
                    .patientEmail("nonexistent@test.com")
                    .build();

            // When & Then
            assertThrows(Exception.class, () -> {
                consultQueryUseCase.findWithFilters(nonExistentFilter);
            });
        }

        @Test
        @DisplayName("Should handle empty consults list")
        void shouldHandleEmptyConsultsList() {
            // When & Then - Test with fresh database (no consults created)
            // This might pass or fail depending on test isolation and cleanup
            assertThrows(Exception.class, () -> {
                consultQueryUseCase.findAll();
            });
        }
    }

    @Nested
    @DisplayName("Complex Integration Scenarios")
    class ComplexIntegrationScenarios {

        @Test
        @DisplayName("Should handle multiple consults for same patient")
        void shouldHandleMultipleConsultsForSamePatient() {
            // Given
            Patient samePatient = Patient.builder()
                    .name("Multi Consult Patient")
                    .email("multi.consult.patient@test.com")
                    .build();

            Professional doctor1 = Professional.builder()
                    .name("Dr. First")
                    .email("doctor1@test.com")
                    .build();

            Professional doctor2 = Professional.builder()
                    .name("Dr. Second")
                    .email("doctor2@test.com")
                    .build();

            // Create first consult
            Consult firstConsult = Consult.builder()
                    .reason("First consult")
                    .patient(samePatient)
                    .professional(doctor1)
                    .dateTime(futureDate, LocalTime.of(9, 0))
                    .status(ConsultStatus.SCHEDULED)
                    .build();

            // Create second consult (different time and doctor)
            Consult secondConsult = Consult.builder()
                    .reason("Second consult")
                    .patient(samePatient)
                    .professional(doctor2)
                    .dateTime(futureDate, LocalTime.of(14, 0))
                    .status(ConsultStatus.SCHEDULED)
                    .build();

            // When
            Consult createdFirst = consultCommandUseCase.createConsult(firstConsult);
            Consult createdSecond = consultCommandUseCase.createConsult(secondConsult);

            // Then
            assertNotNull(createdFirst);
            assertNotNull(createdSecond);
            assertNotEquals(createdFirst.getId(), createdSecond.getId());

            // Verify we can find both by patient email
            ConsultFilter patientFilter = ConsultFilter.builder()
                    .patientEmail(samePatient.getEmail())
                    .build();

            List<Consult> patientConsults = consultQueryUseCase.findWithFilters(patientFilter);
            assertEquals(2, patientConsults.size());
        }
    }
}