package cz.bvv.errorhanlingdemo.builder.common;

import cz.bvv.errorhanlingdemo.exception.IntegrationException;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class DefaultIntegrationExceptionMapperImpl implements IntegrationExceptionMapper {
    @Override
    public IntegrationException map(Exception exception) {
        if (exception == null) {
            return new IntegrationException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                "Unknown error",
                null
            );
        }

        if (exception instanceof IntegrationException integrationException) {
            return integrationException;
        }

        if (exception instanceof HttpOperationFailedException operationException) {
            return new IntegrationException(
              HttpStatus.BAD_GATEWAY,
              HttpStatus.BAD_GATEWAY.name(),
              operationException.getStatusText(),
              operationException);
        }

        return new IntegrationException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          HttpStatus.INTERNAL_SERVER_ERROR.name(),
          exception.getMessage(),
          exception
        );
    }
}
