package com.fiap.easyconsult.integration.graphql;

import com.fiap.easyconsult.EasyconsultMain;
import com.fiap.easyconsult.integration.config.TestConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes End-to-End (E2E) para GraphQL do EasyConsult.
 * 
 * Estes testes validam fluxos completos da aplicação simulando interações
 * reais de clientes externos através de requisições HTTP GraphQL com autenticação JWT.
 * 
 * CENÁRIOS E2E COBERTOS:
 * 1. Enfermeiro agenda uma consulta
 * 2. Médico busca consultas  
 * 3. Médico atualiza status da consulta
 * 4. Médico deleta consulta
 * 5. Validação de segurança por roles
 * 6. Tratamento de erros de autorização
 */
@SpringBootTest(classes = EasyconsultMain.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {EasyconsultMain.class, TestConfig.class})
@ActiveProfiles("test")
@Transactional
@DisplayName("GraphQL End-to-End Integration Tests")
public class GraphQLEndToEndTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String futureDate;

    @BeforeEach
    void setUp() {
        futureDate = LocalDate.now().plusDays(7).format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    private HttpHeaders createHeaders(String role) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(gerarTokenValido(role));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @SuppressWarnings("rawtypes")
    private ResponseEntity<Map> executeGraphQL(String query, HttpHeaders headers) {
        String graphqlRequest = """
            {
              "query": "%s"
            }
            """.formatted(query.replace("\"", "\\\"").replace("\n", "\\n"));

        HttpEntity<String> request = new HttpEntity<>(graphqlRequest, headers);
        return restTemplate.postForEntity("/graphql", request, Map.class);
    }

    @Nested
    @DisplayName("Fluxo E2E Completo de Consulta")
    class CompleteConsultFlowE2ETests {

        @Test
        @DisplayName("Deve executar fluxo E2E: Enfermeiro cria → Médico lista → Médico atualiza → Médico deleta")
        void shouldExecuteCompleteEndToEndFlow() {
            
            // ==============================================================
            // 1. ENFERMEIRO CRIA CONSULTA
            // ==============================================================
            String createMutation = """
                mutation {
                  createFullConsult(input: {
                    patient: {
                      name: "Paciente E2E"
                      email: "paciente.e2e@email.com"
                    }
                    professional: {
                      name: "Dr. E2E"
                      email: "dr.e2e@hospital.com"
                    }
                    localTime: "14:30:00"
                    date: "%s"
                    reason: "Consulta E2E - Fluxo completo"
                  }) {
                    id
                    patient {
                      name
                      email
                    }
                    nameProfessional
                    statusConsult
                    reason
                  }
                }
                """.formatted(futureDate);

            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> createResponse = executeGraphQL(createMutation, createHeaders("enfermeiro"));
            
            // Validar resposta da criação
            assertThat(createResponse.getStatusCode().is2xxSuccessful()).isTrue();
            assertThat(createResponse.getBody()).isNotNull();
            
            @SuppressWarnings("unchecked")
            Map<String, Object> createBody = (Map<String, Object>) createResponse.getBody();
            assertThat(createBody).containsKey("data");
            assertThat(createBody).doesNotContainKey("errors");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> createData = (Map<String, Object>) createBody.get("data");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> createdConsult = (Map<String, Object>) createData.get("createFullConsult");
            
            String consultId = (String) createdConsult.get("id");
            assertThat(consultId).isNotNull();
            assertThat(createdConsult.get("reason")).isEqualTo("Consulta E2E - Fluxo completo");
            assertThat(createdConsult.get("statusConsult")).isEqualTo("SCHEDULED");

            // ==============================================================
            // 2. MÉDICO LISTA CONSULTAS (deve incluir a criada)
            // ==============================================================
            String listQuery = """
                query {
                  getAllConsults {
                    id
                    patient {
                      name
                      email
                    }
                    nameProfessional
                    statusConsult
                    reason
                  }
                }
                """;

            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> listResponse = executeGraphQL(listQuery, createHeaders("medico"));
            
            assertThat(listResponse.getStatusCode().is2xxSuccessful()).isTrue();
            
            @SuppressWarnings("unchecked")
            Map<String, Object> listData = (Map<String, Object>) listResponse.getBody().get("data");
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> consults = (List<Map<String, Object>>) listData.get("getAllConsults");

            assertThat(consults).isNotEmpty();            // Verificar se a consulta criada está na lista
            boolean consultFound = consults.stream()
                .anyMatch(c -> consultId.equals(c.get("id")));
            assertThat(consultFound).isTrue();

            // ==============================================================
            // 3. MÉDICO ATUALIZA STATUS DA CONSULTA
            // ==============================================================
            String updateMutation = """
                mutation {
                  updateConsult(input: {
                    id: "%s"
                    reason: "Consulta E2E - ATUALIZADA"
                    status: CARRIED_OUT
                  }) {
                    id
                    reason
                    statusConsult
                  }
                }
                """.formatted(consultId);

            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> updateResponse = executeGraphQL(updateMutation, createHeaders("medico"));
            
            assertThat(updateResponse.getStatusCode().is2xxSuccessful()).isTrue();
            
            @SuppressWarnings("unchecked")
            Map<String, Object> updateData = (Map<String, Object>) updateResponse.getBody().get("data");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> updatedConsult = (Map<String, Object>) updateData.get("updateConsult");
            
            assertThat(updatedConsult.get("reason")).isEqualTo("Consulta E2E - ATUALIZADA");
            assertThat(updatedConsult.get("statusConsult")).isEqualTo("CARRIED_OUT");

            // ==============================================================
            // 4. MÉDICO DELETA A CONSULTA
            // ==============================================================
            String deleteMutation = """
                mutation {
                  deleteConsult(id: "%s")
                }
                """.formatted(consultId);

            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> deleteResponse = executeGraphQL(deleteMutation, createHeaders("medico"));
            
            assertThat(deleteResponse.getStatusCode().is2xxSuccessful()).isTrue();
            
            @SuppressWarnings("unchecked")
            Map<String, Object> deleteData = (Map<String, Object>) deleteResponse.getBody().get("data");
            
            assertThat(deleteData.get("deleteConsult")).isEqualTo(true);
            
            // FLUXO E2E COMPLETO VALIDADO COM SUCESSO ✅
        }

        @Test
        @DisplayName("Deve filtrar consultas por email do paciente em fluxo E2E")
        void shouldFilterConsultsByPatientEmailE2E() {
            
            // 1. Enfermeiro cria consulta para paciente específico
            String createMutation = """
                mutation {
                  createFullConsult(input: {
                    patient: {
                      name: "Maria Filtro E2E"
                      email: "maria.filtro.e2e@email.com"
                    }
                    professional: {
                      name: "Dr. Filtro E2E"
                      email: "dr.filtro.e2e@hospital.com"
                    }
                    localTime: "10:00:00"
                    date: "%s"
                    reason: "Consulta para teste de filtro E2E"
                  }) {
                    id
                    patient { email }
                  }
                }
                """.formatted(futureDate);

            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> createResponse = executeGraphQL(createMutation, createHeaders("enfermeiro"));
            assertThat(createResponse.getStatusCode().is2xxSuccessful()).isTrue();

            // 2. Médico filtra consultas por email do paciente
            String filterQuery = """
                query {
                  getFilteredConsults(filter: {
                    patientEmail: "maria.filtro.e2e@email.com"
                  }) {
                    id
                    patient {
                      name
                      email
                    }
                    reason
                  }
                }
                """;

            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> filterResponse = executeGraphQL(filterQuery, createHeaders("medico"));
            
            assertThat(filterResponse.getStatusCode().is2xxSuccessful()).isTrue();
            
            assertThat(filterResponse.getBody()).isNotNull();
            @SuppressWarnings("unchecked")
            Map<String, Object> filterData = (Map<String, Object>) filterResponse.getBody().get("data");
            assertThat(filterData).isNotNull();
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> filteredConsults = (List<Map<String, Object>>) filterData.get("getFilteredConsults");

            assertThat(filteredConsults).isNotNull().isNotEmpty();            // Verificar se todas as consultas são do paciente correto
            filteredConsults.forEach(consult -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> patient = (Map<String, Object>) consult.get("patient");
                assertThat(patient.get("email")).isEqualTo("maria.filtro.e2e@email.com");
            });
        }
    }

    @Nested
    @DisplayName("Segurança e Autorização E2E")
    class SecurityEndToEndTests {

        @Test
        @DisplayName("Deve validar autorização por roles - Enfermeiro pode criar")
        void shouldValidateRoleBasedAuthorization_NurseCanCreate() {
            String createMutation = """
                mutation {
                  createFullConsult(input: {
                    patient: {
                      name: "Teste Autorização"
                      email: "teste.auth@email.com"
                    }
                    professional: {
                      name: "Dr. Auth"
                      email: "dr.auth@hospital.com"
                    }
                    localTime: "09:00:00"
                    date: "%s"
                    reason: "Consulta criada por enfermeiro"
                  }) {
                    id
                    reason
                  }
                }
                """.formatted(futureDate);

            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = executeGraphQL(createMutation, createHeaders("enfermeiro"));
            
            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            
            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            
            // Enfermeiro deve conseguir criar sem erros
            assertThat(responseBody).containsKey("data");
            assertThat(responseBody).doesNotContainKey("errors");
        }

        @Test
        @DisplayName("Deve validar autorização por roles - Médico pode listar")
        void shouldValidateRoleBasedAuthorization_DoctorCanList() {
            String listQuery = """
                query {
                  getAllConsults {
                    id
                    reason
                  }
                }
                """;

            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = executeGraphQL(listQuery, createHeaders("medico"));
            
            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            
            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            
            // Médico deve conseguir listar sem erros
            assertThat(responseBody).containsKey("data");
            // Note: Pode não ter erros mesmo com lista vazia em alguns casos
        }

        @Test
        @DisplayName("Deve rejeitar acesso sem token JWT")
        void shouldRejectAccessWithoutJwtToken() {
            String query = """
                query {
                  getAllConsults {
                    id
                  }
                }
                """;

            // Tentar acessar sem token
            HttpHeaders headersWithoutAuth = new HttpHeaders();
            headersWithoutAuth.setContentType(MediaType.APPLICATION_JSON);
            
            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = executeGraphQL(query, headersWithoutAuth);
            
            // Deve retornar erro de autorização (401 ou 403)
            assertThat(response.getStatusCode().value()).isIn(401, 403);
        }
    }

    @Nested
    @DisplayName("Validação de Schema GraphQL E2E")
    class SchemaValidationE2ETests {

        @Test
        @DisplayName("Deve validar introspection do schema GraphQL")
        void shouldValidateGraphQLSchemaIntrospection() {
            String introspectionQuery = """
                query {
                  __schema {
                    queryType { name }
                    mutationType { name }
                    types {
                      name
                      kind
                    }
                  }
                }
                """;

            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = executeGraphQL(introspectionQuery, createHeaders("medico"));
            
            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            
            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            
            assertThat(responseBody).containsKey("data");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> schema = (Map<String, Object>) data.get("__schema");
            
            assertThat(schema).isNotNull();
            
            @SuppressWarnings("unchecked")
            Map<String, Object> queryType = (Map<String, Object>) schema.get("queryType");
            assertThat(queryType.get("name")).isEqualTo("Query");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> mutationType = (Map<String, Object>) schema.get("mutationType");
            assertThat(mutationType.get("name")).isEqualTo("Mutation");
        }

        @Test
        @DisplayName("Deve tratar query GraphQL inválida graciosamente")
        void shouldHandleInvalidGraphQLQueryGracefully() {
            String invalidQuery = """
                query {
                  nonExistentField {
                    someField
                  }
                }
                """;

            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = executeGraphQL(invalidQuery, createHeaders("medico"));
            
            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            
            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            
            // Deve ter erros GraphQL para query inválida
            assertThat(responseBody).containsKey("errors");
        }
    }

    /**
     * Gera token JWT válido usando a mesma chave secreta da aplicação.
     * 
     * Este método replica o mesmo processo de geração de token da aplicação,
     * garantindo compatibilidade total com o sistema de autenticação.
     */
    private String gerarTokenValido(String role) {
        // Usar a mesma chave secreta configurada no application-test.properties
        String secret = "test_secret_key_for_integration_tests_at_least_32_bytes_long";
        Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        String email = switch (role) {
            case "medico" -> "medico@test.com";
            case "enfermeiro" -> "enfermeiro@test.com";
            case "paciente" -> "paciente@test.com";
            default -> "user@test.com";
        };

        return Jwts.builder()
                .setSubject(email)
                .claim("scope", role)  // Importante: usar o role sem prefixo SCOPE_
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24 horas
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
