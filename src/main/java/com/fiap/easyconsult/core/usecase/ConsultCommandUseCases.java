package com.fiap.easyconsult.core.usecase;

import com.fiap.easyconsult.core.domain.model.Consult;
import com.fiap.easyconsult.core.domain.model.UpdateConsult;
import com.fiap.easyconsult.core.inputport.ConsultCommandUseCase;
import com.fiap.easyconsult.core.outputport.DeleteGateway;
import com.fiap.easyconsult.core.outputport.SaveGateway;
import com.fiap.easyconsult.core.outputport.UpdateGateway;
import org.springframework.stereotype.Component;

@Component
public class ConsultCommandUseCases implements ConsultCommandUseCase {

    private final SaveGateway saveGateway;
    private final UpdateGateway updateGateway;
    private final DeleteGateway deleteGateway;

    public ConsultCommandUseCases(SaveGateway saveGateway, UpdateGateway updateGateway, DeleteGateway deleteGateway) {
        this.saveGateway = saveGateway;
        this.updateGateway = updateGateway;
        this.deleteGateway = deleteGateway;
    }

    @Override
    public Consult createConsult(Consult consult) {
        return saveGateway.save(consult);
    }

    @Override
    public Consult updateConsult(UpdateConsult updateConsult) {
        return updateGateway.update(updateConsult);
    }

    @Override
    public void deleteConsult(Long consultId) {
        deleteGateway.delete(consultId);
    }

}
