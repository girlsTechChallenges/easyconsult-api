package com.fiap.easyconsult.core.inputport;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.core.domain.model.UpdateConsult;

import java.util.Optional;

public interface ConsultCommandUseCase {

    Consult createConsultation(Consult consult);

    Optional<UpdateConsult> updateConsultation(UpdateConsult updateConsult);

}
