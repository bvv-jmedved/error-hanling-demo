package cz.bvv.errorhanlingdemo.builder.common.rest;

import cz.bvv.errorhanlingdemo.builder.common.rest.model.DefaultRestError;
import cz.bvv.errorhanlingdemo.exception.IntegrationException;
import java.util.List;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class DefaultRestFailureContractRoutePolicy
  extends RestFailureContractRoutePolicy<DefaultRestError> {

    @Override
    protected DefaultRestError createRestError(IntegrationException exception) {
        return  new DefaultRestError(List.of(new DefaultRestError.Error(
          String.valueOf(exception.getStatus().value()),
          exception.getMessage()
        )));

    }
}
