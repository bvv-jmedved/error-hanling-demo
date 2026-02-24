package cz.bvv.errorhandlingdemo.builder.sender;

import cz.bvv.errorhandlingdemo.builder.common.rest.RestFailureContractRoutePolicy;
import cz.bvv.errorhandlingdemo.builder.common.rest.model.DefaultRestError;
import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class DemoFailureContractRoutePolicy
  extends RestFailureContractRoutePolicy<DefaultRestError> {

    @Override
    protected DefaultRestError createRestError(IntegrationException exception) {
        return new DefaultRestError(
          exception.getErrors().stream()
            .map(e -> new DefaultRestError.Error(e.code(), e.message()))
            .toList()
        );

    }
}
