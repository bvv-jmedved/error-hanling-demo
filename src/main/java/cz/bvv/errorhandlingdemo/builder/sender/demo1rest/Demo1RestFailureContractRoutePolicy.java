package cz.bvv.errorhandlingdemo.builder.sender.demo1rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.bvv.errorhandlingdemo.builder.sender.demo1rest.model.Demo1RestError;
import cz.bvv.errorhandlingdemo.contract.failure.FailureContractMapper;
import cz.bvv.errorhandlingdemo.contract.failure.IntegrationExceptionMapper;
import cz.bvv.errorhandlingdemo.contract.rest.RestFailureContractRoutePolicy;
import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Demo1RestFailureContractRoutePolicy is part of the Camel error-handling architecture demo.
 *
 * <p>The type is documented to serve as an implementation guide for teams that want to apply the
 * same separation of route DSL, policy composition, and failure contracts.
 */
@Component
@Scope("prototype")
public class Demo1RestFailureContractRoutePolicy
    extends RestFailureContractRoutePolicy<Demo1RestError> {

  private final FailureContractMapper<Demo1RestError> failureContractMapper;

  public Demo1RestFailureContractRoutePolicy(
      Demo1RestFailureContractMapper failureContractMapper,
      ObjectMapper objectMapper,
      IntegrationExceptionMapper integrationExceptionMapper) {
    super(objectMapper, integrationExceptionMapper);
    this.failureContractMapper = failureContractMapper;
  }

  @Override
  protected Demo1RestError createRestError(IntegrationException exception) {
    return failureContractMapper.map(exception);
  }
}
