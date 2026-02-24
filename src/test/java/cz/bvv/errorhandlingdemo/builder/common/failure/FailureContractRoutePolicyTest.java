package cz.bvv.errorhandlingdemo.builder.common.failure;

import static org.assertj.core.api.Assertions.assertThat;

import cz.bvv.errorhandlingdemo.builder.common.IntegrationExchangeProperties;
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
        CountingIntegrationExceptionMapper mapper = new CountingIntegrationExceptionMapper(exception -> new IntegrationException(
          HttpStatus.BAD_GATEWAY,
          HttpStatus.BAD_GATEWAY.name(),
          exception == null ? "unknown" : exception.getMessage(),
          exception
        ));
        setIntegrationExceptionMapper(policy, mapper);

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

        assertThat(policy.mapContractCalls).isEqualTo(1);
        assertThat(mapper.mapCalls).isEqualTo(1);
        assertThat(exchange.getMessage().getBody(String.class)).isEqualTo("first-failure");
        assertThat(exchange.getProperty(IntegrationExchangeProperties.FAILURE_CONTRACT_APPLIED, Boolean.class)).isTrue();
        assertThat(exchange.getProperty(IntegrationExchangeProperties.FAILURE_HANDLED, Boolean.class)).isTrue();
    }

    @Test
    void shouldGiveAbsolutePrecedenceToOverrideWhenExceptionCaughtAlsoExists() throws Exception {
        CountingFailureContractRoutePolicy policy = new CountingFailureContractRoutePolicy();
        CountingIntegrationExceptionMapper mapper = new CountingIntegrationExceptionMapper(
          exception -> IntegrationException.unknownError());
        setIntegrationExceptionMapper(policy, mapper);

        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        RuntimeException exceptionCaught = new RuntimeException("exception-caught-must-be-ignored");
        exchange.setProperty(Exchange.EXCEPTION_CAUGHT, exceptionCaught);
        exchange.setException(exceptionCaught);
        exchange.setProperty(
          IntegrationExchangeProperties.EXCEPTION_OVERRIDE,
          new IntegrationException(HttpStatus.BAD_GATEWAY, "TOKEN_REFRESH_FAILED", "Token refresh failed", null)
        );

        policy.onExchangeDone(null, exchange);

        assertThat(policy.mapContractCalls).isEqualTo(1);
        assertThat(mapper.mapCalls).isEqualTo(0);
        assertThat(exchange.getMessage().getBody(String.class)).isEqualTo("Token refresh failed");
        assertThat(exchange.isFailed()).isFalse();
        assertThat(exchange.getProperty(IntegrationExchangeProperties.FAILURE_CONTRACT_APPLIED, Boolean.class)).isTrue();
        assertThat(exchange.getProperty(IntegrationExchangeProperties.FAILURE_HANDLED, Boolean.class)).isTrue();
    }

    @Test
    void shouldIgnoreInvalidOverrideAndFallbackToExceptionCaught() throws Exception {
        CountingFailureContractRoutePolicy policy = new CountingFailureContractRoutePolicy();
        CountingIntegrationExceptionMapper mapper = new CountingIntegrationExceptionMapper(exception -> new IntegrationException(
          HttpStatus.BAD_GATEWAY,
          "MAPPED_FROM_EXCEPTION_CAUGHT",
          exception == null ? "unknown" : exception.getMessage(),
          exception
        ));
        setIntegrationExceptionMapper(policy, mapper);

        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        RuntimeException exceptionCaught = new RuntimeException("exception-caught-wins");
        exchange.setProperty(IntegrationExchangeProperties.EXCEPTION_OVERRIDE, new RuntimeException("invalid-override"));
        exchange.setProperty(Exchange.EXCEPTION_CAUGHT, exceptionCaught);
        exchange.setException(exceptionCaught);

        policy.onExchangeDone(null, exchange);

        assertThat(policy.mapContractCalls).isEqualTo(1);
        assertThat(mapper.mapCalls).isEqualTo(1);
        assertThat(exchange.getMessage().getBody(String.class)).isEqualTo("exception-caught-wins");
        assertThat(exchange.isFailed()).isFalse();
        assertThat(exchange.getProperty(IntegrationExchangeProperties.FAILURE_CONTRACT_APPLIED, Boolean.class)).isTrue();
        assertThat(exchange.getProperty(IntegrationExchangeProperties.FAILURE_HANDLED, Boolean.class)).isTrue();
    }

    @Test
    void shouldIgnoreOverrideWhenExchangeIsNotFailed() throws Exception {
        CountingFailureContractRoutePolicy policy = new CountingFailureContractRoutePolicy();
        CountingIntegrationExceptionMapper mapper = new CountingIntegrationExceptionMapper(
          exception -> IntegrationException.unknownError());
        setIntegrationExceptionMapper(policy, mapper);

        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.setProperty(
          IntegrationExchangeProperties.EXCEPTION_OVERRIDE,
          new IntegrationException(HttpStatus.BAD_GATEWAY, "TOKEN_REFRESH_FAILED", "Token refresh failed", null)
        );

        policy.onExchangeDone(null, exchange);

        assertThat(policy.mapContractCalls).isEqualTo(0);
        assertThat(mapper.mapCalls).isEqualTo(0);
        assertThat(exchange.getProperty(IntegrationExchangeProperties.FAILURE_CONTRACT_APPLIED)).isNull();
        assertThat(exchange.getProperty(IntegrationExchangeProperties.FAILURE_HANDLED)).isNull();
    }

    private static void setIntegrationExceptionMapper(
      FailureContractRoutePolicy policy,
      IntegrationExceptionMapper mapper) throws Exception {
        Field field = FailureContractRoutePolicy.class.getDeclaredField("integrationExceptionMapper");
        field.setAccessible(true);
        field.set(policy, mapper);
    }

    private static final class CountingFailureContractRoutePolicy extends FailureContractRoutePolicy {
        private int mapContractCalls;

        @Override
        protected void mapContract(IntegrationException exception, Exchange exchange) {
            mapContractCalls++;
            exchange.getMessage().setBody(exception.getMessage());
        }
    }

    private static final class CountingIntegrationExceptionMapper implements IntegrationExceptionMapper {
        private final IntegrationExceptionMapper delegate;
        private int mapCalls;

        private CountingIntegrationExceptionMapper(IntegrationExceptionMapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public IntegrationException map(Exception exception) {
            mapCalls++;
            return delegate.map(exception);
        }
    }
}
