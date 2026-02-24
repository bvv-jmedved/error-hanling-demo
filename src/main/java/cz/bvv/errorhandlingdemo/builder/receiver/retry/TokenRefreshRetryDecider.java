package cz.bvv.errorhandlingdemo.builder.receiver.retry;

import cz.bvv.errorhandlingdemo.builder.common.IntegrationExchangeProperties;
import cz.bvv.errorhandlingdemo.builder.common.TokenManager;
import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class TokenRefreshRetryDecider {

    private final TokenManager tokenManager;

    public TokenRefreshRetryDecider(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @SuppressWarnings("unused")
    public boolean shouldRetry(Exchange exchange) {
        Integer counter = exchange.getMessage()
          .getHeader(Exchange.REDELIVERY_COUNTER, Integer.class);

        if (counter == null || counter != 1) {
            return false;
        }

        try {
            tokenManager.refreshToken(exchange);
            return true;
        } catch (IntegrationException ie) {
            exchange.setProperty(IntegrationExchangeProperties.EXCEPTION_OVERRIDE, ie);
            return false;
        }
    }
}
