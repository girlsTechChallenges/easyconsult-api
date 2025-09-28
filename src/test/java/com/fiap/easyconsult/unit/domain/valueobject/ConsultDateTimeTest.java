package com.fiap.easyconsult.unit.domain.valueobject;

import com.fiap.easyconsult.core.domain.valueobject.ConsultDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ConsultDateTime Value Object Tests")
class ConsultDateTimeTest {

    @Nested
    @DisplayName("ConsultDateTime Creation Tests")
    class ConsultDateTimeCreationTests {

        @Test
        @DisplayName("Should create ConsultDateTime with valid date and time")
        void shouldCreateConsultDateTimeWithValidDateAndTime() {
            // Given
            LocalDate date = LocalDate.of(2025, 10, 15);
            LocalTime time = LocalTime.of(14, 30);

            // When
            ConsultDateTime consultDateTime = ConsultDateTime.of(date, time);

            // Then
            assertNotNull(consultDateTime);
            assertEquals(date, consultDateTime.getDate());
            assertEquals(time, consultDateTime.getTime());
            assertEquals(LocalDateTime.of(date, time), consultDateTime.toLocalDateTime());
        }

        @Test
        @DisplayName("Should throw exception when date is null")
        void shouldThrowExceptionWhenDateIsNull() {
            // Given
            LocalTime time = LocalTime.of(14, 30);

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    ConsultDateTime.of(null, time)
            );

            assertEquals("Date and time cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when time is null")
        void shouldThrowExceptionWhenTimeIsNull() {
            // Given
            LocalDate date = LocalDate.of(2025, 10, 15);

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    ConsultDateTime.of(date, null)
            );

            assertEquals("Date and time cannot be null", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("DateTime Validation Tests")
    class DateTimeValidationTests {

        @Test
        @DisplayName("Should validate future date/time successfully")
        void shouldValidateFutureDateTimeSuccessfully() {
            // Given
            LocalDate futureDate = LocalDate.now().plusDays(7);
            LocalTime time = LocalTime.of(14, 30);
            ConsultDateTime consultDateTime = ConsultDateTime.of(futureDate, time);

            // When & Then
            assertDoesNotThrow(consultDateTime::validateFutureDateTime);
        }

        @Test
        @DisplayName("Should throw exception when validating past date/time")
        void shouldThrowExceptionWhenValidatingPastDateTime() {
            // Given
            LocalDate pastDate = LocalDate.now().minusDays(1);
            LocalTime time = LocalTime.of(14, 30);
            ConsultDateTime consultDateTime = ConsultDateTime.of(pastDate, time);

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    consultDateTime::validateFutureDateTime);

            assertEquals("Consult date/time cannot be in the past", exception.getMessage());
        }

        @Test
        @DisplayName("Should correctly identify past date/time")
        void shouldCorrectlyIdentifyPastDateTime() {
            // Given
            LocalDate pastDate = LocalDate.now().minusDays(1);
            LocalTime time = LocalTime.of(14, 30);
            ConsultDateTime consultDateTime = ConsultDateTime.of(pastDate, time);

            // When & Then
            assertTrue(consultDateTime.isPast());
        }

        @Test
        @DisplayName("Should correctly identify future date/time")
        void shouldCorrectlyIdentifyFutureDateTime() {
            // Given
            LocalDate futureDate = LocalDate.now().plusDays(7);
            LocalTime time = LocalTime.of(14, 30);
            ConsultDateTime consultDateTime = ConsultDateTime.of(futureDate, time);

            // When & Then
            assertFalse(consultDateTime.isPast());
        }
    }

    @Nested
    @DisplayName("Equality and HashCode Tests")
    class EqualityAndHashCodeTests {

        @Test
        @DisplayName("Should be equal when same date and time")
        void shouldBeEqualWhenSameDateAndTime() {
            // Given
            LocalDate date = LocalDate.of(2025, 10, 15);
            LocalTime time = LocalTime.of(14, 30);
            
            ConsultDateTime dateTime1 = ConsultDateTime.of(date, time);
            ConsultDateTime dateTime2 = ConsultDateTime.of(date, time);

            // When & Then
            assertEquals(dateTime1, dateTime2);
            assertEquals(dateTime1.hashCode(), dateTime2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when different date")
        void shouldNotBeEqualWhenDifferentDate() {
            // Given
            LocalDate date1 = LocalDate.of(2025, 10, 15);
            LocalDate date2 = LocalDate.of(2025, 10, 16);
            LocalTime time = LocalTime.of(14, 30);
            
            ConsultDateTime dateTime1 = ConsultDateTime.of(date1, time);
            ConsultDateTime dateTime2 = ConsultDateTime.of(date2, time);

            // When & Then
            assertNotEquals(dateTime1, dateTime2);
        }

        @Test
        @DisplayName("Should not be equal when different time")
        void shouldNotBeEqualWhenDifferentTime() {
            // Given
            LocalDate date = LocalDate.of(2025, 10, 15);
            LocalTime time1 = LocalTime.of(14, 30);
            LocalTime time2 = LocalTime.of(15, 30);
            
            ConsultDateTime dateTime1 = ConsultDateTime.of(date, time1);
            ConsultDateTime dateTime2 = ConsultDateTime.of(date, time2);

            // When & Then
            assertNotEquals(dateTime1, dateTime2);
        }
    }

    @Nested
    @DisplayName("String Representation Tests")
    class StringRepresentationTests {

        @Test
        @DisplayName("Should return correct string representation")
        void shouldReturnCorrectStringRepresentation() {
            // Given
            LocalDate date = LocalDate.of(2025, 10, 15);
            LocalTime time = LocalTime.of(14, 30);
            ConsultDateTime consultDateTime = ConsultDateTime.of(date, time);

            // When
            String stringRepresentation = consultDateTime.toString();

            // Then
            assertEquals("2025-10-15T14:30", stringRepresentation);
        }
    }
}