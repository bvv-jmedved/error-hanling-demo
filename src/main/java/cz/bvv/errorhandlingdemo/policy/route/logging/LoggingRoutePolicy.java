package cz.bvv.errorhandlingdemo.policy.route.logging;

import cz.bvv.errorhandlingdemo.support.exchange.IntegrationExchangeHeaders;
import cz.bvv.errorhandlingdemo.support.exchange.IntegrationExchangeProperties;
import lombok.Getter;
import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.support.RoutePolicySupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

@Getter
public class LoggingRoutePolicy extends RoutePolicySupport {
    private static final Logger LOG = LoggerFactory.getLogger(LoggingRoutePolicy.class);
    private static final String UNKNOWN = "unknown";

    private final String service;
    private final boolean critical;
    private final FlowDirectionConfig flowDirection;
    private final LoggingStatusConfig loggingStatus;
    private final ExchangeOutcomeResolver exchangeOutcomeResolver;
    private final CorrelationIdResolver correlationIdResolver;
    private final ProcessStatusTracker processStatusTracker;

    public LoggingRoutePolicy(
      String service,
      boolean critical,
      FlowDirectionConfig flowDirection,
      LoggingStatusConfig loggingStatus,
      ExchangeOutcomeResolver exchangeOutcomeResolver,
      CorrelationIdResolver correlationIdResolver,
      ProcessStatusTracker processStatusTracker) {
        this.service = service;
        this.critical = critical;
        this.flowDirection = flowDirection;
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
        applyFlowDirection(exchange);

        if (!processStatusTracker.updateIfChanged(exchange, status)) {
            return;
        }

        String correlationId = correlationIdResolver.ensureCorrelationId(exchange);
        String source = resolveFlowValue(exchange, IntegrationExchangeHeaders.FLOW_SOURCE, IntegrationExchangeProperties.FLOW_SOURCE);
        String target = resolveFlowValue(exchange, IntegrationExchangeHeaders.FLOW_TARGET, IntegrationExchangeProperties.FLOW_TARGET);
        String process = resolveFlowValue(exchange, IntegrationExchangeHeaders.FLOW_PROCESS, IntegrationExchangeProperties.FLOW_PROCESS);
        LOG.info(
          "event=process_status status={} routeId={} source={} target={} process={} correlationId={}",
          status,
          route.getRouteId(),
          source,
          target,
          process,
          correlationId
        );
    }

    private void applyFlowDirection(Exchange exchange) {
        setFlowValueIfConfigured(exchange, IntegrationExchangeHeaders.FLOW_SOURCE, IntegrationExchangeProperties.FLOW_SOURCE, flowDirection.source());
        setFlowValueIfConfigured(exchange, IntegrationExchangeHeaders.FLOW_TARGET, IntegrationExchangeProperties.FLOW_TARGET, flowDirection.target());
        setFlowValueIfConfigured(exchange, IntegrationExchangeHeaders.FLOW_PROCESS, IntegrationExchangeProperties.FLOW_PROCESS, flowDirection.process());
    }

    private static void setFlowValueIfConfigured(Exchange exchange, String header, String property, String value) {
        if (!StringUtils.hasText(value)) {
            return;
        }
        exchange.getIn().setHeader(header, value);
        exchange.setProperty(property, value);
    }

    private static String resolveFlowValue(Exchange exchange, String header, String property) {
        String value = firstNonBlank(
          exchange.getIn().getHeader(header, String.class),
          exchange.getProperty(property, String.class)
        );
        return StringUtils.hasText(value) ? value : UNKNOWN;
    }

    private static String firstNonBlank(String first, String second) {
        if (StringUtils.hasText(first)) {
            return first;
        }
        if (StringUtils.hasText(second)) {
            return second;
        }
        return null;
    }
}
