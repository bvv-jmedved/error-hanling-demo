package cz.bvv.errorhandlingdemo.policy.route.logging;

import java.util.Objects;

/**
 * Logging policy component used by the architecture demo.
 *
 * <p>This type participates in canonical process-status monitoring and demonstrates
 * how route metadata is translated into stable operational logs.
 */
public record LoggingStatusConfig(
  ExchangeLoggingStatus onStart,
  ExchangeLoggingStatus onComplete,
  ExchangeLoggingStatus onFailure
) {
    public LoggingStatusConfig {
        Objects.requireNonNull(onStart, "onStart");
        Objects.requireNonNull(onComplete, "onComplete");
        Objects.requireNonNull(onFailure, "onFailure");
    }

    public static LoggingStatusConfig defaults() {
        return new LoggingStatusConfig(
          ExchangeLoggingStatus.RUNNING,
          ExchangeLoggingStatus.RUNNING,
          ExchangeLoggingStatus.FAILURE
        );
    }

    public static LoggingStatusConfig fromAnnotation(LoggingStatus loggingStatus) {
        if (loggingStatus == null) {
            return defaults();
        }
        return new LoggingStatusConfig(
          loggingStatus.onStart(),
          loggingStatus.onComplete(),
          loggingStatus.onFailure()
        );
    }
}
