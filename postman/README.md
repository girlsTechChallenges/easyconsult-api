# 📫 Postman Collection - EasyConsult API

Esta pasta contém os arquivos necessários para testar a API EasyConsult usando o Postman.

## 📋 Arquivos Incluídos

- **`EasyConsult-API.postman_collection.json`** - Collection principal com todas as requisições
- **`EasyConsult-Development.postman_environment.json`** - Environment de desenvolvimento com variáveis configuradas

## 🚀 Como Importar

### 1. Importar a Collection

1. Abra o **Postman**
2. Clique em **Import** (canto superior esquerdo)
3. Selecione o arquivo `EasyConsult-API.postman_collection.json`
4. Clique em **Import**

### 2. Importar o Environment

1. No Postman, clique no ícone de **⚙️ Settings** (gear icon)
2. Vá para **Environments**
3. Clique em **Import**
4. Selecione o arquivo `EasyConsult-Development.postman_environment.json`
5. Clique em **Import**

### 3. Configurar o Environment

1. No canto superior direito, selecione **EasyConsult - Development**
2. Verifique se a variável `base_url` está definida como `http://localhost:8081`

## 📚 Estrutura da Collection

### 📋 Queries - Consultas
- **Buscar Todas as Consultas** - Lista todas as consultas disponíveis
- **Buscar Consulta por ID** - Busca uma consulta específica pelo ID
- **Buscar Consultas com Filtros** - Busca consultas com filtros opcionais

### ✏️ Mutations - Operações
- **Criar Nova Consulta** - Cria uma nova consulta
- **Atualizar Consulta Existente** - Atualiza uma consulta existente
- **Deletar Consulta** - Remove uma consulta

### 🧪 Cenários de Teste
- **Fluxo Completo** - Testa o ciclo completo: Criar → Listar → Atualizar → Deletar
- **Teste de Filtros** - Testa diferentes filtros de busca

### 🔍 Schema Introspection
- **Obter Schema GraphQL** - Obtém o schema completo da API para análise

## 🔧 Variáveis Configuradas

| Variável | Valor Padrão | Descrição |
|----------|--------------|-----------|
| `base_url` | `http://localhost:8081` | URL base da aplicação |
| `graphql_endpoint` | `{{base_url}}/graphql` | Endpoint GraphQL |
| `graphiql_url` | `{{base_url}}/graphiql` | Interface GraphiQL |
| `consult_id` | `1` | ID padrão para testes |
| `patient_id` | `1` | ID padrão do paciente |
| `professional_id` | `1` | ID padrão do profissional |

## 🎯 Como Usar

### 1. Pré-requisitos
- ✅ Aplicação EasyConsult rodando em `http://localhost:8081`
- ✅ PostgreSQL e Redis iniciados (via Docker Compose)

### 2. Ordem Recomendada de Testes

1. **Início**: Execute "Buscar Todas as Consultas" para ver o estado inicial
2. **Criar**: Use "Criar Nova Consulta" para adicionar dados
3. **Verificar**: Execute novamente "Buscar Todas as Consultas"
4. **Atualizar**: Use "Atualizar Consulta Existente" (ajuste o ID se necessário)
5. **Filtrar**: Teste os diferentes filtros disponíveis
6. **Deletar**: Use "Deletar Consulta" para limpar

### 3. Dicas de Uso

#### Ajustar IDs
- Verifique os IDs retornados nas consultas
- Atualize a variável `consult_id` conforme necessário
- Use o environment para facilitar mudanças globais

#### Status Disponíveis
- `SCHEDULED` - Consulta agendada
- `CONFIRMED` - Consulta confirmada  
- `CANCELLED` - Consulta cancelada

#### Formato de Datas
- **Data**: `YYYY-MM-DD` (ex: `2025-10-15`)
- **Hora**: `HH:MM:SS` (ex: `14:30:00`)

## 🧪 Exemplos de Teste

### Teste Básico
1. Execute "Criar Nova Consulta"
2. Copie o ID retornado
3. Cole em "Buscar Consulta por ID"
4. Execute para verificar os dados

### Teste de Fluxo Completo
1. Vá para a pasta "Cenários de Teste > Fluxo Completo"
2. Execute as requisições em ordem (1 → 2 → 3 → 4)
3. Ajuste o `consult_id` entre as etapas se necessário

### Teste de Cache
1. Execute "Buscar Todas as Consultas" (primeira vez - busca no banco)
2. Execute novamente (segunda vez - busca no cache)
3. Execute "Criar Nova Consulta"
4. Execute "Buscar Todas as Consultas" (cache invalidado, busca no banco)

## 🚨 Troubleshooting

### Erro de Conexão
- ✅ Verifique se a aplicação está rodando
- ✅ Confirme a URL base: `http://localhost:8081`
- ✅ Teste diretamente no navegador: `http://localhost:8081/graphiql`

### Erro 403 Forbidden
- ✅ Aplicação configurada para ambiente de desenvolvimento
- ✅ Endpoints GraphQL são públicos em modo dev

### Erros de Dados
- ✅ Verifique se o banco PostgreSQL está rodando
- ✅ Confirme se existem dados de Patient/Professional (IDs 1)
- ✅ Use IDs válidos nas requisições

## 📊 Monitoramento

Para acompanhar os testes:
- **Logs da aplicação**: Verifique o terminal onde a aplicação está rodando
- **Cache Redis**: Use `docker exec -it easyconsult-redis redis-cli` para monitorar
- **Banco PostgreSQL**: Use `docker exec -it easyconsult-db psql -U postgres -d easyconsult`

---

💡 **Dica**: Use o **GraphiQL** (`http://localhost:8081/graphiql`) para prototipagem rápida e depois importe as queries para o Postman!