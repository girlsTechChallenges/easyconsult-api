package com.fiap.easyconsult.integration.simple;

import com.fiap.easyconsult.EasyconsultMain;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = EasyconsultMain.class)
@ActiveProfiles("test")
public class SimpleIntegrationTest {

    @Test
    void contextLoads() {
        // Este teste apenas verifica se o contexto do Spring carrega corretamente
    }
}