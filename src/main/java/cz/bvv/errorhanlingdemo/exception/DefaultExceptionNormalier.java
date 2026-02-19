package cz.bvv.errorhanlingdemo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class DefaultExceptionNormalier implements ExceptionNormalizer<Exception> {
    @Override
    public IntegrationException normalize(Exception exception) {
        return new IntegrationException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          "Unexpected exceptionL",
          exception.getMessage(),
          exception);
    }
}
