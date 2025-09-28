package com.fiap.easyconsult.unit.domain.model;

import com.fiap.easyconsult.core.domain.model.Professional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Professional Domain Model Tests")
class ProfessionalTest {

    @Nested
    @DisplayName("Professional Creation Tests")
    class ProfessionalCreationTests {

        @Test
        @DisplayName("Should create professional with valid data")
        void shouldCreateProfessionalWithValidData() {
            // Given & When
            Professional professional = Professional.builder()
                    .id(1L)
                    .name("Dr. Maria Santos")
                    .email("maria@example.com")
                    .build();

            // Then
            assertNotNull(professional);
            assertEquals(1L, professional.getId());
            assertEquals("Dr. Maria Santos", professional.getName());
            assertEquals("maria@example.com", professional.getEmail());
        }

        @Test
        @DisplayName("Should trim name when creating professional")
        void shouldTrimNameWhenCreatingProfessional() {
            // Given & When
            Professional professional = Professional.builder()
                    .id(1L)
                    .name("  Dr. Maria Santos  ")
                    .email("maria@example.com")
                    .build();

            // Then
            assertEquals("Dr. Maria Santos", professional.getName());
        }

        @Test
        @DisplayName("Should trim email when creating professional")
        void shouldTrimEmailWhenCreatingProfessional() {
            // Given & When
            Professional professional = Professional.builder()
                    .id(1L)
                    .name("Dr. Maria Santos")
                    .email("  maria@example.com  ")
                    .build();

            // Then
            assertEquals("maria@example.com", professional.getEmail());
        }

        @Test
        @DisplayName("Should throw exception when name is null")
        void shouldThrowExceptionWhenNameIsNull() {
            // Given & When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    Professional.builder()
                            .id(1L)
                            .name(null)
                            .email("maria@example.com")
                            .build()
            );

            assertEquals("Professional name cannot be empty", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when name is empty")
        void shouldThrowExceptionWhenNameIsEmpty() {
            // Given & When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    Professional.builder()
                            .id(1L)
                            .name("   ")
                            .email("maria@example.com")
                            .build()
            );

            assertEquals("Professional name cannot be empty", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when email is null")
        void shouldThrowExceptionWhenEmailIsNull() {
            // Given & When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    Professional.builder()
                            .id(1L)
                            .name("Dr. Maria Santos")
                            .email(null)
                            .build()
            );

            assertEquals("Professional email cannot be empty", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when email is empty")
        void shouldThrowExceptionWhenEmailIsEmpty() {
            // Given & When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    Professional.builder()
                            .id(1L)
                            .name("Dr. Maria Santos")
                            .email("   ")
                            .build()
            );

            assertEquals("Professional email cannot be empty", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Professional Update Tests")
    class ProfessionalUpdateTests {

        @Test
        @DisplayName("Should update professional name")
        void shouldUpdateProfessionalName() {
            // Given
            Professional professional = Professional.builder()
                    .id(1L)
                    .name("Dr. Maria Santos")
                    .email("maria@example.com")
                    .build();

            // When
            professional.updateName("Dra. Maria Silva");

            // Then
            assertEquals("Dra. Maria Silva", professional.getName());
        }

        @Test
        @DisplayName("Should update professional email")
        void shouldUpdateProfessionalEmail() {
            // Given
            Professional professional = Professional.builder()
                    .id(1L)
                    .name("Dr. Maria Santos")
                    .email("maria@example.com")
                    .build();

            // When
            professional.updateEmail("maria.santos@hospital.com");

            // Then
            assertEquals("maria.santos@hospital.com", professional.getEmail());
        }

        @Test
        @DisplayName("Should trim name when updating")
        void shouldTrimNameWhenUpdating() {
            // Given
            Professional professional = Professional.builder()
                    .id(1L)
                    .name("Dr. Maria Santos")
                    .email("maria@example.com")
                    .build();

            // When
            professional.updateName("  Dra. Maria Silva  ");

            // Then
            assertEquals("Dra. Maria Silva", professional.getName());
        }

        @Test
        @DisplayName("Should throw exception when updating name to null")
        void shouldThrowExceptionWhenUpdatingNameToNull() {
            // Given
            Professional professional = Professional.builder()
                    .id(1L)
                    .name("Dr. Maria Santos")
                    .email("maria@example.com")
                    .build();

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    professional.updateName(null)
            );

            assertEquals("Professional name cannot be empty", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when updating email to null")
        void shouldThrowExceptionWhenUpdatingEmailToNull() {
            // Given
            Professional professional = Professional.builder()
                    .id(1L)
                    .name("Dr. Maria Santos")
                    .email("maria@example.com")
                    .build();

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    professional.updateEmail(null)
            );

            assertEquals("Professional email cannot be empty", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Professional Equality Tests")
    class ProfessionalEqualityTests {

        @Test
        @DisplayName("Should be equal when same ID")
        void shouldBeEqualWhenSameId() {
            // Given
            Professional professional1 = Professional.builder()
                    .id(1L)
                    .name("Dr. Maria Santos")
                    .email("maria@example.com")
                    .build();

            Professional professional2 = Professional.builder()
                    .id(1L)
                    .name("Dr. João Silva")
                    .email("joao@example.com")
                    .build();

            // When & Then
            assertEquals(professional1, professional2);
            assertEquals(professional1.hashCode(), professional2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when different ID")
        void shouldNotBeEqualWhenDifferentId() {
            // Given
            Professional professional1 = Professional.builder()
                    .id(1L)
                    .name("Dr. Maria Santos")
                    .email("maria@example.com")
                    .build();

            Professional professional2 = Professional.builder()
                    .id(2L)
                    .name("Dr. Maria Santos")
                    .email("maria@example.com")
                    .build();

            // When & Then
            assertNotEquals(professional1, professional2);
        }

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {
            // Given
            Professional professional = Professional.builder()
                    .id(1L)
                    .name("Dr. Maria Santos")
                    .email("maria@example.com")
                    .build();

            // When & Then
            assertEquals(professional, professional);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            // Given
            Professional professional = Professional.builder()
                    .id(1L)
                    .name("Dr. Maria Santos")
                    .email("maria@example.com")
                    .build();

            // When & Then
            assertNotEquals(professional, null);
        }
    }
}