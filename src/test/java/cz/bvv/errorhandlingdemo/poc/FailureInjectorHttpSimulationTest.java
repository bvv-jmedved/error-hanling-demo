package cz.bvv.errorhandlingdemo.poc;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.camel.Exchange;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.Test;

class FailureInjectorHttpSimulationTest {

    @Test
    void shouldThrowHttpOperationFailedExceptionWhenTypeIsHttp() {
        FailureInjector failureInjector = new FailureInjector(FailureStep.SENDER_VALIDATE);
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setHeader("X_THROW_IN", "sender-validate");
        exchange.getIn().setHeader("X_THROW_TYPE", "http");
        exchange.getIn().setHeader("X_THROW_STATUS", "404");
        exchange.getIn().setHeader("X_THROW_STATUS_TEXT", "Not Found");
        exchange.getIn().setHeader("X_THROW_BODY", "{\"error\":\"missing\"}");

        assertThatThrownBy(() -> failureInjector.process(exchange))
          .isInstanceOfSatisfying(HttpOperationFailedException.class, exception -> {
              org.assertj.core.api.Assertions.assertThat(exception.getStatusCode()).isEqualTo(404);
              org.assertj.core.api.Assertions.assertThat(exception.getStatusText()).isEqualTo("Not Found");
              org.assertj.core.api.Assertions.assertThat(exception.getResponseBody()).isEqualTo("{\"error\":\"missing\"}");
          });
    }

    @Test
    void shouldDefaultTo500WhenStatusMissing() {
        FailureInjector failureInjector = new FailureInjector(FailureStep.SENDER_VALIDATE);
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setHeader("X_THROW_IN", "sender-validate");
        exchange.getIn().setHeader("X_THROW_TYPE", "http");

        assertThatThrownBy(() -> failureInjector.process(exchange))
          .isInstanceOfSatisfying(HttpOperationFailedException.class, exception -> {
              org.assertj.core.api.Assertions.assertThat(exception.getStatusCode()).isEqualTo(500);
              org.assertj.core.api.Assertions.assertThat(exception.getStatusText()).isEqualTo("Simulated HTTP failure");
          });
    }

    @Test
    void shouldFallbackToRuntimeExceptionWhenTypeMissing() {
        FailureInjector failureInjector = new FailureInjector(FailureStep.SENDER_VALIDATE);
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setHeader("X_THROW_IN", "sender-validate");

        assertThatThrownBy(() -> failureInjector.process(exchange))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Injected failure at sender-validate");
    }

    @Test
    void shouldSimulateHttp200BySettingResponseBodyWithoutThrowing() {
        FailureInjector failureInjector = new FailureInjector(FailureStep.TECHNICAL_CALL);
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setHeader("X_THROW_IN", "technical-call");
        exchange.getIn().setHeader("X_THROW_TYPE", "http");
        exchange.getIn().setHeader("X_THROW_STATUS", "200");
        exchange.getIn().setHeader("X_THROW_BODY", "{\"errCode\":\"K2_123\",\"errMsg\":\"Business validation failed\"}");

        assertThatCode(() -> failureInjector.process(exchange)).doesNotThrowAnyException();
        org.assertj.core.api.Assertions.assertThat(exchange.getMessage().getBody(String.class))
          .isEqualTo("{\"errCode\":\"K2_123\",\"errMsg\":\"Business validation failed\"}");
    }
}
