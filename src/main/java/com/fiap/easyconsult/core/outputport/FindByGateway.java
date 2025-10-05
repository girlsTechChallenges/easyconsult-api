package com.fiap.easyconsult.core.outputport;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.core.domain.model.ConsultFilter;

import java.util.List;

public interface FindByGateway {

     List<Consult> findWithFilters(ConsultFilter consultFilter);
     List<Consult> findAll();
}
