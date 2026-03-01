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
public class ExchangeOutcomeResolver {

  /**
   * Determines whether exchange should be treated as failed from monitoring perspective.
   *
   * <p>Checks both Camel failure flags and architecture-specific properties set by fail-contract
   * mapping.
   *
   * @param exchange Camel exchange being completed
   * @return {@code true} when exchange represents failed outcome
   */
  public boolean isFailure(Exchange exchange) {
    if (exchange.isFailed()) {
      return true;
    }
    if (exchange.getException() != null) {
      return true;
    }
    if (exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class) != null) {
      return true;
    }
    if (exchange.getProperty(IntegrationExchangeProperties.EXCEPTION_OVERRIDE) != null) {
      return true;
    }
    if (Boolean.TRUE.equals(
        exchange.getProperty(IntegrationExchangeProperties.FAILURE_HANDLED, Boolean.class))) {
      return true;
    }
    return Boolean.TRUE.equals(
        exchange.getProperty(
            IntegrationExchangeProperties.FAILURE_CONTRACT_APPLIED, Boolean.class));
  }
}
