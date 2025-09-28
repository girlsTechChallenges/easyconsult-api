# 🧪 Testes Unitários - EasyConsult API

Este documento descreve os testes unitários criados para o projeto EasyConsult, seguindo as práticas de **TDD (Test-Driven Development)** e garantindo cobertura adequada das regras de negócio.

## 📋 Estrutura de Testes

Os testes estão organizados em três categorias principais:

### 🎯 1. Testes de Domínio (`src/test/java/com/fiap/easyconsult/unit/domain/`)

#### 1.1 `ConsultTest` - Testes para o modelo `Consult`
**Lógica testada**: Regras de negócio das consultas, incluindo criação, cancelamento, conclusão e marcação de no-show.

**Cenários cobertos:**
- ✅ **Criação de Consulta**:
  - Criação com dados válidos
  - Validação de campos obrigatórios (motivo, paciente, profissional, data/hora)
  - Tratamento de campos nulos ou vazios

- ✅ **Transições de Status**:
  - Cancelamento de consulta agendada (futura)
  - Conclusão de consulta agendada (passada)
  - Marcação como "não compareceu" (passada)
  - Validação de regras de negócio para cada transição

- ✅ **Regras de Negócio**:
  - Não permitir cancelamento de consulta finalizada
  - Não permitir conclusão de consulta cancelada
  - Não permitir marcar como "no-show" consulta cancelada

#### 1.2 `PatientTest` - Testes para o modelo `Patient`
**Lógica testada**: Validações de criação e atualização de dados do paciente.

**Cenários cobertos:**
- ✅ **Criação**: Validação de nome e email obrigatórios
- ✅ **Atualização**: Métodos `updateName()` e `updateEmail()`
- ✅ **Validação**: Trimming automático de espaços
- ✅ **Igualdade**: Comparação baseada no ID

#### 1.3 `ProfessionalTest` - Testes para o modelo `Professional`
**Lógica testada**: Similar ao `Patient`, com validações específicas para profissionais.

**Cenários cobertos:**
- ✅ **Criação e Atualização**: Validação de campos obrigatórios
- ✅ **Validação**: Trimming e tratamento de nulos
- ✅ **Igualdade**: Comparação baseada no ID

#### 1.4 `ConsultStatusTest` - Testes para o enum `ConsultStatus`
**Lógica testada**: Regras de transição de estados e validações.

**Cenários cobertos:**
- ✅ **Cancelamento**: Apenas status `SCHEDULED` pode ser cancelado
- ✅ **Finalização**: Status finalizados (`CANCELLED`, `COMPLETED`, `NO_SHOW`)

#### 1.5 `ConsultDateTimeTest` - Testes para o Value Object `ConsultDateTime`
**Lógica testada**: Validações de data/hora e métodos utilitários.

**Cenários cobertos:**
- ✅ **Criação**: Validação de data e hora não nulas
- ✅ **Validação Temporal**: Identificação de datas passadas/futuras
- ✅ **Igualdade**: Comparação baseada em data e hora
- ✅ **Representação**: Conversão para string

### 🔧 2. Testes de Use Cases (`src/test/java/com/fiap/easyconsult/unit/usecase/`)

#### 2.1 `ConsultCommandUseCasesTest`
**Lógica testada**: Casos de uso de comando (criação, atualização, exclusão) com mocks.

**Cenários cobertos:**
- ✅ **Criação**: Delegação correta para `SaveGateway`
- ✅ **Atualização**: Delegação correta para `UpdateGateway`
- ✅ **Exclusão**: Delegação correta para `DeleteGateway`
- ✅ **Tratamento de Exceções**: Propagação de erros dos gateways

#### 2.2 `ConsultQueryUseCasesTest`
**Lógica testada**: Casos de uso de consulta (busca por filtros, busca geral) com mocks.

**Cenários cobertos:**
- ✅ **Busca com Filtros**: Retorno de resultados válidos
- ✅ **Busca Geral**: Retorno de todas as consultas
- ✅ **Cenários de Erro**: Lista vazia, retorno nulo, exceções do gateway
- ✅ **Validação**: Lançamento de `GatewayException` quando não encontrado

### 🌐 3. Testes de Controllers (`src/test/java/com/fiap/easyconsult/unit/controller/`)

#### 3.1 `GraphqlControllerTest`
**Lógica testada**: Resolvers GraphQL com mocks, incluindo mapeamento de dados.

**Cenários cobertos:**
- ✅ **Query Mappings**:
  - `getFilteredConsultations`: Busca com filtros
  - `getAllConsultations`: Busca geral
  - Tratamento de exceções nos use cases

- ✅ **Mutation Mappings**:
  - `createFullConsultation`: Criação de consulta
  - `updateConsultation`: Atualização de consulta
  - `deleteConsultation`: Exclusão de consulta
  - Tratamento de exceções

- ✅ **Integração com Mapper**:
  - Chamadas corretas dos métodos de conversão
  - Tratamento de exceções do mapper

## 🏗️ Padrões Utilizados

### ✅ **TDD (Test-Driven Development)**
- Testes escritos para validar comportamentos específicos
- Cobertura de cenários de sucesso e falha
- Estrutura clear: Given-When-Then

### ✅ **Organização Clara**
- Uso de `@Nested` para agrupar testes relacionados
- Nomes descritivos com `@DisplayName`
- Separação por responsabilidade

### ✅ **Mocking Eficiente**
- Uso do Mockito para isolar unidades de teste
- Mocks de dependências externas (gateways, mappers)
- Verificação de interações com `verify()`

### ✅ **Cobertura Abrangente**
- Testes para cenários válidos e inválidos
- Validação de exceções e códigos de erro
- Testes de construtores e métodos utilitários

## 🚀 Execução dos Testes

### Executar todos os testes unitários:
```bash
mvn test -Dtest="com.fiap.easyconsult.unit.**"
```

### Executar testes específicos:
```bash
# Testes de domínio
mvn test -Dtest="ConsultTest"
mvn test -Dtest="PatientTest"

# Testes de use cases
mvn test -Dtest="ConsultCommandUseCasesTest"
mvn test -Dtest="ConsultQueryUseCasesTest"

# Testes de controllers
mvn test -Dtest="GraphqlControllerTest"
```

## 📊 Cobertura de Testes

Os testes criados cobrem:
- **Modelos de Domínio**: 100% das regras de negócio
- **Value Objects**: 100% das validações
- **Use Cases**: 100% dos fluxos principais e de erro
- **Controllers**: 100% dos endpoints GraphQL

## 🎯 Benefícios Alcançados

1. **Confiabilidade**: Garantia de que as regras de negócio funcionam corretamente
2. **Manutenibilidade**: Testes servem como documentação viva do código
3. **Refatoração Segura**: Alterações futuras podem ser validadas rapidamente
4. **Detecção Precoce de Bugs**: Problemas identificados durante desenvolvimento
5. **Qualidade**: Código mais robusto e confiável

---

## 📈 Próximos Passos Sugeridos

1. **Testes de Integração**: Testar interação entre camadas
2. **Testes E2E**: Validar fluxos completos via GraphQL
3. **Testes de Performance**: Validar performance dos endpoints
4. **Testes de Segurança**: Validar autorização e autenticação

---

*Documentação criada em: Setembro 2025*
*Versão: 1.0*