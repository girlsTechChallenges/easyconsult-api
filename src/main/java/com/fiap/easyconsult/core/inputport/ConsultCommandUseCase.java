package com.fiap.easyconsult.core.inputport;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.core.domain.model.UpdateConsult;

public interface ConsultCommandUseCase {

    Consult createConsultation(Consult consult);
    
    Consult updateConsultation(UpdateConsult updateConsult);
    
    void deleteConsultation(Long consultId);

}
