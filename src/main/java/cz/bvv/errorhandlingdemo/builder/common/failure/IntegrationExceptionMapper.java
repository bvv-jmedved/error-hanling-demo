package cz.bvv.errorhandlingdemo.builder.common.failure;

import cz.bvv.errorhandlingdemo.exception.IntegrationException;

public interface IntegrationExceptionMapper {
    IntegrationException map(Exception exception);
}
