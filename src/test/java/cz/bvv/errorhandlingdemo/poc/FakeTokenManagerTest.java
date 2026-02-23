package cz.bvv.errorhandlingdemo.poc;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.Test;

class FakeTokenManagerTest {

    private final FakeTokenManager fakeTokenManager = new FakeTokenManager();

    @Test
    void shouldSucceedWhenHeaderMissing() {
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());

        assertThatCode(() -> fakeTokenManager.refreshToken(exchange)).doesNotThrowAnyException();
    }

    @Test
    void shouldThrowIntegrationExceptionWhenHeaderTrue() {
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setHeader("X_TOKEN_REFRESH_FAIL", "true");

        assertThatThrownBy(() -> fakeTokenManager.refreshToken(exchange))
          .isInstanceOfSatisfying(IntegrationException.class, exception -> {
              org.assertj.core.api.Assertions.assertThat(exception.getErrors()).hasSize(1);
              org.assertj.core.api.Assertions.assertThat(exception.getErrors().getFirst().code())
                .isEqualTo("TOKEN_REFRESH_FAILED");
          });
    }
}
