package cz.bvv.errorhandlingdemo.policy.route.logging;

import lombok.Getter;
import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.support.RoutePolicySupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class LoggingRoutePolicy extends RoutePolicySupport {
    private static final Logger LOG = LoggerFactory.getLogger(LoggingRoutePolicy.class);

    private final String service;
    private final boolean critical;
    private final LoggingStatusConfig loggingStatus;
    private final ExchangeOutcomeResolver exchangeOutcomeResolver;
    private final CorrelationIdResolver correlationIdResolver;
    private final ProcessStatusTracker processStatusTracker;

    public LoggingRoutePolicy(
      String service,
      boolean critical,
      LoggingStatusConfig loggingStatus,
      ExchangeOutcomeResolver exchangeOutcomeResolver,
      CorrelationIdResolver correlationIdResolver,
      ProcessStatusTracker processStatusTracker) {
        this.service = service;
        this.critical = critical;
        this.loggingStatus = loggingStatus;
        this.exchangeOutcomeResolver = exchangeOutcomeResolver;
        this.correlationIdResolver = correlationIdResolver;
        this.processStatusTracker = processStatusTracker;
    }

    @Override
    public void onExchangeBegin(Route route, Exchange exchange) {
        logProcessStatusIfChanged(route, exchange, loggingStatus.onStart());
    }

    @Override
    public void onExchangeDone(Route route, Exchange exchange) {
        ExchangeLoggingStatus status =
          exchangeOutcomeResolver.isFailure(exchange) ? loggingStatus.onFailure() : loggingStatus.onComplete();
        logProcessStatusIfChanged(route, exchange, status);
    }

    private void logProcessStatusIfChanged(Route route, Exchange exchange, ExchangeLoggingStatus status) {
        if (!processStatusTracker.updateIfChanged(exchange, status)) {
            return;
        }

        String correlationId = correlationIdResolver.ensureCorrelationId(exchange);
        LOG.info(
          "event=process_status status={} routeId={} correlationId={}",
          status,
          route.getRouteId(),
          correlationId
        );
    }
}
