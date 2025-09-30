// ============================================================================
// POSTMAN SCRIPTS ÚTEIS PARA EASYCONSULT API
// ============================================================================
// Este arquivo contém scripts que podem ser copiados para as abas "Pre-request Script" 
// ou "Tests" das requisições do Postman para automatizar testes

// ============================================================================
// SCRIPT 1: EXTRAIR ID DE RESPOSTA (usar na aba "Tests")
// ============================================================================
// Use este script após criar uma consulta para capturar o ID automaticamente

/*
// Verifica se a resposta foi bem-sucedida
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

// Extrai ID da resposta e salva como variável
pm.test("Extract consult ID", function () {
    var jsonData = pm.response.json();
    
    // Para mutation createConsult (retorna string com ID)
    if (jsonData.data && jsonData.data.createConsult) {
        var consultId = jsonData.data.createConsult;
        pm.environment.set("consult_id", consultId);
        console.log("Consult ID saved: " + consultId);
    }
    
    // Para query findAllConsults (pega o primeiro ID da lista)
    if (jsonData.data && jsonData.data.findAllConsults && jsonData.data.findAllConsults.length > 0) {
        var firstId = jsonData.data.findAllConsults[0].id;
        pm.environment.set("consult_id", firstId);
        console.log("First consult ID saved: " + firstId);
    }
});
*/

// ============================================================================
// SCRIPT 2: VALIDAR RESPOSTA GRAPHQL (usar na aba "Tests")
// ============================================================================
// Script para validar se não há erros GraphQL na resposta

/*
pm.test("No GraphQL errors", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.errors).to.be.undefined;
});

pm.test("Response has data", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.data).to.not.be.undefined;
});
*/

// ============================================================================
// SCRIPT 3: GERAR DADOS DINÂMICOS (usar na aba "Pre-request Script")
// ============================================================================
// Script para gerar dados únicos em cada execução

/*
// Gera data futura aleatória
var today = new Date();
var futureDate = new Date(today.getTime() + (Math.random() * 30 + 1) * 24 * 60 * 60 * 1000);
var formattedDate = futureDate.toISOString().split('T')[0];
pm.environment.set("random_date", formattedDate);

// Gera horário aleatório
var hours = Math.floor(Math.random() * 8) + 8; // 8-16h
var minutes = Math.floor(Math.random() * 4) * 15; // 0, 15, 30, 45
var formattedTime = hours.toString().padStart(2, '0') + ':' + minutes.toString().padStart(2, '0') + ':00';
pm.environment.set("random_time", formattedTime);

// Gera motivo único
var reasons = [
    "Consulta de rotina",
    "Consulta de retorno", 
    "Exame preventivo",
    "Consulta de urgência",
    "Consulta especializada"
];
var randomReason = reasons[Math.floor(Math.random() * reasons.length)] + " - " + Date.now();
pm.environment.set("random_reason", randomReason);
*/

// ============================================================================
// SCRIPT 4: LIMPAR VARIÁVEIS DE TESTE (usar na aba "Tests")
// ============================================================================
// Script para limpar variáveis após os testes

/*
pm.test("Cleanup test variables", function () {
    pm.environment.unset("temp_consult_id");
    pm.environment.unset("random_date");
    pm.environment.unset("random_time");
    pm.environment.unset("random_reason");
    console.log("Test variables cleaned up");
});
*/

// ============================================================================
// SCRIPT 5: VALIDAR ESTRUTURA DE CONSULTA (usar na aba "Tests")
// ============================================================================
// Script para validar a estrutura dos dados de consulta

/*
pm.test("Validate consultation structure", function () {
    var jsonData = pm.response.json();
    
    if (jsonData.data && jsonData.data.findAllConsults) {
        var consults = jsonData.data.findAllConsults;
        
        if (consults.length > 0) {
            var consult = consults[0];
            
            // Validar campos obrigatórios
            pm.expect(consult).to.have.property('id');
            pm.expect(consult).to.have.property('reason');
            pm.expect(consult).to.have.property('status');
            pm.expect(consult).to.have.property('localDate');
            pm.expect(consult).to.have.property('localTime');
            
            // Validar relacionamentos
            if (consult.patient) {
                pm.expect(consult.patient).to.have.property('id');
                pm.expect(consult.patient).to.have.property('name');
                pm.expect(consult.patient).to.have.property('email');
            }
            
            if (consult.professional) {
                pm.expect(consult.professional).to.have.property('id');
                pm.expect(consult.professional).to.have.property('name');
                pm.expect(consult.professional).to.have.property('email');
            }
            
            // Validar status válido
            var validStatuses = ['SCHEDULED', 'CONFIRMED', 'CANCELLED'];
            pm.expect(validStatuses).to.include(consult.status);
        }
    }
});
*/