package cz.bvv.errorhandlingdemo.contract.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.bvv.errorhandlingdemo.contract.failure.IntegrationExceptionMapper;
import cz.bvv.errorhandlingdemo.contract.rest.model.DefaultRestError;
import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class DefaultRestFailureContractRoutePolicy
  extends RestFailureContractRoutePolicy<DefaultRestError> {

    public DefaultRestFailureContractRoutePolicy(
      ObjectMapper objectMapper,
      IntegrationExceptionMapper integrationExceptionMapper) {
        super(objectMapper, integrationExceptionMapper);
    }

    @Override
    protected DefaultRestError createRestError(IntegrationException exception) {
        return new DefaultRestError(
          exception.getErrors().stream()
            .map(error -> new DefaultRestError.Error(error.code(), error.message()))
            .toList()
        );

    }
}
