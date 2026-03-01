package cz.bvv.errorhandlingdemo.policy.route.logging;

import cz.bvv.errorhandlingdemo.builder.common.IntegrationExchangeProperties;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class ProcessStatusTracker {

    public boolean updateIfChanged(Exchange exchange, ExchangeLoggingStatus status) {
        ExchangeLoggingStatus previous = exchange.getProperty(
          IntegrationExchangeProperties.PROCESS_STATUS,
          ExchangeLoggingStatus.class
        );
        if (previous == status) {
            return false;
        }
        exchange.setProperty(IntegrationExchangeProperties.PROCESS_STATUS, status);
        return true;
    }
}
