package com.fiap.easyconsult.core.outputport;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.core.domain.model.ConsultationFilter;

import java.util.List;

public interface FindByGateway {

     List<Consult> findWithFilters(ConsultationFilter consultationFilter);
     List<Consult> findAll();
}
