package cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging;

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
    private final RecoveryActionResolver recoveryActionResolver;

    public LoggingRoutePolicy(
      String service,
      boolean critical,
      LoggingStatusConfig loggingStatus,
      ExchangeOutcomeResolver exchangeOutcomeResolver,
      CorrelationIdResolver correlationIdResolver,
      ProcessStatusTracker processStatusTracker,
      RecoveryActionResolver recoveryActionResolver) {
        this.service = service;
        this.critical = critical;
        this.loggingStatus = loggingStatus;
        this.exchangeOutcomeResolver = exchangeOutcomeResolver;
        this.correlationIdResolver = correlationIdResolver;
        this.processStatusTracker = processStatusTracker;
        this.recoveryActionResolver = recoveryActionResolver;
    }

    @Override
    public void onExchangeBegin(Route route, Exchange exchange) {
        logProcessStatusIfChanged(route, exchange, loggingStatus.onStart(), "BEGIN");
    }

    @Override
    public void onExchangeDone(Route route, Exchange exchange) {
        ExchangeLoggingStatus status =
          exchangeOutcomeResolver.isFailure(exchange) ? loggingStatus.onFailure() : loggingStatus.onComplete();
        logProcessStatusIfChanged(route, exchange, status, "DONE");
    }

    private void logProcessStatusIfChanged(
      Route route,
      Exchange exchange,
      ExchangeLoggingStatus status,
      String phase) {
        if (!processStatusTracker.updateIfChanged(exchange, status)) {
            return;
        }

        String correlationId = correlationIdResolver.ensureCorrelationId(exchange);
        RecoveryAction recoveryAction = recoveryActionResolver.currentAction(exchange);
        boolean terminal = isTerminal(status);
        LOG.info(
          "event=process_status phase={} final={} status={} recoveryAction={} routeId={} service={} critical={} exchangeId={} correlationId={}",
          phase,
          terminal,
          status,
          recoveryAction,
          route.getRouteId(),
          service,
          critical,
          exchange.getExchangeId(),
          correlationId
        );
    }

    private static boolean isTerminal(ExchangeLoggingStatus status) {
        return status == ExchangeLoggingStatus.COMPLETED
          || status == ExchangeLoggingStatus.FAILED
          || status == ExchangeLoggingStatus.ENQUEUED;
    }
}
