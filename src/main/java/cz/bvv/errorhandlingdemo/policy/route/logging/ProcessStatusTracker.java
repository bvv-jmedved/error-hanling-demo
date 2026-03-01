package cz.bvv.errorhandlingdemo.policy.route.logging;

import cz.bvv.errorhandlingdemo.support.exchange.IntegrationExchangeProperties;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

/**
 * Logging policy component used by the architecture demo.
 *
 * <p>This type participates in canonical process-status monitoring and demonstrates how route
 * metadata is translated into stable operational logs.
 */
@Component
public class ProcessStatusTracker {

  /**
   * Updates canonical process status only when value changed from the previous one.
   *
   * @param exchange Camel exchange storing canonical process status
   * @param status candidate status to store
   * @return {@code true} when status changed and was updated
   */
  public boolean updateIfChanged(Exchange exchange, ExchangeLoggingStatus status) {
    ExchangeLoggingStatus previous =
        exchange.getProperty(
            IntegrationExchangeProperties.PROCESS_STATUS, ExchangeLoggingStatus.class);
    if (previous == status) {
      return false;
    }
    exchange.setProperty(IntegrationExchangeProperties.PROCESS_STATUS, status);
    return true;
  }
}
