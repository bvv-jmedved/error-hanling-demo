package cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.Test;

class RecoveryActionResolverTest {

    private final RecoveryActionResolver resolver = new RecoveryActionResolver();

    @Test
    void shouldReturnNoneWhenActionNotSet() {
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());

        assertThat(resolver.currentAction(exchange)).isEqualTo(RecoveryAction.NONE);
    }

    @Test
    void shouldReturnMarkedAction() {
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        resolver.markAction(exchange, RecoveryAction.TOKEN_REFRESH_START);

        assertThat(resolver.currentAction(exchange)).isEqualTo(RecoveryAction.TOKEN_REFRESH_START);
    }
}
