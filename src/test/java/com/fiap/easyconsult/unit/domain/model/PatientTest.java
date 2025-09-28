package com.fiap.easyconsult.unit.domain.model;

import com.fiap.easyconsult.core.domain.model.Patient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Patient Domain Model Tests")
class PatientTest {

    @Nested
    @DisplayName("Patient Creation Tests")
    class PatientCreationTests {

        @Test
        @DisplayName("Should create patient with valid data")
        void shouldCreatePatientWithValidData() {
            // Given & When
            Patient patient = Patient.builder()
                    .id(1L)
                    .name("João Silva")
                    .email("joao@example.com")
                    .build();

            // Then
            assertNotNull(patient);
            assertEquals(1L, patient.getId());
            assertEquals("João Silva", patient.getName());
            assertEquals("joao@example.com", patient.getEmail());
        }

        @Test
        @DisplayName("Should trim name when creating patient")
        void shouldTrimNameWhenCreatingPatient() {
            // Given & When
            Patient patient = Patient.builder()
                    .id(1L)
                    .name("  João Silva  ")
                    .email("joao@example.com")
                    .build();

            // Then
            assertEquals("João Silva", patient.getName());
        }

        @Test
        @DisplayName("Should trim email when creating patient")
        void shouldTrimEmailWhenCreatingPatient() {
            // Given & When
            Patient patient = Patient.builder()
                    .id(1L)
                    .name("João Silva")
                    .email("  joao@example.com  ")
                    .build();

            // Then
            assertEquals("joao@example.com", patient.getEmail());
        }

        @Test
        @DisplayName("Should throw exception when name is null")
        void shouldThrowExceptionWhenNameIsNull() {
            // Given & When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    Patient.builder()
                            .id(1L)
                            .name(null)
                            .email("joao@example.com")
                            .build()
            );

            assertEquals("Patient name cannot be empty", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when name is empty")
        void shouldThrowExceptionWhenNameIsEmpty() {
            // Given & When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    Patient.builder()
                            .id(1L)
                            .name("   ")
                            .email("joao@example.com")
                            .build()
            );

            assertEquals("Patient name cannot be empty", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when email is null")
        void shouldThrowExceptionWhenEmailIsNull() {
            // Given & When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    Patient.builder()
                            .id(1L)
                            .name("João Silva")
                            .email(null)
                            .build()
            );

            assertEquals("Patient email cannot be empty", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when email is empty")
        void shouldThrowExceptionWhenEmailIsEmpty() {
            // Given & When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    Patient.builder()
                            .id(1L)
                            .name("João Silva")
                            .email("   ")
                            .build()
            );

            assertEquals("Patient email cannot be empty", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Patient Update Tests")
    class PatientUpdateTests {

        @Test
        @DisplayName("Should update patient name")
        void shouldUpdatePatientName() {
            // Given
            Patient patient = Patient.builder()
                    .id(1L)
                    .name("João Silva")
                    .email("joao@example.com")
                    .build();

            // When
            patient.updateName("João Santos");

            // Then
            assertEquals("João Santos", patient.getName());
        }

        @Test
        @DisplayName("Should update patient email")
        void shouldUpdatePatientEmail() {
            // Given
            Patient patient = Patient.builder()
                    .id(1L)
                    .name("João Silva")
                    .email("joao@example.com")
                    .build();

            // When
            patient.updateEmail("joao.silva@example.com");

            // Then
            assertEquals("joao.silva@example.com", patient.getEmail());
        }

        @Test
        @DisplayName("Should trim name when updating")
        void shouldTrimNameWhenUpdating() {
            // Given
            Patient patient = Patient.builder()
                    .id(1L)
                    .name("João Silva")
                    .email("joao@example.com")
                    .build();

            // When
            patient.updateName("  João Santos  ");

            // Then
            assertEquals("João Santos", patient.getName());
        }

        @Test
        @DisplayName("Should trim email when updating")
        void shouldTrimEmailWhenUpdating() {
            // Given
            Patient patient = Patient.builder()
                    .id(1L)
                    .name("João Silva")
                    .email("joao@example.com")
                    .build();

            // When
            patient.updateEmail("  joao.silva@example.com  ");

            // Then
            assertEquals("joao.silva@example.com", patient.getEmail());
        }

        @Test
        @DisplayName("Should throw exception when updating name to null")
        void shouldThrowExceptionWhenUpdatingNameToNull() {
            // Given
            Patient patient = Patient.builder()
                    .id(1L)
                    .name("João Silva")
                    .email("joao@example.com")
                    .build();

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    patient.updateName(null)
            );

            assertEquals("Patient name cannot be empty", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when updating email to null")
        void shouldThrowExceptionWhenUpdatingEmailToNull() {
            // Given
            Patient patient = Patient.builder()
                    .id(1L)
                    .name("João Silva")
                    .email("joao@example.com")
                    .build();

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    patient.updateEmail(null)
            );

            assertEquals("Patient email cannot be empty", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Patient Equality Tests")
    class PatientEqualityTests {

        @Test
        @DisplayName("Should be equal when same ID")
        void shouldBeEqualWhenSameId() {
            // Given
            Patient patient1 = Patient.builder()
                    .id(1L)
                    .name("João Silva")
                    .email("joao@example.com")
                    .build();

            Patient patient2 = Patient.builder()
                    .id(1L)
                    .name("Maria Santos")
                    .email("maria@example.com")
                    .build();

            // When & Then
            assertEquals(patient1, patient2);
            assertEquals(patient1.hashCode(), patient2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when different ID")
        void shouldNotBeEqualWhenDifferentId() {
            // Given
            Patient patient1 = Patient.builder()
                    .id(1L)
                    .name("João Silva")
                    .email("joao@example.com")
                    .build();

            Patient patient2 = Patient.builder()
                    .id(2L)
                    .name("João Silva")
                    .email("joao@example.com")
                    .build();

            // When & Then
            assertNotEquals(patient1, patient2);
        }

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {
            // Given
            Patient patient = Patient.builder()
                    .id(1L)
                    .name("João Silva")
                    .email("joao@example.com")
                    .build();

            // When & Then
            assertEquals(patient, patient);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            // Given
            Patient patient = Patient.builder()
                    .id(1L)
                    .name("João Silva")
                    .email("joao@example.com")
                    .build();

            // When & Then
            assertNotEquals(patient, null);
        }
    }
}