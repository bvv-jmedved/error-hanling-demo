package cz.bvv.errorhandlingdemo.builder.common.failure;

import cz.bvv.errorhandlingdemo.exception.IntegrationException;

public interface FailureContractMapper<T> {
    T map(IntegrationException integrationException);
}
