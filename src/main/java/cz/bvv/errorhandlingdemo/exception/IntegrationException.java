package cz.bvv.errorhandlingdemo.exception;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@Builder
public class IntegrationException extends RuntimeException {
    private final HttpStatus status;
    private final List<IntegrationError> errors;

    public IntegrationException(
      HttpStatus status,
      String errorCode,
      String message,
      Exception cause) {
        super(message, cause);
        List<IntegrationError> integrationErrors = List.of(
          new IntegrationError(errorCode, message));
        this.status = status;
        this.errors = integrationErrors;
    }

    public IntegrationException(
      HttpStatus status,
      List<IntegrationError> errors,
      String message,
      Exception cause) {
        super(message, cause);
        this.status = status;
        this.errors = List.copyOf(errors);
    }

    public static IntegrationException unknownError() {
        return new IntegrationException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          HttpStatus.INTERNAL_SERVER_ERROR.name(),
          "Unknown error",
          null
        );
    }
}
