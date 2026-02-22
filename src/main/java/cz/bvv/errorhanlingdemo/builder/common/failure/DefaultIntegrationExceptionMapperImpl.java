package cz.bvv.errorhanlingdemo.builder.common.failure;

import cz.bvv.errorhanlingdemo.exception.IntegrationException;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class DefaultIntegrationExceptionMapperImpl implements IntegrationExceptionMapper {
    @Override
    public IntegrationException map(Exception exception) {
        return switch (exception) {
            case null -> IntegrationException.unknownError();
            case IntegrationException integrationException -> integrationException;
            case HttpOperationFailedException operationException -> new IntegrationException(
              HttpStatus.BAD_GATEWAY,
              HttpStatus.BAD_GATEWAY.name(),
              operationException.getStatusText(),
              operationException);
            default -> new IntegrationException(
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatus.INTERNAL_SERVER_ERROR.name(),
              exception.getMessage(),
              exception
            );
        };

    }
}
