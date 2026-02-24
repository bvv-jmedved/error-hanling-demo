package cz.bvv.errorhandlingdemo.exception;

import java.util.List;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class IntegrationException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Integration error";

    private final HttpStatus status;
    private final List<IntegrationError> errors;

    public IntegrationException(
      HttpStatus status,
      String errorCode,
      String message,
      Exception cause) {
        super(normalizeMessage(message), cause);
        this.status = requireStatus(status);
        this.errors = List.of(
          new IntegrationError(errorCode, normalizeMessage(message)));
    }

    public IntegrationException(
      HttpStatus status,
      List<IntegrationError> errors,
      String message,
      Exception cause) {
        super(normalizeMessage(message), cause);
        this.status = requireStatus(status);
        this.errors = requireErrors(errors);
    }

    public static IntegrationException unknownError() {
        return new IntegrationException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          HttpStatus.INTERNAL_SERVER_ERROR.name(),
          "Unknown error",
          null
        );
    }

    private static HttpStatus requireStatus(HttpStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        return status;
    }

    private static List<IntegrationError> requireErrors(List<IntegrationError> errors) {
        if (errors == null || errors.isEmpty()) {
            throw new IllegalArgumentException("errors must not be null or empty");
        }
        return List.copyOf(errors);
    }

    private static String normalizeMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return DEFAULT_MESSAGE;
        }
        return message;
    }
}
