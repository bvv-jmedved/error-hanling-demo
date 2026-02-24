package cz.bvv.errorhandlingdemo.builder.common.rest;

import cz.bvv.errorhandlingdemo.builder.common.rest.model.DefaultRestError;
import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class DefaultRestFailureContractRoutePolicy
  extends RestFailureContractRoutePolicy<DefaultRestError> {

    @Override
    protected DefaultRestError createRestError(IntegrationException exception) {
        return new DefaultRestError(
          exception.getErrors().stream()
            .map(error -> new DefaultRestError.Error(error.code(), error.message()))
            .toList()
        );

    }
}
