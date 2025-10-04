package com.fiap.easyconsult.unit.domain.valueobject;

import com.fiap.easyconsult.core.domain.valueobject.ConsultStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ConsultStatus Value Object Tests")
class ConsultStatusTest {

    @Nested
    @DisplayName("Status Validation Tests")
    class StatusValidationTests {

        @Test
        @DisplayName("SCHEDULED status can be cancelled")
        void scheduledStatusCanBeCancelled() {
            // Given
            ConsultStatus status = ConsultStatus.SCHEDULED;

            // When & Then
            assertTrue(status.canBeCancelled());
        }

        @Test
        @DisplayName("CANCELLED status cannot be cancelled")
        void cancelledStatusCannotBeCancelled() {
            // Given
            ConsultStatus status = ConsultStatus.CANCELLED;

            // When & Then
            assertFalse(status.canBeCancelled());
        }

        @Test
        @DisplayName("CARRIED_OUT status cannot be cancelled")
        void carriedOutStatusCannotBeCancelled() {
            // Given
            ConsultStatus status = ConsultStatus.CARRIED_OUT;

            // When & Then
            assertFalse(status.canBeCancelled());
        }
    }

    @Nested
    @DisplayName("Finalized Status Tests")
    class FinalizedStatusTests {

        @Test
        @DisplayName("SCHEDULED status is not finalized")
        void scheduledStatusIsNotFinalized() {
            // Given
            ConsultStatus status = ConsultStatus.SCHEDULED;

            // When & Then
            assertFalse(status.isFinalized());
        }

        @Test
        @DisplayName("CANCELLED status is finalized")
        void cancelledStatusIsFinalized() {
            // Given
            ConsultStatus status = ConsultStatus.CANCELLED;

            // When & Then
            assertTrue(status.isFinalized());
        }

        @Test
        @DisplayName("CARRIED_OUT status is finalized")
        void carriedOutStatusIsFinalized() {
            // Given
            ConsultStatus status = ConsultStatus.CARRIED_OUT;

            // When & Then
            assertTrue(status.isFinalized());
        }
    }
}