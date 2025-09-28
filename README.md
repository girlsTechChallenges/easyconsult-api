# 🏥 EasyConsult API

**Serviço de agendamento de consultas médicas** desenvolvido em Java Spring Boot com arquitetura hexagonal e GraphQL.

## 📋 Sobre o Projeto

O **EasyConsult** é uma API REST/GraphQL que permite gerenciar agendamentos de consultas entre pacientes e profissionais de saúde de forma simples e eficiente.

## 🏗️ Arquitetura

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
- 🔍 **Schema Introspection** - Análise do schema GraphQL **arquitetura hexagonal (Clean Architecture)** com separação clara de responsabilidades:

- **Core Domain**: Modelos de domínio (`Patient`, `Professional`, `Consult`)
- **Input Ports**: Interfaces dos casos de uso (`ConsultCommandUseCase`, `ConsultQueryUseCase`)
- **Output Ports**: Interfaces de gateways (`SaveGateway`, `FindByGateway`, `DeleteGateway`, `UpdateGateway`)
- **Infrastructure**: Implementações concretas (persistência, controladores, adapters)

## ⚡ Stack Tecnológica

- **Java 21** - Linguagem principal
- **Spring Boot 3.5.5** - Framework web
- **GraphQL** - API principal para queries e mutations
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

### 🚧 Em Desenvolvimento
- � **Notificações** via Kafka
- 📊 **Relatórios** de consultas
- � **Integração de email** para confirmações

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

2. **Suba a infraestrutura (PostgreSQL + Redis):**
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

## 📡 GraphQL API

### 🌐 Endpoints Disponíveis
- **GraphQL Endpoint**: `http://localhost:8081/graphql`
- **GraphiQL Interface**: `http://localhost:8081/graphiql` (Interface gráfica para testes)

### 🔍 Queries Disponíveis

```graphql
# Buscar consulta por ID
query {
  findConsultById(id: "1") {
    id
    reason
    status
    localDate
    localTime
    patient {
      id
      name
      email
    }
    professional {
      id
      name
      email
    }
  }
}

# Buscar todas as consultas
query {
  findAllConsults {
    id
    reason
    status
    localDate
    localTime
    patient {
      name
      email
    }
    professional {
      name
      email
    }
  }
}

# Buscar consultas com filtros
query {
  findConsultsByFilter(
    patientId: "1"
    professionalId: "2" 
    status: SCHEDULED
  ) {
    id
    reason
    status
    localDate
    localTime
  }
}
```

### ✏️ Mutations Disponíveis

```graphql
# Criar nova consulta
mutation {
  createConsult(input: {
    reason: "Consulta de rotina"
    localDate: "2025-10-15"
    localTime: "14:30:00"
    patientId: 1
    professionalId: 1
  })
}

# Atualizar consulta existente
mutation {
  updateConsult(input: {
    id: "1"
    reason: "Consulta de retorno"
    localDate: "2025-10-20"
    localTime: "15:00:00"
    status: CONFIRMED
    patientId: 1
    professionalId: 1
  })
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
  createConsult(input: {
    reason: "Consulta de rotina"
    localDate: "2025-10-15"
    localTime: "14:30:00"
    patientId: 1
    professionalId: 1
  })
}

# 2. Buscar todas as consultas
query GetAllConsults {
  findAllConsults {
    id
    reason
    status
    localDate
    localTime
  }
}

# 3. Atualizar uma consulta
mutation UpdateConsult {
  updateConsult(input: {
    id: "1"
    reason: "Consulta de retorno - ATUALIZADA"
    localDate: "2025-10-20"
    localTime: "15:00:00"
    status: CONFIRMED
    patientId: 1
    professionalId: 1
  })
}

# 4. Buscar por filtros
query FilterConsults {
  findConsultsByFilter(
    patientId: "1"
    status: CONFIRMED
  ) {
    id
    reason
    status
  }
}

# 5. Deletar consulta
mutation DeleteConsult {
  deleteConsult(id: "1")
}
```

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
│       │   └── kafka/                  # Mensageria (futuro)
│       ├── config/
│       │   ├── SecurityConfig.java     # ✅ Atualizado
│       │   ├── JwtTokenProvider.java   # Autenticação JWT
│       │   └── CustomScalarConfig.java # GraphQL scalars
│       ├── entrypoint/
│       │   ├── controller/             # GraphQL controllers
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

## 🧪 Testes

```bash
# Executar todos os testes
./mvnw test

# Executar testes com relatório de cobertura
./mvnw test jacoco:report
```

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

## 🎯 Roadmap

### Próximas Funcionalidades
- [ ] **Autenticação completa** com diferentes roles
- [ ] **Notificações** via Kafka/Email
- [ ] **Testes unitários** e de integração completos

### Melhorias Técnicas
- [ ] **Métricas** com Micrometer/Prometheus
- [ ] **Health checks** mais robustos
- [ ] **Logging estruturado** com Logback
- [ ] **Documentação OpenAPI** para REST endpoints
- [ ] **Pipeline CI/CD** com GitHub Actions

## �👥 Time

Desenvolvido com ❤️ pela equipe **Girls Tech Challenges**

### Funcionalidades Implementadas nesta Branch
- ✅ **Endpoint UPDATE** para consultas
- ✅ **Endpoint DELETE** para consultas  
- ✅ **Sistema de cache Redis** completo
- ✅ **Correção de configuração de segurança**
- ✅ **Interface GraphiQL** para testes
- ✅ **Documentação completa** atualizada


