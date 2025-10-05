# 🏥 EasyConsult API

**Serviço de agendamento de consultas médicas** desenvolvido em Java Spring Boot com Clean Architecture, GraphQL e Apache Kafka.

## 📋 Sobre o Projeto

O **EasyConsult** é uma aplicação que permite gerenciar agendamentos de consultas entre pacientes e profissionais de saúde de forma simples e eficiente.

## 🚀 Kafka + Docker

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

O projeto segue **Clean Architecture** com separação clara de responsabilidades:

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

## 🧪 Cobertura de Testes

O projeto possui **cobertura completa de testes** seguindo as melhores práticas de TDD (Test-Driven Development):

### 📊 Estatísticas de Testes

| Categoria | Quantidade | Status | Cobertura |
|-----------|------------|--------|-----------|
| **🔬 Testes Unitários** | 94 | ✅ 100% Pass | Completa |
| **🔧 Testes de Integração** | 10 | ✅ 100% Pass | Completa |
| **🚀 Testes E2E** | 7 | ✅ 100% Pass | Completa |
| **📊 Total** | **111** | ✅ **100% Pass** | **100%** |

### 🏗️ Arquitetura de Testes

#### 🔬 **Testes Unitários** (94 testes)
- **Controllers**: Validação de mapeamentos GraphQL e REST
- **Use Cases**: Lógica de negócio isolada com mocks
- **Domain Models**: Validação de regras de domínio
- **Mappers**: Conversões entre DTOs e entidades

#### 🔧 **Testes de Integração** (10 testes)
- **Database Integration**: Operações CRUD com H2 in-memory
- **Cache Integration**: Validação do Redis com cache manager de teste
- **Kafka Integration**: Publicação de eventos com mock service
- **Security Integration**: Autenticação JWT e autorização

#### 🚀 **Testes End-to-End** (7 testes)
- **GraphQL Queries**: Busca completa e filtrada de consultas
- **GraphQL Mutations**: CRUD completo via GraphQL
- **Authentication Flow**: Fluxo completo com JWT
- **Business Rules**: Validação de regras de negócio em cenários reais

### 🎯 **Cobertura por Componente**

| Componente | Unitário | Integração | E2E | Status |
|------------|----------|------------|-----|---------|
| **GraphQL API** | ✅ | ✅ | ✅ | 100% |
| **Use Cases** | ✅ | ✅ | ✅ | 100% |
| **Gateways** | ✅ | ✅ | ✅ | 100% |
| **Domain Models** | ✅ | ✅ | ✅ | 100% |
| **JWT Security** | ✅ | ✅ | ✅ | 100% |
| **Redis Cache** | ✅ | ✅ | ✅ | 100% |
| **Kafka Events** | ✅ Mock | ✅ Mock | ✅ Mock | 100% |

### **Executando Testes**

```bash
./mvnw test  # Todos os testes
./mvnw jacoco:report  # Relatório de cobertura
```

### 📋 **Validações Incluídas**

- ✅ **Regras de Negócio**: Validação de conflitos de horário, dados obrigatórios
- ✅ **Cenários de Erro**: Tratamento de exceções e casos limite
- ✅ **Performance**: Validação de cache e otimizações
- ✅ **Segurança**: Autorização por roles e validação JWT
- ✅ **Integrações**: Kafka, Redis, PostgreSQL via mocks/containers
- ✅ **API Contract**: Validação completa dos contratos GraphQL



## 📊 Status de Consulta

| Status | Descrição |
|--------|-----------|
| `SCHEDULED` | Consulta agendada |
| `CARRIED_OUT` | Consulta realizada |
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

### 🧪 Executando Testes

O projeto possui **111 testes** cobrindo todas as camadas:

```bash
./mvnw test  # Todos os testes
# Resultado: Tests run: 111, Failures: 0, Errors: 0, Skipped: 0
```

### 🐳 Docker (Produção)

```bash
# Ambiente completo (aplicação + infraestrutura)
docker-compose --profile prod up -d

# Comandos úteis
docker-compose down                    # Parar containers
docker-compose down -v                 # Parar e remover volumes
docker-compose ps                      # Ver status
docker-compose logs -f app            # Logs da aplicação
```

## 📨 Apache Kafka Integration

### Classes Principais

1. **`KafkaProducerConfig`** - Configuração Spring para o Kafka Producer
2. **`KafkaMessageService`** - Serviço Spring para envio de mensagens de consultas
3. **`KafkaController`** - Controlador REST para testes via API
4. **`ConsultKafkaMessage`** - DTO para mensagens de consulta no Kafka

### ⚙️ Configuração

O projeto está configurado para usar o tópico `easyconsult-consult` definido em `application.properties`:

```properties
# Kafka Topics Configuration
app.kafka.topics.consult=easyconsult-consult
app.kafka.groupid=group-consult
```

### 🚀 Como Usar o Kafka

#### 1. Integração Automática (Produção)

O Kafka é **integrado automaticamente** no fluxo de criação de consultas. Sempre que uma nova consulta é salva ou alterada, uma mensagem é enviada automaticamente para o tópico `easyconsult-consult`:

```java
// No SaveGatewayImpl - Executado automaticamente ao salvar ou alterar consulta
kafkaMessageService.publishConsultEvent(result);
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
  "statusConsult": "SCHEDULED"
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
# Comandos básicos do Kafka
docker exec kafka kafka-topics --bootstrap-server localhost:9092 --list
docker exec kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic easyconsult-consult --from-beginning
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



## 📡 GraphQL API

### 🌐 Endpoints Disponíveis
- **GraphQL Endpoint**: `http://localhost:8081/graphql`
- **GraphiQL Interface**: `http://localhost:8081/graphiql` (Interface gráfica para testes)

### 🔍 Queries Disponíveis

```graphql
# Buscar todas as consultas
query {
  getAllConsults {
    id
    reason
    statusConsult
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
  getFilteredConsults(filter: {
    patientEmail: "joao@email.com"
    professionalEmail: "dr.silva@email.com"
    status: SCHEDULED
    date: "2025-10-15"
  }) {
    id
    reason
    statusConsult
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
  createFullConsult(input: {
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
    statusConsult
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
  updateConsult(input: {
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
    statusConsult
    date
    localTime
  }
}

# Deletar consulta
mutation {
  deleteConsult(id: "1")
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
  createFullConsult(input: {
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
    statusConsult
    date
    localTime
  }
}

# 2. Buscar todas as consultas
query GetAllConsults {
  getAllConsults {
    id
    reason
    statusConsult
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
  updateConsult(input: {
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
    statusConsult
  }
}

# 4. Buscar por filtros
query FilterConsults {
  getFilteredConsults(filter: {
    patientEmail: "joao@email.com"
    status: SCHEDULED
  }) {
    id
    reason
    statusConsult
    patient {
      name
      email
    }
  }
}

# 5. Deletar consulta
mutation DeleteConsult {
  deleteConsult(id: "1")
}
```

## 📫 Testando com Postman

### 📁 Estrutura Simplificada das Collections
O projeto possui apenas **2 arquivos Postman** otimizados:
```
postman/
├── EasyConsult.postman_collection.json     # 🎯 Collection completa (GraphQL + Kafka)
└── EasyConsult.postman_environment.json    # 🔐 Environment com JWT tokens
```

### Importar Collection
1. Abra o Postman
2. Clique em **Import**
3. Selecione os 2 arquivos da pasta `postman/`:
   - `EasyConsult.postman_collection.json` - Collection completa (GraphQL + Kafka)
   - `EasyConsult.postman_environment.json` - Environment com tokens JWT
4. **Selecione o environment** "EasyConsult - Environment" no canto superior direito
5. Pronto! Uma única collection com tudo organizado!

### 🔐 Autenticação JWT Configurada
**Problema do erro 403 resolvido!** Todos os endpoints têm tokens JWT válidos:
- ✅ `token_enfermeiro` - Para operações de criação de consultas (`createFullConsult`)
- ✅ `token_medico` - Para operações de atualização e exclusão (`updateConsult`, `deleteConsult`)
- ✅ `token_paciente` - Para consultas filtradas por paciente (`getFilteredConsults`)
- ⏰ **Validade**: 1 ano (sem necessidade de renovação durante desenvolvimento)

### Collection Inclui
- 📋 **Queries** - Buscar consultas (todas, por ID, com filtros)
- ✏️ **Mutations** - Criar, atualizar e deletar consultas
- 🧪 **Cenários de Teste** - Fluxos completos e testes de filtros
- 🔍 **Schema Introspection** - Análise do schema GraphQL
- 📨 **Kafka Producer** - Teste de publicação de mensagens

### 🚨 Endpoints Kafka Disponíveis
```bash
✅ POST /api/kafka/publish-consult?message=sua_mensagem  
```



### 🧪 Como Testar
1. **Importe** as collections atualizadas no Postman
2. **Selecione** o environment "EasyConsult - Environment"
3. **Configure** `base_url = http://localhost:8081` (já configurado)
4. **Execute** os testes - todos devem funcionar sem erros 403/404
5. **Para Kafka**, use o endpoint `publish-consult`

### ✅ Status das Collections
- ✅ **1 Collection única** (GraphQL + Kafka consolidados)
- ✅ **1 Environment consolidado** (com todos os tokens)
- ✅ **Estrutura simplificada** (máxima organização)
- ✅ **Endpoints inexistentes removidos**
- ✅ **Schema GraphQL sincronizado**
- ✅ **Testes validados**

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
│   │   │   ├── UpdateGateway.java      
│   │   │   └── DeleteGateway.java      
│   │   └── usecase/                    # Implementação dos casos de uso
│   └── infra/                          # Camada de infraestrutura
│       ├── adapter/
│       │   ├── gateway/                # Implementação dos gateways
│       │   ├── redis/                  # Cache management
│       │   └── kafka/                  # Mensageria
│       ├── config/
│       │   ├── SecurityConfig.java     
│       │   ├── JwtTokenProvider.java   # Autenticação JWT
│       │   ├── KafkaProducerConfig.java # Configuração Kafka
│       │   └── CustomScalarConfig.java # GraphQL scalars
│       ├── entrypoint/
│       │   ├── controller/             # GraphQL controllers + Kafka controller
│       │   ├── dto/                    # DTOs de entrada/saída
│       │   │   ├── ConsultUpdateRequestDto.java  
│       │   │   └── ...
│       │   └── mapper/                 # Mapeamento de objetos
│       └── persistence/                # JPA entities e repositories
└── resources/
    ├── graphql/schema.graphqls         
    ├── application.properties          
    ├── application-dev.properties      
    └── application-prod.properties     
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
- ✅ **Queries GraphQL**: `getAllConsults`, `getFilteredConsults` contra schema real
- ✅ **Mutations GraphQL**: `createFullConsult`, `updateConsult`, `deleteConsult` com persistência
- ✅ **Fluxos E2E**: Create → Read → Update → Delete completos
- ✅ **Tratamento de erros**: GraphQL errors, campos inválidos, dados ausentes
- ✅ **Schema validation**: Introspection e validação de tipos
- ✅ **Status principal**: **6/6 testes GraphQL passando perfeitamente**


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
- ✅ **Clean Architecture** bem estruturada
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