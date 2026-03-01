package cz.bvv.errorhandlingdemo.policy.route.logging;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.Test;

/**
 * ProcessStatusTrackerTest architecture test.
 *
 * <p>Verifies route-policy orchestration and monitoring policy decisions and documents expected
 * architecture behavior.
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
