package cz.bvv.errorhandlingdemo.policy.route.logging;

import java.util.Objects;

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
