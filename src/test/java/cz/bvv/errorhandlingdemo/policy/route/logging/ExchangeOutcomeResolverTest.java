package cz.bvv.errorhandlingdemo.policy.route.logging;

import static org.assertj.core.api.Assertions.assertThat;

import cz.bvv.errorhandlingdemo.support.exchange.IntegrationExchangeProperties;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.Test;


/**
 * ExchangeOutcomeResolverTest architecture test.
 *
 * <p>This class serves as executable documentation for the demo architecture by validating
 * route-policy orchestration and monitoring policy decisions. It helps readers understand how to implement and verify the same pattern in
 * their own Camel integration modules.
 */
class ExchangeOutcomeResolverTest {

    private final ExchangeOutcomeResolver resolver = new ExchangeOutcomeResolver();

    @Test
    void shouldResolveSuccessWhenExchangeIsClean() {
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());

        assertThat(resolver.isFailure(exchange)).isFalse();
    }

    @Test
    void shouldResolveFailureWhenFailureHandledFlagIsSet() {
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.setProperty(IntegrationExchangeProperties.FAILURE_HANDLED, true);

        assertThat(resolver.isFailure(exchange)).isTrue();
    }

    @Test
    void shouldResolveFailureWhenExceptionCaughtIsPresent() {
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.setProperty(Exchange.EXCEPTION_CAUGHT, new IllegalStateException("boom"));

        assertThat(resolver.isFailure(exchange)).isTrue();
    }
}
