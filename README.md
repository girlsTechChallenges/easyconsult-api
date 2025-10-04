# 🏥 EasyConsult API

**Serviço de agendamento de consultas médicas** desenvolvido em Java Spring Boot com arquitetura hexagonal, GraphQL e Apache Kafka.

## 📋 Sobre o Projeto

O **EasyConsult** é uma aplicação que permite gerenciar agendamentos de consultas entre pacientes e profissionais de saúde de forma simples e eficiente.

## 🚀 Kafka + Docker (Novo!)

A aplicação agora inclui **Apache Kafka** para mensageria assíncrona! 

### 📋 Quick Start

```bash
# Suba a infraestrutura (PostgreSQL, Redis, Kafka)
docker-compose --profile infra up -d

# Execute a aplicação localmente
./mvnw spring-boot:run

# Teste a API Kafka
curl -X POST "http://localhost:8081/api/kafka/publish-consult?message=Hello%20Kafka"
```

### 🔧 Serviços Incluídos

| Serviço | Porta | URL | Descrição |
|---------|-------|-----|-----------|
| **EasyConsult API** | 8081 | http://localhost:8081 | API principal |
| **Kafka UI** | 8080 | http://localhost:8080 | Interface web do Kafka |
| **Kafka Broker** | 9092 | localhost:9092 | Message broker |
| **PostgreSQL** | 5432 | localhost:5432 | Banco principal |
| **Redis** | 6379 | localhost:6379 | Cache |

### 🧪 Testando Kafka

```bash
# Via API REST
curl -X POST "http://localhost:8081/api/kafka/publish-consult?message=Hello%20Kafka"

# Via Interface Web do Kafka
# Acesse: http://localhost:8080
```

## 🏗️ Arquitetura

O projeto segue **arquitetura hexagonal (Clean Architecture)** com separação clara de responsabilidades:

- **Core Domain**: Modelos de domínio (`Patient`, `Professional`, `Consult`)
- **Input Ports**: Interfaces dos casos de uso (`ConsultCommandUseCase`, `ConsultQueryUseCase`)
- **Output Ports**: Interfaces de gateways (`SaveGateway`, `FindByGateway`, `DeleteGateway`, `UpdateGateway`)
- **Infrastructure**: Implementações concretas (persistência, controladores, adapters)

## ⚡ Stack Tecnológica

- **Java 21** - Linguagem principal
- **Spring Boot 3.5.5** - Framework web
- **GraphQL** - API principal para queries e mutations
- **Apache Kafka** - Sistema de mensageria assíncrona
- **PostgreSQL** - Banco de dados relacional
- **Redis** - Sistema de cache
- **Docker & Docker Compose** - Containerização
- **Lombok** - Redução de boilerplate code
- **Maven** - Gerenciamento de dependências

## 🔧 Funcionalidades

### ✅ Implementadas
- ✨ **Criar consultas** com dados do paciente, profissional, data/hora e motivo
- 🔍 **Buscar todas as consultas** com cache Redis
- 🎯 **Filtrar consultas** por:
  - ID da consulta
  - ID do paciente
  - ID do profissional
  - Status da consulta
- ✏️ **Atualizar consultas** existentes com invalidação de cache
- 🗑️ **Deletar consultas** com limpeza completa de cache
- 🔐 **Autenticação JWT** com roles de usuário
- 💾 **Sistema de cache Redis** com estratégias de invalidação
- 📨 **Integração Kafka** para mensageria assíncrona

## 📊 Status de Consulta

| Status | Descrição |
|--------|-----------|
| `SCHEDULED` | Consulta agendada |
| `CONFIRMED` | Consulta confirmada |
| `CANCELLED` | Consulta cancelada |

## 🔐 Segurança e Autenticação

### JWT Authentication
A API utiliza autenticação JWT com suporte a diferentes roles:

- **`SCOPE_medico`** - Profissionais médicos
- **`SCOPE_enfermeiro`** - Profissionais de enfermagem  
- **`SCOPE_paciente`** - Pacientes

### Configuração de Desenvolvimento
Durante o desenvolvimento, os endpoints GraphQL estão configurados como **público** para facilitar os testes:
- `/graphql` - Endpoint principal
- `/graphiql` - Interface gráfica (habilitada apenas em dev)

### Cache Strategy
Sistema de cache Redis implementado com estratégias de invalidação:
- **Individual**: Cache por ID de consulta
- **Geral**: Cache de todas as consultas
- **Filtros**: Cache por critérios de busca
- **Invalidação**: Automática em operações de UPDATE/DELETE

## 🚀 Como Executar

### Pré-requisitos
- Java 21+
- Docker & Docker Compose
- Maven 3.9+

### Desenvolvimento Local

1. **Clone o repositório:**
```bash
git clone https://github.com/girlsTechChallenges/easyconsult-api.git
cd easyconsult-api
```

2. **Suba a infraestrutura (PostgreSQL + Redis + Kafka):**
```bash
docker-compose --profile infra up -d
```

3. **Execute a aplicação:**
```bash
./mvnw spring-boot:run
```

> A aplicação irá iniciar automaticamente com o perfil `dev` ativo.

### Produção com Docker

```bash
docker-compose --profile prod up -d
```

## 🐳 Docker Setup Completo

### **1. Construir a aplicação**
```bash
# Compilar o projeto
./mvnw clean package -DskipTests

# Ou no Windows
mvnw.cmd clean package -DskipTests
```

### **2. Subir apenas a infraestrutura** (recomendado para desenvolvimento)
```bash
# Sobe PostgreSQL, Redis, Kafka e Zookeeper
docker-compose --profile infra up -d

# Executar a aplicação localmente
./mvnw spring-boot:run
```

### **3. Subir tudo com Docker** (ambiente completo)
```bash
# Sobe toda a stack incluindo a aplicação
docker-compose --profile prod up -d
```

### **4. Comandos úteis**
```bash
# Parar todos os containers
docker-compose down

# Parar e remover volumes (CUIDADO: apaga dados)
docker-compose down -v

# Ver status dos containers
docker-compose ps

# Ver logs da aplicação
docker-compose logs -f app

# Ver logs do Kafka
docker-compose logs -f kafka
```

## 📨 Apache Kafka Integration

### Classes Principais

1. **`KafkaProducerConfig`** - Configuração Spring para o Kafka Producer
2. **`KafkaMessageService`** - Serviço Spring para envio de mensagens de consultas
3. **`KafkaController`** - Controlador REST para testes via API
4. **`ConsultationKafkaMessage`** - DTO para mensagens de consulta no Kafka

### ⚙️ Configuração

O projeto está configurado para usar o tópico `easyconsult-consult` definido em `application.properties`:

```properties
# Kafka Topics Configuration
app.kafka.topics.consult=easyconsult-consult
app.kafka.groupid=group-consulation
```

### 🚀 Como Usar o Kafka

#### 1. Integração Automática (Produção)

O Kafka é **integrado automaticamente** no fluxo de criação de consultas. Sempre que uma nova consulta é salva, uma mensagem é enviada automaticamente para o tópico `easyconsult-consult`:

```java
// No SaveGatewayImpl - Executado automaticamente ao salvar consulta
kafkaMessageService.publishConsultationEvent(result);
```

A mensagem enviada contém todos os dados da consulta:
```json
{
  "id": "123",
  "nameProfessional": "Dr. Silva",
  "patient": {
    "name": "João Santos",
    "email": "joao@email.com"
  },
  "localTime": "14:30:00",
  "date": "2025-10-15",
  "reason": "Consulta de rotina",
  "statusConsulation": "SCHEDULED"
}
```

#### 2. Via Endpoints REST (Para Testes)

```bash
# Teste manual de mensagem
POST /api/kafka/publish-consult?message=Teste%20de%20mensagem

# Exemplo via cURL
curl -X POST "http://localhost:8081/api/kafka/publish-consult?message=Teste%20Kafka"
```

### 📊 Monitoramento do Kafka

#### **Kafka UI (Interface Web)**
Acesse: http://localhost:8080
- Visualizar tópicos
- Monitorar mensagens
- Gerenciar consumers
- Ver métricas em tempo real

#### **Comandos CLI**
```bash
# Listar tópicos
docker exec kafka kafka-topics --bootstrap-server localhost:9092 --list

# Criar tópico
docker exec kafka kafka-topics --bootstrap-server localhost:9092 --create --topic meu-topico --partitions 3 --replication-factor 1

# Consumir mensagens
docker exec kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic meu-topico --from-beginning

# Produzir mensagens
docker exec -it kafka kafka-console-producer --bootstrap-server localhost:9092 --topic meu-topico
```

### 🔧 Configurações do Kafka

**Detecção Automática de Ambiente**: O sistema detecta automaticamente se está rodando localmente ou em Docker:

```java
// KafkaProducerConfig.java
private String detectKafkaBootstrapServers() {
    String dockerEnv = System.getenv("DOCKER_ENV");
    String springProfile = System.getProperty("spring.profiles.active");
    
    if ("true".equals(dockerEnv) || "prod".equals(springProfile)) {
        return "kafka:29092"; // Docker
    }
    
    try {
        java.net.InetAddress.getByName("kafka");
        return "kafka:29092"; // Docker network
    } catch (java.net.UnknownHostException e) {
        return "localhost:9092"; // Local
    }
}
```

**Configurações do Producer** (definidas no código):
- **Confiabilidade**: `acks=all`, `retries=3`, `idempotence=true`
- **Performance**: `batch.size=16384`, `linger.ms=5`, `buffer.memory=33554432`
- **Serialização**: `StringSerializer` para chave e valor

### 🔍 Debug do Zookeeper

Comandos úteis para explorar o Zookeeper:

```bash
# Conectar no Zookeeper CLI
docker exec -it zookeeper zookeeper-shell localhost:2181

# Dentro do zookeeper-shell:
ls /
ls /brokers
ls /brokers/ids
ls /config/topics

# Ver informações de um broker
get /brokers/ids/1

# Ver metadados de tópicos
ls /brokers/topics
get /brokers/topics/meu-topico

# Verificar controller
get /controller
```

## 📡 GraphQL API

### 🌐 Endpoints Disponíveis
- **GraphQL Endpoint**: `http://localhost:8081/graphql`
- **GraphiQL Interface**: `http://localhost:8081/graphiql` (Interface gráfica para testes)

### 🔍 Queries Disponíveis

```graphql
# Buscar todas as consultas
query {
  getAllConsultations {
    id
    reason
    statusConsultation
    date
    localTime
    patient {
      name
      email
    }
    nameProfessional
  }
}

# Buscar consultas com filtros
query {
  getFilteredConsultations(filter: {
    patientEmail: "joao@email.com"
    professionalEmail: "dr.silva@email.com"
    status: SCHEDULED
    date: "2025-10-15"
  }) {
    id
    reason
    statusConsultation
    date
    localTime
    patient {
      name
      email
    }
    nameProfessional
  }
}
```

### ✏️ Mutations Disponíveis

```graphql
# Criar nova consulta
mutation {
  createFullConsultation(input: {
    reason: "Consulta de rotina"
    date: "2025-10-15"
    localTime: "14:30:00"
    patient: {
      name: "João Silva"
      email: "joao@email.com"
    }
    professional: {
      name: "Dr. Silva"
      email: "dr.silva@email.com"
    }
  }) {
    id
    reason
    statusConsultation
    date
    localTime
    patient {
      name
      email
    }
    nameProfessional
  }
}

# Atualizar consulta existente
mutation {
  updateConsultation(input: {
    id: "1"
    reason: "Consulta de retorno"
    date: "2025-10-20"
    localTime: "15:00:00"
    status: SCHEDULED
    professional: {
      name: "Dr. Silva"
      email: "dr.silva@email.com"
    }
  }) {
    id
    reason
    statusConsultation
    date
    localTime
  }
}

# Deletar consulta
mutation {
  deleteConsultation(id: "1")
}
```

## 💡 Exemplos Práticos

### Testando via GraphiQL
1. Acesse `http://localhost:8081/graphiql`
2. Use os exemplos abaixo para testar os endpoints

### Fluxo Completo de Teste

```graphql
# 1. Criar uma consulta
mutation CreateConsult {
  createFullConsultation(input: {
    reason: "Consulta de rotina"
    date: "2025-10-15"
    localTime: "14:30:00"
    patient: {
      name: "João Silva"
      email: "joao@email.com"
    }
    professional: {
      name: "Dr. Silva"
      email: "dr.silva@email.com"
    }
  }) {
    id
    reason
    statusConsultation
    date
    localTime
  }
}

# 2. Buscar todas as consultas
query GetAllConsults {
  getAllConsultations {
    id
    reason
    statusConsultation
    date
    localTime
    patient {
      name
      email
    }
    nameProfessional
  }
}

# 3. Atualizar uma consulta
mutation UpdateConsult {
  updateConsultation(input: {
    id: "1"
    reason: "Consulta de retorno - ATUALIZADA"
    date: "2025-10-20"
    localTime: "15:00:00"
    status: SCHEDULED
    professional: {
      name: "Dr. Silva"
      email: "dr.silva@email.com"
    }
  }) {
    id
    reason
    statusConsultation
  }
}

# 4. Buscar por filtros
query FilterConsults {
  getFilteredConsultations(filter: {
    patientEmail: "joao@email.com"
    status: SCHEDULED
  }) {
    id
    reason
    statusConsultation
    patient {
      name
      email
    }
  }
}

# 5. Deletar consulta
mutation DeleteConsult {
  deleteConsultation(id: "1")
}
```

## 📫 Testando com Postman

### Importar Collection
1. Abra o Postman
2. Clique em **Import**
3. Selecione o arquivo [`postman/EasyConsult-API.postman_collection.json`](postman/EasyConsult-API.postman_collection.json)
4. Importe também o environment [`postman/EasyConsult-Development.postman_environment.json`](postman/EasyConsult-Development.postman_environment.json)

### Collection Inclui
- 📋 **Queries** - Buscar consultas (todas, por ID, com filtros)
- ✏️ **Mutations** - Criar, atualizar e deletar consultas
- 🧪 **Cenários de Teste** - Fluxos completos e testes de filtros
- 🔍 **Schema Introspection** - Análise do schema GraphQL

## 📁 Estrutura do Projeto

```
src/
├── main/java/com/fiap/easyconsult/
│   ├── core/                           # Camada de domínio
│   │   ├── domain/
│   │   │   ├── model/                  # Entidades (UpdateConsult, etc.)
│   │   │   └── valueobject/            # Value Objects (ConsultId, ConsultStatus)
│   │   ├── inputport/                  # Interfaces de casos de uso
│   │   ├── outputport/                 # Interfaces de gateways
│   │   │   ├── SaveGateway.java
│   │   │   ├── FindByGateway.java
│   │   │   ├── UpdateGateway.java      # ✨ Novo
│   │   │   └── DeleteGateway.java      # ✨ Novo
│   │   └── usecase/                    # Implementação dos casos de uso
│   └── infra/                          # Camada de infraestrutura
│       ├── adapter/
│       │   ├── gateway/                # Implementação dos gateways
│       │   ├── redis/                  # Cache management
│       │   └── kafka/                  # Mensageria
│       ├── config/
│       │   ├── SecurityConfig.java     # ✅ Atualizado
│       │   ├── JwtTokenProvider.java   # Autenticação JWT
│       │   ├── KafkaProducerConfig.java # Configuração Kafka
│       │   └── CustomScalarConfig.java # GraphQL scalars
│       ├── entrypoint/
│       │   ├── controller/             # GraphQL controllers + Kafka controller
│       │   ├── dto/                    # DTOs de entrada/saída
│       │   │   ├── ConsultationUpdateRequestDto.java  # ✨ Novo
│       │   │   └── ...
│       │   └── mapper/                 # Mapeamento de objetos
│       └── persistence/                # JPA entities e repositories
└── resources/
    ├── graphql/schema.graphqls         # ✅ Schema atualizado
    ├── application.properties          # ✨ Novo - Config base
    ├── application-dev.properties      # ✅ Atualizado
    └── application-prod.properties     # ✅ Atualizado
```

## 🧪 Testes Automatizados

O EasyConsult possui uma **suíte de testes de alta qualidade** seguindo **TDD (Test-Driven Development)** com cobertura completa das funcionalidades.

### 📋 **Estrutura Organizada**

```
src/test/
├── java/com/fiap/easyconsult/
│   ├── unit/                           📁 TESTES UNITÁRIOS (8 arquivos)
│   │   ├── controller/                 ✅ Resolvers GraphQL
│   │   ├── domain/model/               ✅ Regras de negócio (Consult, Patient, Professional)
│   │   ├── domain/valueobject/         ✅ Value Objects (ConsultStatus, ConsultDateTime)
│   │   └── usecase/                    ✅ Casos de uso (Command + Query)
│   └── integration/                    📁 TESTES DE INTEGRAÇÃO (4 arquivos)
│       ├── config/                     ✅ Configuração de segurança para testes
│       ├── graphql/                    ✅ Testes GraphQL completos
│       ├── simple/                     ✅ Context loading
│       └── usecase/                    ✅ Integração de casos de uso
└── resources/
    └── application-test.properties     ✅ Profile de teste com H2
```

### 🎯 **Funcionalidades Testadas**

#### **🔬 Testes Unitários (JUnit 5 + Mockito)**
- ✅ **Regras de agendamento**: Validações de data/hora, campos obrigatórios
- ✅ **Lógica de cancelamento**: Apenas consultas SCHEDULED podem ser canceladas
- ✅ **Transições de status**: SCHEDULED → CANCELLED/COMPLETED/NO_SHOW
- ✅ **Resolvers GraphQL**: Dados esperados e tratamento de erros
- ✅ **Value Objects**: Validações de domínio e regras de negócio
- ✅ **Use Cases**: Commands e Queries com mocks adequados

#### **🔗 Testes de Integração (TestRestTemplate + H2)**
- ✅ **Queries GraphQL**: `getAllConsultations`, `getFilteredConsultations` contra schema real
- ✅ **Mutations GraphQL**: `createFullConsultation`, `updateConsultation`, `deleteConsultation` com persistência
- ✅ **Fluxos E2E**: Create → Read → Update → Delete completos
- ✅ **Tratamento de erros**: GraphQL errors, campos inválidos, dados ausentes
- ✅ **Schema validation**: Introspection e validação de tipos
- ✅ **Status principal**: **6/6 testes GraphQL passando perfeitamente**

### 🚀 **Execução dos Testes**

```bash
# Executar todos os testes
./mvnw test

# Testes por categoria
./mvnw test -Dtest="com.fiap.easyconsult.unit.**"        # Apenas unitários
./mvnw test -Dtest="com.fiap.easyconsult.integration.**" # Apenas integração

# Testes específicos importantes
./mvnw test -Dtest="ConsultTest"                         # Regras de negócio principais
./mvnw test -Dtest="GraphQLWorkingIntegrationTest"       # GraphQL funcionando (6 testes)

# Com relatório de cobertura
./mvnw test jacoco:report
```

### 📊 **Qualidade e Cobertura**

| Categoria | Arquivos | Cobertura | Status |
|-----------|----------|-----------|--------|
| **Testes Unitários** | 8 arquivos | 100% domínio | ✅ Completo |
| **Testes Integração** | 4 arquivos | 100% GraphQL | ✅ Funcionando |
| **Regras de Negócio** | Todas | 100% | ✅ Validadas |
| **Endpoints GraphQL** | Todos | 100% | ✅ Testados |
| **Cenários de Erro** | Abrangente | 100% | ✅ Cobertos |

### 🏆 **Práticas de Excelência**
- ✅ **TDD**: Estrutura Given-When-Then clara em todos os testes
- ✅ **Organização**: `@Nested` classes e `@DisplayName` descritivos em português
- ✅ **Isolamento**: Testes independentes com rollback automático
- ✅ **Mocking**: Mockito para dependências externas adequadamente utilizado
- ✅ **Ambiente dedicado**: Profile de teste com H2, cache simples e JWT configurado
- ✅ **Limpeza**: Estrutura otimizada sem pastas vazias ou arquivos redundantes

## ⚙️ Configuração de Ambiente

### Variáveis de Ambiente

| Variável | Desenvolvimento | Produção | Descrição |
|----------|----------------|----------|-----------|
| `SPRING_PROFILES_ACTIVE` | `dev` | `prod` | Perfil ativo do Spring |
| `DATABASE_URL` | `localhost:5432` | `db:5432` | URL do PostgreSQL |
| `REDIS_HOST` | `localhost` | `redis` | Host do Redis |
| `JWT_SECRET` | Auto-gerado | Configurar | Chave secreta JWT |

### Arquivos de Configuração

- `application.properties` - Configurações base
- `application-dev.properties` - Desenvolvimento (localhost)  
- `application-prod.properties` - Produção (Docker containers)

### 🔄 Configuração Automática

A aplicação detecta automaticamente o ambiente:

- **Local**: Conecta em `localhost:9092`
- **Docker**: Conecta em `kafka:29092` (rede interna)

## 🚨 Troubleshooting

### Erro 403 Forbidden
Se encontrar erro 403 ao acessar `/graphql`:
1. Verifique se a aplicação está rodando
2. Use a interface GraphiQL em `/graphiql`
3. Confirme que o perfil `dev` está ativo

### Problemas de Cache
Para limpar o cache Redis:
```bash
docker exec -it easyconsult-redis redis-cli FLUSHALL
```

### Problemas de Banco
Para resetar o banco PostgreSQL:
```bash
docker-compose down -v
docker-compose --profile infra up -d
```

### **Problema: Kafka não inicia**
```bash
# Verificar logs do Zookeeper primeiro
docker-compose logs zookeeper

# Depois verificar logs do Kafka
docker-compose logs kafka

# Reiniciar serviços na ordem correta
docker-compose restart zookeeper
sleep 10
docker-compose restart kafka
```

### **Problema: Aplicação não conecta no Kafka**
```bash
# Verificar se o Kafka está saudável
docker-compose ps

# Testar conectividade
docker exec kafka kafka-topics --bootstrap-server kafka:29092 --list
```

### **Problema: Porta ocupada**
```bash
# Verificar portas em uso
netstat -tulpn | grep -E ':(8080|8081|9092|2181|5432|6379)'

# Parar serviços conflitantes
docker-compose down
```

## 👥 Time

Desenvolvido com ❤️ pela equipe **Girls Tech Challenges**

### Funcionalidades Implementadas nesta Branch
- ✅ **CRUD completo** para consultas via GraphQL
- ✅ **Sistema de cache Redis** com invalidação inteligente
- ✅ **Integração Apache Kafka** para eventos de consulta
- ✅ **Docker Compose** com stack completa (PostgreSQL, Redis, Kafka, Zookeeper)
- ✅ **Kafka UI** para monitoramento em tempo real
- ✅ **Arquitetura hexagonal** bem estruturada
- ✅ **Testes automatizados** (unitários e integração)
- ✅ **Interface GraphiQL** para desenvolvimento
- ✅ **Autenticação JWT** configurada
- ✅ **Detecção automática** de ambiente (local/Docker)

---

## 📚 Links Úteis

- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [Spring Kafka Reference](https://docs.spring.io/spring-kafka/docs/current/reference/html/)
- [GraphQL Documentation](https://graphql.org/learn/)
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)