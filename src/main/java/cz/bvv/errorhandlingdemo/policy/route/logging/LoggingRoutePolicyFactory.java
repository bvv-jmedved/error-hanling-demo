package cz.bvv.errorhandlingdemo.policy.route.logging;

import cz.bvv.errorhandlingdemo.foundation.annotation.FlowDirection;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Logging policy component used by the architecture demo.
 *
 * <p>This type participates in canonical process-status monitoring and demonstrates how route
 * metadata is translated into stable operational logs.
 */
@Component
public class LoggingRoutePolicyFactory {
  private final ExchangeOutcomeResolver exchangeOutcomeResolver;
  private final CorrelationIdResolver correlationIdResolver;
  private final ProcessStatusTracker processStatusTracker;

  public LoggingRoutePolicyFactory(
      ExchangeOutcomeResolver exchangeOutcomeResolver,
      CorrelationIdResolver correlationIdResolver,
      ProcessStatusTracker processStatusTracker) {
    this.exchangeOutcomeResolver = exchangeOutcomeResolver;
    this.correlationIdResolver = correlationIdResolver;
    this.processStatusTracker = processStatusTracker;
  }

  /**
   * Creates route logging policy using annotations declared on the given builder type.
   *
   * <p>Factory composes:
   *
   * <ul>
   *   <li>{@link Monitored} metadata (service and criticality),
   *   <li>{@link LoggingStatus} lifecycle overrides,
   *   <li>{@link FlowDirection} business flow metadata.
   * </ul>
   *
   * @param builder route builder for which logging policy is created
   * @return configured logging route policy
   */
  public LoggingRoutePolicy create(RouteBuilder builder) {
    String defaultService = builder.getClass().getSimpleName();
    Monitored monitored = builder.getClass().getAnnotation(Monitored.class);
    LoggingStatus loggingStatus = builder.getClass().getAnnotation(LoggingStatus.class);
    FlowDirection flowDirection = builder.getClass().getAnnotation(FlowDirection.class);
    LoggingStatusConfig statusConfig = LoggingStatusConfig.fromAnnotation(loggingStatus);
    FlowDirectionConfig flowDirectionConfig = FlowDirectionConfig.fromAnnotation(flowDirection);

    if (monitored == null) {
      return new LoggingRoutePolicy(
          defaultService,
          false,
          flowDirectionConfig,
          statusConfig,
          exchangeOutcomeResolver,
          correlationIdResolver,
          processStatusTracker);
    }

    String service =
        StringUtils.hasText(monitored.service()) ? monitored.service() : defaultService;
    return new LoggingRoutePolicy(
        service,
        monitored.critical(),
        flowDirectionConfig,
        statusConfig,
        exchangeOutcomeResolver,
        correlationIdResolver,
        processStatusTracker);
  }
}
