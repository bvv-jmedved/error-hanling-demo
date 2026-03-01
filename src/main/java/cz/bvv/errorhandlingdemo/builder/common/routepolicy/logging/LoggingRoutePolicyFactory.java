package cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class LoggingRoutePolicyFactory {
    private final ExchangeOutcomeResolver exchangeOutcomeResolver;
    private final CorrelationIdResolver correlationIdResolver;

    public LoggingRoutePolicyFactory(
      ExchangeOutcomeResolver exchangeOutcomeResolver,
      CorrelationIdResolver correlationIdResolver) {
        this.exchangeOutcomeResolver = exchangeOutcomeResolver;
        this.correlationIdResolver = correlationIdResolver;
    }

    public LoggingRoutePolicy create(RouteBuilder builder) {
        String defaultService = builder.getClass().getSimpleName();
        Monitored monitored = builder.getClass().getAnnotation(Monitored.class);
        LoggingStatus loggingStatus = builder.getClass().getAnnotation(LoggingStatus.class);
        LoggingStatusConfig statusConfig = LoggingStatusConfig.fromAnnotation(loggingStatus);

        if (monitored == null) {
            return new LoggingRoutePolicy(
              defaultService,
              false,
              statusConfig,
              exchangeOutcomeResolver,
              correlationIdResolver
            );
        }

        String service = StringUtils.hasText(monitored.service()) ? monitored.service() : defaultService;
        return new LoggingRoutePolicy(
          service,
          monitored.critical(),
          statusConfig,
          exchangeOutcomeResolver,
          correlationIdResolver
        );
    }
}
