package cz.bvv.errorhandlingdemo.policy.route.logging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import cz.bvv.errorhandlingdemo.support.exchange.IntegrationExchangeHeaders;
import cz.bvv.errorhandlingdemo.support.exchange.IntegrationExchangeProperties;
import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.Test;


/**
 * LoggingRoutePolicyFlowDirectionTest architecture test.
 *
 * <p>This class serves as executable documentation for the demo architecture by validating
 * route-policy orchestration and monitoring policy decisions. It helps readers understand how to implement and verify the same pattern in
 * their own Camel integration modules.
 */
class LoggingRoutePolicyFlowDirectionTest {

    @Test
    void shouldSetFlowDirectionHeadersAndPropertiesFromConfiguredAnnotationValues() {
        LoggingRoutePolicy policy = new LoggingRoutePolicy(
          "service",
          false,
          new FlowDirectionConfig("source-a", "target-b", "create-order"),
          LoggingStatusConfig.defaults(),
          new ExchangeOutcomeResolver(),
          new CorrelationIdResolver(),
          new ProcessStatusTracker()
        );
        Route route = mock(Route.class);
        when(route.getRouteId()).thenReturn("demo-route");
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());

        policy.onExchangeBegin(route, exchange);

        assertThat(exchange.getIn().getHeader(IntegrationExchangeHeaders.FLOW_SOURCE, String.class)).isEqualTo("source-a");
        assertThat(exchange.getIn().getHeader(IntegrationExchangeHeaders.FLOW_TARGET, String.class)).isEqualTo("target-b");
        assertThat(exchange.getIn().getHeader(IntegrationExchangeHeaders.FLOW_PROCESS, String.class)).isEqualTo("create-order");
        assertThat(exchange.getProperty(IntegrationExchangeProperties.FLOW_SOURCE, String.class)).isEqualTo("source-a");
        assertThat(exchange.getProperty(IntegrationExchangeProperties.FLOW_TARGET, String.class)).isEqualTo("target-b");
        assertThat(exchange.getProperty(IntegrationExchangeProperties.FLOW_PROCESS, String.class)).isEqualTo("create-order");
    }
}
