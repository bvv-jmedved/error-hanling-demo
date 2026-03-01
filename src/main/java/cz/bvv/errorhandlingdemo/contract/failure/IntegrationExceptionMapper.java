package cz.bvv.errorhandlingdemo.contract.failure;

import cz.bvv.errorhandlingdemo.exception.IntegrationException;

public interface IntegrationExceptionMapper {
    IntegrationException map(Exception exception);
}
