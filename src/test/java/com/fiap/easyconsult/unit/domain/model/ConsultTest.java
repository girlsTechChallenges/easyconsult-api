package com.fiap.easyconsult.unit.domain.model;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.core.domain.model.Patient;
import com.fiap.easyconsult.core.domain.model.Professional;
import com.fiap.easyconsult.core.domain.valueobject.ConsultStatus;
import com.fiap.easyconsult.core.exception.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Consult Domain Model Tests")
class ConsultTest {

    private Patient patient;
    private Professional professional;
    private LocalDate futureDate;
    private LocalDate pastDate;
    private LocalTime consultTime;

    @BeforeEach
    void setUp() {
        patient = Patient.builder()
                .id(1L)
                .name("João Silva")
                .email("joao@example.com")
                .build();

        professional = Professional.builder()
                .id(1L)
                .name("Dr. Maria Santos")
                .email("maria@example.com")
                .build();

        futureDate = LocalDate.now().plusDays(7);
        pastDate = LocalDate.now().minusDays(1);
        consultTime = LocalTime.of(14, 30);
    }

    @Nested
    @DisplayName("Consult Creation Tests")
    class ConsultCreationTests {

        @Test
        @DisplayName("Should create consult with valid data")
        void shouldCreateConsultWithValidData() {
            // Given & When
            Consult consult = Consult.builder()
                    .id(1L)
                    .reason("Consulta de rotina")
                    .patient(patient)
                    .professional(professional)
                    .dateTime(futureDate, consultTime)
                    .build();

            // Then
            assertNotNull(consult);
            assertEquals(1L, consult.getId().getValue());
            assertEquals("Consulta de rotina", consult.getReason());
            assertEquals(ConsultStatus.SCHEDULED, consult.getStatus());
            assertEquals(patient, consult.getPatient());
            assertEquals(professional, consult.getProfessional());
            assertEquals(futureDate, consult.getDate());
            assertEquals(consultTime, consult.getTime());
        }

        @Test
        @DisplayName("Should throw exception when reason is null")
        void shouldThrowExceptionWhenReasonIsNull() {
            // Given & When & Then
            DomainException exception = assertThrows(DomainException.class, () ->
                    Consult.builder()
                            .id(1L)
                            .reason(null)
                            .patient(patient)
                            .professional(professional)
                            .dateTime(futureDate, consultTime)
                            .build()
            );

            assertEquals("Consult reason cannot be empty", exception.getMessage());
            assertEquals("CONSTRAINT_VIOLATION", exception.getCode());
        }

        @Test
        @DisplayName("Should throw exception when reason is empty")
        void shouldThrowExceptionWhenReasonIsEmpty() {
            // Given & When & Then
            DomainException exception = assertThrows(DomainException.class, () ->
                    Consult.builder()
                            .id(1L)
                            .reason("   ")
                            .patient(patient)
                            .professional(professional)
                            .dateTime(futureDate, consultTime)
                            .build()
            );

            assertEquals("Consult reason cannot be empty", exception.getMessage());
            assertEquals("CONSTRAINT_VIOLATION", exception.getCode());
        }

        @Test
        @DisplayName("Should throw exception when patient is null")
        void shouldThrowExceptionWhenPatientIsNull() {
            // Given & When & Then
            DomainException exception = assertThrows(DomainException.class, () ->
                    Consult.builder()
                            .id(1L)
                            .reason("Consulta de rotina")
                            .patient(null)
                            .professional(professional)
                            .dateTime(futureDate, consultTime)
                            .build()
            );

            assertEquals("Patient cannot be null", exception.getMessage());
            assertEquals("CONSTRAINT_VIOLATION", exception.getCode());
        }

        @Test
        @DisplayName("Should throw exception when professional is null")
        void shouldThrowExceptionWhenProfessionalIsNull() {
            // Given & When & Then
            DomainException exception = assertThrows(DomainException.class, () ->
                    Consult.builder()
                            .id(1L)
                            .reason("Consulta de rotina")
                            .patient(patient)
                            .professional(null)
                            .dateTime(futureDate, consultTime)
                            .build()
            );

            assertEquals("Professional cannot be null", exception.getMessage());
            assertEquals("CONSTRAINT_VIOLATION", exception.getCode());
        }

        @Test
        @DisplayName("Should throw exception when dateTime is null")
        void shouldThrowExceptionWhenDateTimeIsNull() {
            // Given & When & Then
            DomainException exception = assertThrows(DomainException.class, () ->
                    Consult.builder()
                            .id(1L)
                            .reason("Consulta de rotina")
                            .patient(patient)
                            .professional(professional)
                            .build()
            );

            assertEquals("DateTime cannot be null", exception.getMessage());
            assertEquals("CONSTRAINT_VIOLATION", exception.getCode());
        }
    }

    @Nested
    @DisplayName("Consult Status Transition Tests")
    class ConsultStatusTransitionTests {

        @Test
        @DisplayName("Should cancel scheduled consult in the future")
        void shouldCancelScheduledConsultInFuture() {
            // Given
            Consult consult = Consult.builder()
                    .id(1L)
                    .reason("Consulta de rotina")
                    .patient(patient)
                    .professional(professional)
                    .dateTime(futureDate, consultTime)
                    .build();

            // When
            consult.cancel();

            // Then
            assertEquals(ConsultStatus.CANCELLED, consult.getStatus());
        }

        @Test
        @DisplayName("Should throw exception when trying to cancel past consult")
        void shouldThrowExceptionWhenTryingToCancelPastConsult() {
            // Given
            Consult consult = Consult.builder()
                    .id(1L)
                    .reason("Consulta de rotina")
                    .patient(patient)
                    .professional(professional)
                    .dateTime(pastDate, consultTime)
                    .build();

            // When & Then
            DomainException exception = assertThrows(DomainException.class, consult::cancel);
            assertEquals("Cannot cancel a past consult", exception.getMessage());
            assertEquals("BUSINESS_RULE", exception.getCode());
        }

        @Test
        @DisplayName("Should complete scheduled consult in the past")
        void shouldCompleteScheduledConsultInPast() {
            // Given
            Consult consult = Consult.builder()
                    .id(1L)
                    .reason("Consulta de rotina")
                    .patient(patient)
                    .professional(professional)
                    .dateTime(pastDate, consultTime)
                    .build();

            // When
            consult.complete();

            // Then
            assertEquals(ConsultStatus.COMPLETED, consult.getStatus());
        }

        @Test
        @DisplayName("Should throw exception when trying to complete future consult")
        void shouldThrowExceptionWhenTryingToCompleteFutureConsult() {
            // Given
            Consult consult = Consult.builder()
                    .id(1L)
                    .reason("Consulta de rotina")
                    .patient(patient)
                    .professional(professional)
                    .dateTime(futureDate, consultTime)
                    .build();

            // When & Then
            DomainException exception = assertThrows(DomainException.class, consult::complete);
            assertEquals("Cannot complete a future consult", exception.getMessage());
            assertEquals("BUSINESS_RULE", exception.getCode());
        }

        @Test
        @DisplayName("Should mark scheduled consult as no-show in the past")
        void shouldMarkScheduledConsultAsNoShowInPast() {
            // Given
            Consult consult = Consult.builder()
                    .id(1L)
                    .reason("Consulta de rotina")
                    .patient(patient)
                    .professional(professional)
                    .dateTime(pastDate, consultTime)
                    .build();

            // When
            consult.markAsNoShow();

            // Then
            assertEquals(ConsultStatus.NO_SHOW, consult.getStatus());
        }

        @Test
        @DisplayName("Should throw exception when trying to mark future consult as no-show")
        void shouldThrowExceptionWhenTryingToMarkFutureConsultAsNoShow() {
            // Given
            Consult consult = Consult.builder()
                    .id(1L)
                    .reason("Consulta de rotina")
                    .patient(patient)
                    .professional(professional)
                    .dateTime(futureDate, consultTime)
                    .build();

            // When & Then
            DomainException exception = assertThrows(DomainException.class, consult::markAsNoShow);
            assertEquals("Cannot mark as no-show a future consult", exception.getMessage());
            assertEquals("BUSINESS_RULE", exception.getCode());
        }
    }

    @Nested
    @DisplayName("Consult Business Rules Tests")
    class ConsultBusinessRulesTests {

        @Test
        @DisplayName("Should not allow cancellation of completed consult")
        void shouldNotAllowCancellationOfCompletedConsult() {
            // Given
            Consult consult = Consult.builder()
                    .id(1L)
                    .reason("Consulta de rotina")
                    .patient(patient)
                    .professional(professional)
                    .dateTime(pastDate, consultTime)
                    .build();
            
            consult.complete();

            // When & Then
            DomainException exception = assertThrows(DomainException.class, consult::cancel);
            assertEquals("Cannot cancel a consult with status: COMPLETED", exception.getMessage());
            assertEquals("BUSINESS_RULE", exception.getCode());
        }

        @Test
        @DisplayName("Should not allow completion of cancelled consult")
        void shouldNotAllowCompletionOfCancelledConsult() {
            // Given
            Consult consult = Consult.builder()
                    .id(1L)
                    .reason("Consulta de rotina")
                    .patient(patient)
                    .professional(professional)
                    .dateTime(futureDate, consultTime)
                    .build();
            
            consult.cancel();

            // When & Then
            DomainException exception = assertThrows(DomainException.class, consult::complete);
            assertEquals("Only scheduled consults can be completed", exception.getMessage());
            assertEquals("BUSINESS_RULE", exception.getCode());
        }

        @Test
        @DisplayName("Should not allow marking cancelled consult as no-show")
        void shouldNotAllowMarkingCancelledConsultAsNoShow() {
            // Given
            Consult consult = Consult.builder()
                    .id(1L)
                    .reason("Consulta de rotina")
                    .patient(patient)
                    .professional(professional)
                    .dateTime(futureDate, consultTime)
                    .build();
            
            consult.cancel();

            // When & Then
            DomainException exception = assertThrows(DomainException.class, consult::markAsNoShow);
            assertEquals("Only scheduled consults can be marked as no-show", exception.getMessage());
            assertEquals("BUSINESS_RULE", exception.getCode());
        }
    }
}