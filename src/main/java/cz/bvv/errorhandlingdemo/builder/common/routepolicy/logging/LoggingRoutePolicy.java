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

    public LoggingRoutePolicy(
      String service,
      boolean critical,
      LoggingStatusConfig loggingStatus,
      ExchangeOutcomeResolver exchangeOutcomeResolver,
      CorrelationIdResolver correlationIdResolver) {
        this.service = service;
        this.critical = critical;
        this.loggingStatus = loggingStatus;
        this.exchangeOutcomeResolver = exchangeOutcomeResolver;
        this.correlationIdResolver = correlationIdResolver;
    }

    @Override
    public void onExchangeBegin(Route route, Exchange exchange) {
        String correlationId = correlationIdResolver.ensureCorrelationId(exchange);
        LOG.info(
          "event=exchange_status phase=BEGIN status={} routeId={} service={} critical={} exchangeId={} correlationId={}",
          loggingStatus.onStart(),
          route.getRouteId(),
          service,
          critical,
          exchange.getExchangeId(),
          correlationId
        );
    }

    @Override
    public void onExchangeDone(Route route, Exchange exchange) {
        ExchangeLoggingStatus status =
          exchangeOutcomeResolver.isFailure(exchange) ? loggingStatus.onFailure() : loggingStatus.onComplete();
        String correlationId = correlationIdResolver.ensureCorrelationId(exchange);
        LOG.info(
          "event=exchange_status phase=DONE status={} routeId={} service={} critical={} exchangeId={} correlationId={}",
          status,
          route.getRouteId(),
          service,
          critical,
          exchange.getExchangeId(),
          correlationId
        );
    }
}
