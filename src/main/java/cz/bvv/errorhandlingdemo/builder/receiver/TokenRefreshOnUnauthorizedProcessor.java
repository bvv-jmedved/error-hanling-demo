package cz.bvv.errorhandlingdemo.builder.receiver;

import cz.bvv.errorhandlingdemo.builder.common.failure.FailureContractRoutePolicy;
import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import cz.bvv.errorhandlingdemo.poc.FakeTokenManager;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.http.base.HttpOperationFailedException;

public class TokenRefreshOnUnauthorizedProcessor implements Processor {
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
            exchange.setProperty(Exchange.EXCEPTION_CAUGHT , ie);
        }
    }
}
