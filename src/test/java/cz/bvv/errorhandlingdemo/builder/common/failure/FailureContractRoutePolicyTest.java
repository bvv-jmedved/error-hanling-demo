package cz.bvv.errorhandlingdemo.builder.common.failure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cz.bvv.errorhandlingdemo.builder.common.IntegrationExchangeProperties;
import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import java.lang.reflect.Field;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultMessage;
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
        assertThat(exchange.getProperty(IntegrationExchangeProperties.FAILURE_CONTRACT_APPLIED, Boolean.class)).isTrue();
        assertThat(exchange.getProperty(IntegrationExchangeProperties.FAILURE_HANDLED, Boolean.class)).isTrue();
    }

    @Test
    void shouldUseExceptionCaughtAsAuthoritativeSource() throws Exception {
        CountingFailureContractRoutePolicy policy = new CountingFailureContractRoutePolicy();
        setIntegrationExceptionMapper(policy, exception -> new IntegrationException(
          HttpStatus.BAD_GATEWAY,
          "MAPPED_FROM_EXCEPTION_CAUGHT",
          exception == null ? "unknown" : exception.getMessage(),
          exception
        ));

        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        RuntimeException exceptionCaught = new RuntimeException("failed-from-caught");
        exchange.setProperty(Exchange.EXCEPTION_CAUGHT, exceptionCaught);
        exchange.setException(new RuntimeException("failed-from-exchange-exception"));

        policy.onExchangeDone(null, exchange);

        assertThat(policy.mappingCalls).isEqualTo(1);
        assertThat(exchange.getMessage().getBody(String.class)).isEqualTo("failed-from-caught");
        assertThat(exchange.isFailed()).isFalse();
        assertThat(exchange.getProperty("integration.exception.override")).isNull();
        assertThat(exchange.getProperty(IntegrationExchangeProperties.FAILURE_CONTRACT_APPLIED, Boolean.class)).isTrue();
        assertThat(exchange.getProperty(IntegrationExchangeProperties.FAILURE_HANDLED, Boolean.class)).isTrue();
    }

    @Test
    void shouldFallbackToExchangeExceptionWhenExceptionCaughtIsMissing() throws Exception {
        CountingFailureContractRoutePolicy policy = new CountingFailureContractRoutePolicy();
        setIntegrationExceptionMapper(policy, exception -> new IntegrationException(
          HttpStatus.BAD_GATEWAY,
          "MAPPED_FROM_GET_EXCEPTION",
          exception == null ? "unknown" : exception.getMessage(),
          exception
        ));

        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.setException(new RuntimeException("failed-from-get-exception"));

        policy.onExchangeDone(null, exchange);

        assertThat(policy.mappingCalls).isEqualTo(1);
        assertThat(exchange.getMessage().getBody(String.class)).isEqualTo("failed-from-get-exception");
        assertThat(exchange.isFailed()).isFalse();
        assertThat(exchange.getProperty(IntegrationExchangeProperties.FAILURE_CONTRACT_APPLIED, Boolean.class)).isTrue();
        assertThat(exchange.getProperty(IntegrationExchangeProperties.FAILURE_HANDLED, Boolean.class)).isTrue();
    }

    @Test
    void shouldUseUnknownFallbackWhenExchangeFailedWithoutFailureSource() throws Exception {
        CountingFailureContractRoutePolicy policy = new CountingFailureContractRoutePolicy();
        setIntegrationExceptionMapper(policy, exception -> {
            throw new AssertionError("Mapper must not be called for unknown fallback");
        });

        Exchange exchange = mock(Exchange.class);
        when(exchange.isFailed()).thenReturn(true, false);
        when(exchange.getProperty(IntegrationExchangeProperties.FAILURE_CONTRACT_APPLIED, Boolean.class)).thenReturn(null);
        when(exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class)).thenReturn(null);
        when(exchange.getException()).thenReturn(null);
        when(exchange.getMessage()).thenReturn(new DefaultMessage(new DefaultCamelContext()));

        policy.onExchangeDone(null, exchange);

        assertThat(policy.mappingCalls).isEqualTo(1);
        assertThat(exchange.getMessage().getBody(String.class)).isEqualTo("Unknown error");
        verify(exchange).setProperty(IntegrationExchangeProperties.FAILURE_CONTRACT_APPLIED, true);
        verify(exchange).setProperty(IntegrationExchangeProperties.FAILURE_HANDLED, true);
    }

    @Test
    void shouldNotSetFailureHandledFlagWhenExchangeIsNotFailedContinuedPathEquivalent() throws Exception {
        CountingFailureContractRoutePolicy policy = new CountingFailureContractRoutePolicy();
        setIntegrationExceptionMapper(policy, exception -> IntegrationException.unknownError());

        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        RuntimeException original = new RuntimeException("continued-original");
        exchange.setProperty(Exchange.EXCEPTION_CAUGHT, original);

        policy.onExchangeDone(null, exchange);

        assertThat(policy.mappingCalls).isEqualTo(0);
        assertThat(exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class)).isSameAs(original);
        assertThat(exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class)).isNotInstanceOf(IntegrationException.class);
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
        private int mappingCalls;

        @Override
        protected void mapContract(IntegrationException exception, Exchange exchange) {
            mappingCalls++;
            exchange.getMessage().setBody(exception.getMessage());
        }
    }
}
