package cz.bvv.errorhandlingdemo.builder.sender.demo1rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.bvv.errorhandlingdemo.builder.common.failure.FailureContractMapper;
import cz.bvv.errorhandlingdemo.builder.common.failure.IntegrationExceptionMapper;
import cz.bvv.errorhandlingdemo.builder.common.rest.RestFailureContractRoutePolicy;
import cz.bvv.errorhandlingdemo.builder.sender.demo1rest.model.Demo1RestError;
import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
