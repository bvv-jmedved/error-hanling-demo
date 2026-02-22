package cz.bvv.errorhandlingdemo.builder.common.failure;

import static org.assertj.core.api.Assertions.assertThat;

import cz.bvv.errorhandlingdemo.builder.common.ExchangePropertyKeys;
import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import java.lang.reflect.Field;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class FailureContractRoutePolicyTest {

    @Test
    void shouldApplyFailureContractOnlyOnceForSameExchange() throws Exception {
        CountingFailureContractRoutePolicy policy = new CountingFailureContractRoutePolicy();
        setIntegrationExceptionMapper(policy, exception -> new IntegrationException(
          HttpStatus.BAD_GATEWAY,
          HttpStatus.BAD_GATEWAY.name(),
          exception == null ? "unknown" : exception.getMessage(),
          exception
        ));

        Exchange exchange = new DefaultExchange(new DefaultCamelContext());

        Exception firstException = new RuntimeException("first-failure");
        exchange.setProperty(Exchange.EXCEPTION_CAUGHT, firstException);
        exchange.setException(firstException);

        policy.onExchangeDone(null, exchange);

        assertThat(exchange.isFailed()).isFalse();

        Exception secondException = new RuntimeException("second-failure");
        exchange.setProperty(Exchange.EXCEPTION_CAUGHT, secondException);
        exchange.setException(secondException);

        policy.onExchangeDone(null, exchange);

        assertThat(policy.mappingCalls).isEqualTo(1);
        assertThat(exchange.getMessage().getBody(String.class)).isEqualTo("first-failure");
    }

    @Test
    void shouldUseIntegrationExceptionOverrideWithoutCallingMapper() throws Exception {
        CountingFailureContractRoutePolicy policy = new CountingFailureContractRoutePolicy();
        setIntegrationExceptionMapper(policy, exception -> {
            throw new AssertionError("Mapper must not be called when override is present");
        });

        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.setException(new RuntimeException("failed"));
        exchange.setProperty(
          ExchangePropertyKeys.INTEGRATION_EXCEPTION_OVERRIDE,
          new IntegrationException(HttpStatus.BAD_GATEWAY, "TOKEN_REFRESH_FAILED", "Token refresh failed", null)
        );

        policy.onExchangeDone(null, exchange);

        assertThat(policy.mappingCalls).isEqualTo(1);
        assertThat(exchange.getMessage().getBody(String.class)).isEqualTo("Token refresh failed");
        assertThat(exchange.isFailed()).isFalse();
    }

    private static void setIntegrationExceptionMapper(
      FailureContractRoutePolicy policy,
      IntegrationExceptionMapper mapper) throws Exception {
        Field field = FailureContractRoutePolicy.class.getDeclaredField("integrationExceptionMapper");
        field.setAccessible(true);
        field.set(policy, mapper);
    }

    private static final class CountingFailureContractRoutePolicy extends FailureContractRoutePolicy {
        private int mappingCalls;

        @Override
        protected void mapContract(IntegrationException exception, Exchange exchange) {
            mappingCalls++;
            exchange.getMessage().setBody(exception.getMessage());
        }
    }
}
