package cz.bvv.errorhandlingdemo.builder.receiver;

import cz.bvv.errorhandlingdemo.builder.common.ExchangePropertyKeys;
import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import cz.bvv.errorhandlingdemo.poc.FakeTokenManager;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePropertyKey;
import org.apache.camel.Processor;
import org.apache.camel.http.base.HttpOperationFailedException;

public class TokenRefreshOnUnauthorizedProcessor implements Processor {
    static final String TOKEN_REFRESH_FAILED = "token.refresh.failed";
    private final FakeTokenManager tokenManager;

    public TokenRefreshOnUnauthorizedProcessor(FakeTokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public void process(Exchange exchange) {
        Exception caught = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);

        if (!(caught instanceof HttpOperationFailedException httpEx)) {
            return;
        }
        if (httpEx.getStatusCode() != 401) {
            return;
        }

        try {
            tokenManager.refreshToken();
        } catch (IntegrationException ie) {
            Integer redeliveryCounter = exchange.getProperty(Exchange.REDELIVERY_COUNTER, Integer.class);
            exchange.setProperty(ExchangePropertyKey.UNIT_OF_WORK_EXHAUSTED, true);
            exchange.setProperty(Exchange.REDELIVERY_MAX_COUNTER, redeliveryCounter == null ? 1 : redeliveryCounter);
            exchange.setProperty(TOKEN_REFRESH_FAILED, true);
            exchange.setProperty(ExchangePropertyKeys.INTEGRATION_EXCEPTION_OVERRIDE, ie);
        }
    }
}
