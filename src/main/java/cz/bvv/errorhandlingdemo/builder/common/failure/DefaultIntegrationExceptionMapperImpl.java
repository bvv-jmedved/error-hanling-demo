package cz.bvv.errorhandlingdemo.builder.common.failure;

import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class DefaultIntegrationExceptionMapperImpl implements IntegrationExceptionMapper {
    private static final String DOWNSTREAM_HTTP_ERROR_PREFIX = "DOWNSTREAM_HTTP_";

    @Override
    public IntegrationException map(Exception exception) {
        return switch (exception) {
            case null -> IntegrationException.unknownError();
            case IntegrationException integrationException -> integrationException;
            case HttpOperationFailedException operationException -> {
                int downstreamStatus = operationException.getStatusCode();
                String errorCode = DOWNSTREAM_HTTP_ERROR_PREFIX + downstreamStatus;
                String message = extractMessage(operationException);
                yield new IntegrationException(
                  HttpStatus.BAD_GATEWAY,
                  errorCode,
                  message,
                  operationException);
            }
            default -> new IntegrationException(
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatus.INTERNAL_SERVER_ERROR.name(),
              exception.getMessage(),
              exception
            );
        };

    }

    private static String extractMessage(HttpOperationFailedException operationException) {
        if (StringUtils.hasText(operationException.getStatusText())) {
            return operationException.getStatusText();
        }

        String fallback = sanitize(operationException.getMessage());
        if (StringUtils.hasText(fallback)) {
            return fallback;
        }

        return "Downstream HTTP call failed with status " + operationException.getStatusCode();
    }

    private static String sanitize(String message) {
        if (!StringUtils.hasText(message)) {
            return message;
        }

        String sanitized = message.replaceAll("(?i)\\s+with body:.*$", "").trim();
        return sanitized.replaceAll("[\\r\\n\\t]+", " ");
    }
}
