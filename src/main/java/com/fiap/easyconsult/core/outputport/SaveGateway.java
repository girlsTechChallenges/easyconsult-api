package com.fiap.easyconsult.core.outputport;

import com.fiap.easyconsult.core.domain.model.Consult;

public interface SaveGateway {

    Consult save(Consult consult);

}
