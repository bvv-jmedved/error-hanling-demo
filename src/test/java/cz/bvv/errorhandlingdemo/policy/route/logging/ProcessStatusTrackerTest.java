package cz.bvv.errorhandlingdemo.policy.route.logging;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.Test;


/**
 * ProcessStatusTrackerTest architecture test.
 *
 * <p>This class serves as executable documentation for the demo architecture by validating
 * route-policy orchestration and monitoring policy decisions. It helps readers understand how to implement and verify the same pattern in
 * their own Camel integration modules.
 */
class ProcessStatusTrackerTest {

    private final ProcessStatusTracker tracker = new ProcessStatusTracker();

    @Test
    void shouldReturnTrueWhenStatusChanges() {
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());

        assertThat(tracker.updateIfChanged(exchange, ExchangeLoggingStatus.STARTED)).isTrue();
        assertThat(tracker.updateIfChanged(exchange, ExchangeLoggingStatus.RUNNING)).isTrue();
    }

    @Test
    void shouldReturnFalseWhenStatusStaysSame() {
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        tracker.updateIfChanged(exchange, ExchangeLoggingStatus.RUNNING);

        assertThat(tracker.updateIfChanged(exchange, ExchangeLoggingStatus.RUNNING)).isFalse();
    }
}
