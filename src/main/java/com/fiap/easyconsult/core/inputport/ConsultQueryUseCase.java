package com.fiap.easyconsult.core.inputport;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.core.domain.model.ConsultationFilter;

import java.util.List;

public interface ConsultQueryUseCase {

    List<Consult> findWithFilters(ConsultationFilter consultationFilter);
    List<Consult> findAll();

}
