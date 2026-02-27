package cz.bvv.errorhandlingdemo.builder.sender.demo1rest;

import cz.bvv.errorhandlingdemo.builder.common.failure.FailureContractMapper;
import cz.bvv.errorhandlingdemo.builder.sender.demo1rest.model.Demo1RestError;
import cz.bvv.errorhandlingdemo.exception.IntegrationError;
import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import org.springframework.stereotype.Component;

@Component
public class Demo1RestFailureContractMapper implements
  FailureContractMapper<Demo1RestError> {
    @Override
    public Demo1RestError map(IntegrationException integrationException) {
        return new Demo1RestError(
          integrationException.getErrors().stream()
            .map(this::map)
            .toList()
        );
    }

    private Demo1RestError.Error map (IntegrationError integrationError) {
        return new Demo1RestError.Error(
          integrationError.code(),
          integrationError.message()
        );
    }
}
