package com.fiap.easyconsult.core.inputport;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.core.domain.model.UpdateConsult;

public interface ConsultCommandUseCase {

    Consult createConsult(Consult consult);
    
    Consult updateConsult(UpdateConsult updateConsult);
    
    void deleteConsult(Long consultId);

}
