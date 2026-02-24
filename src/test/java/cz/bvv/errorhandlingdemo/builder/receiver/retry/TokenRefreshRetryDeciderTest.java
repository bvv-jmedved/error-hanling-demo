package cz.bvv.errorhandlingdemo.builder.receiver.retry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cz.bvv.errorhandlingdemo.builder.common.TokenManager;
import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class TokenRefreshRetryDeciderTest {

    @Test
    void shouldRetryOnlyOnFirstRedeliveryWhenTokenRefreshSucceeds() {
        TokenManager tokenManager = exchange -> { };
        TokenRefreshRetryDecider decider = new TokenRefreshRetryDecider(tokenManager);
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getMessage().setHeader(Exchange.REDELIVERY_COUNTER, 1);

        boolean shouldRetry = decider.shouldRetry(exchange);

        assertThat(shouldRetry).isTrue();
    }

    @Test
    void shouldPropagateIntegrationExceptionWhenTokenRefreshFails() {
        IntegrationException tokenRefreshFailure = new IntegrationException(
          HttpStatus.BAD_GATEWAY,
          "TOKEN_REFRESH_FAILED",
          "Token refresh failed",
          null
        );
        TokenManager tokenManager = exchange -> {
            throw tokenRefreshFailure;
        };
        TokenRefreshRetryDecider decider = new TokenRefreshRetryDecider(tokenManager);
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getMessage().setHeader(Exchange.REDELIVERY_COUNTER, 1);

        assertThatThrownBy(() -> decider.shouldRetry(exchange))
          .isSameAs(tokenRefreshFailure);
    }

    @Test
    void shouldNotRetryWhenNotFirstRedelivery() {
        TokenManager tokenManager = exchange -> {
            throw new AssertionError("refreshToken must not be called");
        };
        TokenRefreshRetryDecider decider = new TokenRefreshRetryDecider(tokenManager);
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getMessage().setHeader(Exchange.REDELIVERY_COUNTER, 2);

        boolean shouldRetry = decider.shouldRetry(exchange);

        assertThat(shouldRetry).isFalse();
    }
}
