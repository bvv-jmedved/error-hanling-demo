package cz.bvv.errorhanlingdemo.exception;

import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class DefaultHttpOperationFailedExceptionNormalizer
  implements ExceptionNormalizer<HttpOperationFailedException> {
    @Override
    public IntegrationException normalize(HttpOperationFailedException exception) {
        return new IntegrationException(
          HttpStatus.BAD_GATEWAY,
          exception.getHttpResponseStatus(),
          exception.getMessage(),
          exception);

    }
}
