package cz.bvv.errorhanlingdemo.builder.sender;

import cz.bvv.errorhanlingdemo.builder.common.rest.RestFailureContractRoutePolicy;
import cz.bvv.errorhanlingdemo.builder.sender.model.DemoError;
import cz.bvv.errorhanlingdemo.exception.IntegrationException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class DemoFailureContractRoutePolicy
  extends RestFailureContractRoutePolicy<DemoError> {

    @Override
    protected DemoError createRestError(IntegrationException exception) {
        return  new DemoError(
          exception.getErrors().stream()
            .map(e -> new DemoError.Error(e.code(), e.message()))
            .toList()
        );

    }
}
