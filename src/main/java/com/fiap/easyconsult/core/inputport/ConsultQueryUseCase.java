package com.fiap.easyconsult.core.inputport;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.core.domain.model.ConsultFilter;

import java.util.List;

public interface ConsultQueryUseCase {

    List<Consult> findWithFilters(ConsultFilter consultFilter);
    List<Consult> findAll();

}
