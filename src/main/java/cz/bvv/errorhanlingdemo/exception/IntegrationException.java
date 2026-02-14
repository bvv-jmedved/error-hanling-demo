package cz.bvv.errorhanlingdemo.exception;

import java.util.List;
import lombok.Getter;

@Getter
public class IntegrationException extends RuntimeException {
    private final int statusCode;
    private final List<IntegrationError> errors;

    public IntegrationException(
      int statusCode,
      String errorCode,
      String message,
      Exception cause) {
        super(message, cause);
        List<IntegrationError> integrationErrors = List.of(
          new IntegrationError(errorCode, message));
        this.statusCode = statusCode;
        this.errors = integrationErrors;
    }

    public IntegrationException(
      int statusCode,
      List<IntegrationError> errors,
      String message,
      Exception cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.errors = List.copyOf(errors);
    }
}
