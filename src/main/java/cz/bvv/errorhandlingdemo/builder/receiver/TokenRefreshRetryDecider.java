package cz.bvv.errorhandlingdemo.builder.receiver;

import org.apache.camel.Exchange;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.stereotype.Component;

@Component
public class TokenRefreshRetryDecider {

    public boolean shouldRetry(Exchange exchange) {
        Exception caught = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
        if (!(caught instanceof HttpOperationFailedException httpEx) || httpEx.getStatusCode() != 401) {
            return true;
        }

        return !Boolean.TRUE.equals(
          exchange.getProperty(TokenRefreshOnUnauthorizedProcessor.TOKEN_REFRESH_FAILED, Boolean.class));
    }
}
