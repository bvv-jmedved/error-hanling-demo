package cz.bvv.errorhanlingdemo.builder.common.failure;

import cz.bvv.errorhanlingdemo.exception.IntegrationException;

public interface IntegrationExceptionMapper {
    IntegrationException map(Exception exception);
}
