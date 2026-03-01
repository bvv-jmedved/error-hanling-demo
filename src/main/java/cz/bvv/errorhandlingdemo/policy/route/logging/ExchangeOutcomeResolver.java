package cz.bvv.errorhandlingdemo.policy.route.logging;

import cz.bvv.errorhandlingdemo.support.exchange.IntegrationExchangeProperties;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class ExchangeOutcomeResolver {

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
        if (Boolean.TRUE.equals(exchange.getProperty(IntegrationExchangeProperties.FAILURE_HANDLED, Boolean.class))) {
            return true;
        }
        return Boolean.TRUE.equals(
          exchange.getProperty(IntegrationExchangeProperties.FAILURE_CONTRACT_APPLIED, Boolean.class)
        );
    }
}
