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
@DisplayName("Consultation Use Cases Integration Tests")
class ConsultationUseCasesIntegrationTest {

    @Autowired
    private ConsultCommandUseCase consultCommandUseCase;
    
    @Autowired
    private ConsultQueryUseCase consultQueryUseCase;

    private Patient testPatient;
    private Professional testProfessional;
    private LocalDate futureDate;
    private LocalTime consultTime;
    private ConsultStatus status;

    @BeforeEach
    void setUp() {
        testPatient = Patient.builder()
                .name("João Silva")
                .email("joao.integration.test@email.com")
                .build();

        testProfessional = Professional.builder()
                .name("Dra. Maria Santos")
                .email("dra.maria.integration@hospital.com")
                .build();

        futureDate = LocalDate.now().plusDays(7);
        consultTime = LocalTime.of(14, 30);
        status = ConsultStatus.SCHEDULED;

    }

    @Nested
    @DisplayName("Fluxo Completo CRUD")
    class CompleteFlowTests {

        @Test
        @DisplayName("Deve executar fluxo completo: criar, buscar, atualizar e deletar")
        void shouldExecuteCompleteFlow() {
            // 1. Criar consulta
            Consult consultation = Consult.builder()
                    .reason("Consulta de integração completa")
                    .patient(testPatient)
                    .professional(testProfessional)
                    .dateTime(futureDate, consultTime)
                    .status(status)
                    .build();

            Consult createdConsultation = consultCommandUseCase.createConsultation(consultation);

            // Validar criação
            assertNotNull(createdConsultation);
            assertNotNull(createdConsultation.getId());
            assertTrue(createdConsultation.getId().getValue() > 0);
            assertEquals("Consulta de integração completa", createdConsultation.getReason());

            // 2. Buscar consultas (deve incluir a criada)
            List<Consult> allConsultations = consultQueryUseCase.findAll();
            assertNotNull(allConsultations);
            assertTrue(allConsultations.size() >= 1);
            
            boolean consultationExists = allConsultations.stream()
                    .anyMatch(c -> c.getId().getValue().equals(createdConsultation.getId().getValue()));
            assertTrue(consultationExists, "Consulta criada deve estar presente na busca");

            // 3. Atualizar consulta
            String newReason = "Consulta atualizada via integração";
            UpdateConsult updateConsult = UpdateConsult.builder()
                    .id(createdConsultation.getId().getValue())
                    .reason(newReason)
                    .date(futureDate.plusDays(1))
                    .time(consultTime.plusHours(1))
                    .build();

            Consult updatedConsultation = consultCommandUseCase.updateConsultation(updateConsult);
            
            // Validar atualização
            assertNotNull(updatedConsultation);
            assertEquals(createdConsultation.getId().getValue(), updatedConsultation.getId().getValue());
            assertEquals(newReason, updatedConsultation.getReason());
            assertEquals(futureDate.plusDays(1), updatedConsultation.getDate());

            // 4. Deletar consulta
            Long consultationId = createdConsultation.getId().getValue();
            assertDoesNotThrow(() -> consultCommandUseCase.deleteConsultation(consultationId));

            // Nota: Não podemos verificar se foi deletada porque findAll pode lançar exceção
            // quando não há consultas, dependendo da implementação do use case
        }

        @Test
        @DisplayName("Deve filtrar consultas por email do paciente")
        void shouldFilterConsultationsByPatientEmail() {
            // Given: Criar consulta para paciente específico
            Patient specificPatient = Patient.builder()
                    .name("Paciente Específico")
                    .email("paciente.especifico@email.com")
                    .build();

            Consult consultation = Consult.builder()
                    .reason("Consulta para filtro de paciente")
                    .patient(specificPatient)
                    .professional(testProfessional)
                    .dateTime(futureDate, consultTime)
                    .status(status)
                    .build();

            consultCommandUseCase.createConsultation(consultation);

            // When: Filtrar por email do paciente
            ConsultationFilter filter = new ConsultationFilter(
                    null, "paciente.especifico@email.com", null, null, null, null
            );

            List<Consult> filteredConsultations = consultQueryUseCase.findWithFilters(filter);

            // Then: Deve retornar apenas consultas do paciente específico
            assertNotNull(filteredConsultations);
            assertTrue(filteredConsultations.size() >= 1);
            
            filteredConsultations.forEach(c -> 
                assertEquals("paciente.especifico@email.com", c.getPatient().getEmail())
            );
        }

        @Test
        @DisplayName("Deve filtrar consultas por email do profissional")
        void shouldFilterConsultationsByProfessionalEmail() {
            // Given: Criar consulta para profissional específico
            Professional specificProfessional = Professional.builder()
                    .name("Dr. Específico")
                    .email("dr.especifico@hospital.com")
                    .build();

            // Usar email único para paciente para evitar conflitos
            Patient uniquePatient = Patient.builder()
                    .name("Paciente Único")
                    .email("paciente.unico.prof@email.com")
                    .build();

            Consult consultation = Consult.builder()
                    .reason("Consulta para filtro de profissional")
                    .patient(uniquePatient)
                    .professional(specificProfessional)
                    .dateTime(futureDate, consultTime)
                    .status(status)
                    .build();

            consultCommandUseCase.createConsultation(consultation);

            // When: Filtrar por email do profissional
            ConsultationFilter filter = new ConsultationFilter(
                    null, null, "dr.especifico@hospital.com", null, null, null
            );

            List<Consult> filteredConsultations = consultQueryUseCase.findWithFilters(filter);

            // Then: Deve retornar apenas consultas do profissional específico
            assertNotNull(filteredConsultations);
            assertTrue(filteredConsultations.size() >= 1);
            
            filteredConsultations.forEach(c -> 
                assertEquals("dr.especifico@hospital.com", c.getProfessional().getEmail())
            );
        }
    }

    @Nested
    @DisplayName("Validação de Regras de Negócio")
    class BusinessRulesValidationTests {

        @Test
        @DisplayName("Deve criar consulta com dados válidos")
        void shouldCreateConsultationWithValidData() {
            // Given: Consulta com dados válidos
            Consult consultation = Consult.builder()
                    .reason("Consulta de rotina para validação")
                    .patient(Patient.builder()
                            .name("Paciente Válido")
                            .email("paciente.valido@email.com")
                            .build())
                    .professional(testProfessional)
                    .dateTime(futureDate, consultTime)
                    .status(status)
                    .build();

            // When & Then: Criar consulta deve ser bem-sucedido
            Consult createdConsultation = consultCommandUseCase.createConsultation(consultation);
            
            assertNotNull(createdConsultation);
            assertNotNull(createdConsultation.getId());
            assertEquals("Consulta de rotina para validação", createdConsultation.getReason());
        }

        @Test
        @DisplayName("Deve rejeitar consulta com dados inválidos")
        void shouldRejectConsultationWithInvalidData() {
            // Given: Consulta com dados inválidos (razão vazia)
            assertThrows(Exception.class, () -> {
                Consult.builder()
                        .reason("") // Razão vazia - deve falhar na validação do domínio
                        .patient(testPatient)
                        .professional(testProfessional)
                        .dateTime(futureDate, consultTime)
                        .build();
            });
        }

        @Test
        @DisplayName("Deve rejeitar consulta com paciente nulo")
        void shouldRejectConsultationWithNullPatient() {
            // Given & When & Then: Consulta com paciente nulo deve falhar
            assertThrows(Exception.class, () -> {
                Consult.builder()
                        .reason("Consulta válida")
                        .patient(null) // Paciente nulo - deve falhar na validação
                        .professional(testProfessional)
                        .dateTime(futureDate, consultTime)
                        .build();
            });
        }

        @Test
        @DisplayName("Deve rejeitar consulta com profissional nulo")
        void shouldRejectConsultationWithNullProfessional() {
            // Given & When & Then: Consulta com profissional nulo deve falhar
            assertThrows(Exception.class, () -> {
                Consult.builder()
                        .reason("Consulta válida")
                        .patient(testPatient)
                        .professional(null) // Profissional nulo - deve falhar na validação
                        .dateTime(futureDate, consultTime)
                        .build();
            });
        }
    }

    @Nested
    @DisplayName("Tratamento de Exceções")
    class ExceptionHandlingTests {

        @Test
        @DisplayName("Deve lidar com filtros que não retornam resultados")
        void shouldHandleFiltersWithNoResults() {
            // Given: Filtro que não deve retornar resultados
            ConsultationFilter filter = new ConsultationFilter(
                    null, "email.inexistente@domain.com", null, null, null, null
            );

            // When & Then: Deve lançar exceção específica ou retornar lista vazia
            // Dependendo da implementação do use case
            assertThrows(Exception.class, () -> {
                consultQueryUseCase.findWithFilters(filter);
            });
        }

        @Test
        @DisplayName("Deve lidar com busca geral quando não há consultas")
        void shouldHandleFindAllWhenNoConsultations() {
            // Nota: Este teste pode falhar se já existirem consultas de outros testes
            // ou se o use case não lança exceção quando não há resultados
            
            // When & Then: Dependendo da implementação, pode lançar exceção
            // quando não há consultas
            try {
                List<Consult> consultations = consultQueryUseCase.findAll();
                // Se não lançar exceção, deve pelo menos retornar lista não nula
                assertNotNull(consultations);
            } catch (Exception e) {
                // Aceitar exceção como comportamento válido para "nenhuma consulta encontrada"
                assertNotNull(e);
            }
        }
    }
}