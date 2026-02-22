package cz.bvv.errorhandlingdemo.poc;

import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import org.apache.camel.Exchange;
import org.springframework.http.HttpStatus;

/**
 * PoC-only token manager used to simulate token refresh failure flow.
 */
public class FakeTokenManager {

    public void refreshToken(Exchange exchange) {
        String refreshShouldFail = exchange.getIn().getHeader("X_TOKEN_REFRESH_FAIL", String.class);
        if (!"true".equalsIgnoreCase(refreshShouldFail)) {
            return;
        }

        throw new IntegrationException(
          HttpStatus.BAD_GATEWAY,
          "TOKEN_REFRESH_FAILED",
          "Token refresh failed",
          null
        );
    }
}
