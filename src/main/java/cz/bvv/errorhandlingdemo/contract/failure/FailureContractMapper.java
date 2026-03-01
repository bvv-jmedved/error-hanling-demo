package cz.bvv.errorhandlingdemo.contract.failure;

import cz.bvv.errorhandlingdemo.exception.IntegrationException;

public interface FailureContractMapper<T> {
    T map(IntegrationException integrationException);
}
