package com.fiap.easyconsult.unit.usecase;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.core.domain.model.Patient;
import com.fiap.easyconsult.core.domain.model.Professional;
import com.fiap.easyconsult.core.domain.model.UpdateConsult;
import com.fiap.easyconsult.core.domain.valueobject.ConsultStatus;
import com.fiap.easyconsult.core.outputport.DeleteGateway;
import com.fiap.easyconsult.core.outputport.SaveGateway;
import com.fiap.easyconsult.core.outputport.UpdateGateway;
import com.fiap.easyconsult.core.usecase.ConsultCommandUseCases;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConsultCommandUseCases Tests")
class ConsultCommandUseCasesTest {

    @Mock
    private SaveGateway saveGateway;

    @Mock
    private UpdateGateway updateGateway;

    @Mock
    private DeleteGateway deleteGateway;

    private ConsultCommandUseCases consultCommandUseCases;

    private Consult validConsult;

    @BeforeEach
    void setUp() {
        consultCommandUseCases = new ConsultCommandUseCases(saveGateway, updateGateway, deleteGateway);
        
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
    }

    @Nested
    @DisplayName("Create Consult Tests")
    class createConsultTests {

        @Test
        @DisplayName("Should create consult successfully")
        void shouldcreateConsultSuccessfully() {
            // Given
            when(saveGateway.save(any(Consult.class))).thenReturn(validConsult);

            // When
            Consult result = consultCommandUseCases.createConsult(validConsult);

            // Then
            assertNotNull(result);
            assertEquals(validConsult.getId(), result.getId());
            assertEquals(validConsult.getReason(), result.getReason());
            assertEquals(validConsult.getPatient(), result.getPatient());
            assertEquals(validConsult.getProfessional(), result.getProfessional());
            
            verify(saveGateway, times(1)).save(validConsult);
        }

        @Test
        @DisplayName("Should propagate exception when save gateway fails")
        void shouldPropagateExceptionWhenSaveGatewayFails() {
            // Given
            RuntimeException gatewayException = new RuntimeException("Database error");
            when(saveGateway.save(any(Consult.class))).thenThrow(gatewayException);

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    consultCommandUseCases.createConsult(validConsult)
            );

            assertEquals("Database error", exception.getMessage());
            verify(saveGateway, times(1)).save(validConsult);
        }

        @Test
        @DisplayName("Should call save gateway with correct consult")
        void shouldCallSaveGatewayWithCorrectConsult() {
            // Given
            when(saveGateway.save(any(Consult.class))).thenReturn(validConsult);

            // When
            consultCommandUseCases.createConsult(validConsult);

            // Then
            verify(saveGateway, times(1)).save(validConsult);
        }
    }

    @Nested
    @DisplayName("Update Consult Tests")
    class updateConsultTests {

        @Test
        @DisplayName("Should update consult successfully")
        void shouldupdateConsultSuccessfully() {
            // Given
            UpdateConsult updateConsult = mock(UpdateConsult.class);
            when(updateGateway.update(any(UpdateConsult.class))).thenReturn(validConsult);

            // When
            Consult result = consultCommandUseCases.updateConsult(updateConsult);

            // Then
            assertNotNull(result);
            assertEquals(validConsult.getId(), result.getId());
            assertEquals(validConsult.getReason(), result.getReason());
            
            verify(updateGateway, times(1)).update(updateConsult);
        }

        @Test
        @DisplayName("Should propagate exception when update gateway fails")
        void shouldPropagateExceptionWhenUpdateGatewayFails() {
            // Given
            UpdateConsult updateConsult = mock(UpdateConsult.class);
            RuntimeException gatewayException = new RuntimeException("Update failed");
            when(updateGateway.update(any(UpdateConsult.class))).thenThrow(gatewayException);

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    consultCommandUseCases.updateConsult(updateConsult)
            );

            assertEquals("Update failed", exception.getMessage());
            verify(updateGateway, times(1)).update(updateConsult);
        }

        @Test
        @DisplayName("Should call update gateway with correct parameters")
        void shouldCallUpdateGatewayWithCorrectParameters() {
            // Given
            UpdateConsult updateConsult = mock(UpdateConsult.class);
            when(updateGateway.update(any(UpdateConsult.class))).thenReturn(validConsult);

            // When
            consultCommandUseCases.updateConsult(updateConsult);

            // Then
            verify(updateGateway, times(1)).update(updateConsult);
        }
    }

    @Nested
    @DisplayName("Delete Consult Tests")
    class deleteConsultTests {

        @Test
        @DisplayName("Should delete consult successfully")
        void shoulddeleteConsultSuccessfully() {
            // Given
            Long consultId = 1L;
            doNothing().when(deleteGateway).delete(any(Long.class));

            // When & Then
            assertDoesNotThrow(() -> consultCommandUseCases.deleteConsult(consultId));
            
            verify(deleteGateway, times(1)).delete(consultId);
        }

        @Test
        @DisplayName("Should propagate exception when delete gateway fails")
        void shouldPropagateExceptionWhenDeleteGatewayFails() {
            // Given
            Long consultId = 1L;
            RuntimeException gatewayException = new RuntimeException("Delete failed");
            doThrow(gatewayException).when(deleteGateway).delete(any(Long.class));

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    consultCommandUseCases.deleteConsult(consultId)
            );

            assertEquals("Delete failed", exception.getMessage());
            verify(deleteGateway, times(1)).delete(consultId);
        }

        @Test
        @DisplayName("Should call delete gateway with correct ID")
        void shouldCallDeleteGatewayWithCorrectId() {
            // Given
            Long consultId = 42L;
            doNothing().when(deleteGateway).delete(any(Long.class));

            // When
            consultCommandUseCases.deleteConsult(consultId);

            // Then
            verify(deleteGateway, times(1)).delete(consultId);
        }
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with all required dependencies")
        void shouldCreateInstanceWithAllRequiredDependencies() {
            // Given & When
            ConsultCommandUseCases useCases = new ConsultCommandUseCases(
                    saveGateway, updateGateway, deleteGateway
            );

            // Then
            assertNotNull(useCases);
        }
    }
}
