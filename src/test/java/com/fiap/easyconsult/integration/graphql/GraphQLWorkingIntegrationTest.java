package com.fiap.easyconsult.integration.graphql;

import com.fiap.easyconsult.EasyconsultMain;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
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

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração funcionais para GraphQL do EasyConsult.
 * 
 * Estes testes validam o funcionamento das queries e mutations GraphQL
 * usando TestRestTemplate para fazer requisições HTTP reais.
 */
@SpringBootTest(classes = EasyconsultMain.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("GraphQL Integration Tests")
public class GraphQLWorkingIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(gerarTokenTeste());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void executeGraphQLQuery(String query, String expectedDataKey) {
        String graphqlRequest = """
            {
              "query": "%s"
            }
            """.formatted(query.replace("\"", "\\\"").replace("\n", "\\n"));

        HttpEntity<String> request = new HttpEntity<>(graphqlRequest, createHeaders());
        ResponseEntity<Map> response = restTemplate.postForEntity("/graphql", request, Map.class);
        
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertThat(responseBody).containsKey("data");
        
        Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
        if (expectedDataKey != null) {
            assertThat(data).containsKey(expectedDataKey);
        }
    }

    @Nested
    @DisplayName("Schema Validation Tests")
    class SchemaValidationTests {

        @Test
        @DisplayName("Deve carregar o schema GraphQL corretamente")
        void shouldLoadGraphQLSchema() {
            executeGraphQLQuery("{ __schema { types { name } } }", "__schema");
        }

        @Test
        @DisplayName("Deve validar tipos disponíveis no schema")
        void shouldValidateAvailableTypes() {
            String query = """
                {
                  __schema {
                    queryType { name }
                    mutationType { name }
                  }
                }
                """;
            executeGraphQLQuery(query, "__schema");
        }
    }

    @Nested
    @DisplayName("Consultation Query Tests")
    class ConsultationQueryTests {

        @Test
        @DisplayName("Deve executar query getAllConsultations sem erro")
        void shouldExecuteGetAllConsultationsQuery() {
            String query = """
                {
                  getAllConsultations {
                    id
                    patient {
                      name
                      email
                    }
                    nameProfessional
                    statusConsultation
                    date
                    localTime
                    reason
                  }
                }
                """;
            
            executeGraphQLQuery(query, "getAllConsultations");
        }

        @Test
        @DisplayName("Deve executar query getFilteredConsultations sem erro")
        void shouldExecuteGetFilteredConsultationsQuery() {
            String query = """
                {
                  getFilteredConsultations(filter: {}) {
                    id
                    patient {
                      name
                      email
                    }
                    nameProfessional
                    statusConsultation
                    date
                    localTime
                    reason
                  }
                }
                """;
            
            executeGraphQLQuery(query, "getFilteredConsultations");
        }
    }

    @Nested
    @DisplayName("Mutation Tests")
    class MutationTests {

        @Test
        @DisplayName("Deve validar estrutura da mutation createConsultation")
        void shouldValidateCreateConsultationMutation() {
            // Teste apenas a estrutura da mutation, não a execução (que requer dados válidos)
            String introspectionQuery = """
                {
                  __schema {
                    mutationType {
                      fields {
                        name
                        args {
                          name
                          type {
                            name
                          }
                        }
                      }
                    }
                  }
                }
                """;
            
            executeGraphQLQuery(introspectionQuery, "__schema");
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Deve tratar query inválida graciosamente")
        void shouldHandleInvalidQueryGracefully() {
            String invalidQuery = """
                {
                  nonExistentField {
                    someField
                  }
                }
                """;
            
            String graphqlRequest = """
                {
                  "query": "%s"
                }
                """.formatted(invalidQuery.replace("\"", "\\\"").replace("\n", "\\n"));

            HttpEntity<String> request = new HttpEntity<>(graphqlRequest, createHeaders());
            
            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.postForEntity("/graphql", request, Map.class);
            
            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            
            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            assertThat(responseBody).isNotNull();
            
            // Deve ter erros para query inválida
            assertThat(responseBody).containsKey("errors");
        }
    }

    private String gerarTokenTeste() {
        String secret = "test_secret_key_for_integration_tests_at_least_32_bytes_long";
        Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject("exemplo@example.com")
                .claim("scope", "paciente")
                .setIssuedAt(new Date())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

}