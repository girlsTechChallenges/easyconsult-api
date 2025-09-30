package com.fiap.easyconsult.core.outputport;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.core.domain.model.UpdateConsult;

public interface UpdateGateway {
    
    Consult update(UpdateConsult updateConsult);
    
}
