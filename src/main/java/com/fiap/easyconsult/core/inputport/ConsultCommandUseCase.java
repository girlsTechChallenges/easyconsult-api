package com.fiap.easyconsult.core.inputport;

import com.fiap.easyconsult.core.domain.model.Consult;

public interface ConsultCommandUseCase {

    Consult createConsultation(Consult consult);

}
