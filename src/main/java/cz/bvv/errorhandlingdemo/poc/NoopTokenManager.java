package cz.bvv.errorhandlingdemo.poc;

import cz.bvv.errorhandlingdemo.builder.common.TokenManager;
import org.apache.camel.Exchange;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!poc")
public class NoopTokenManager implements TokenManager {

    @Override
    public void refreshToken(Exchange exchange) {
        // no-op outside PoC profile
    }
}
