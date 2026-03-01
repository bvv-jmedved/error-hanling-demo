package cz.bvv.errorhandlingdemo.poc;

import cz.bvv.errorhandlingdemo.support.auth.TokenManager;
import org.apache.camel.Exchange;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * PoC-only component used to simulate failures and retry scenarios for the architecture demo.
 *
 * <p>Types in this package are intentionally non-architectural and exist only
 * to make behavior observable in tests and local runs.
 */
@Component
@Profile("!poc")
public class NoopTokenManager implements TokenManager {

    @Override
    public void refreshToken(Exchange exchange) {
        // Intentionally empty for non-PoC profiles.
    }
}
