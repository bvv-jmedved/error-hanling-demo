package cz.bvv.errorhandlingdemo.poc;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class FailureInjectorTest {

    @Test
    void shouldThrowWhenHeaderMatchesStepId() {
        FailureInjector failureInjector = new FailureInjector(FailureStep.SENDER_VALIDATE);
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setHeader("X_THROW_IN", "sender-validate");

        assertThatThrownBy(() -> failureInjector.process(exchange))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Injected failure at sender-validate");
    }

    @Test
    void shouldDoNothingWhenHeaderDoesNotMatchStepId() {
        FailureInjector failureInjector = new FailureInjector(FailureStep.SENDER_VALIDATE);
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setHeader("X_THROW_IN", "other-step");

        assertThatCode(() -> failureInjector.process(exchange)).doesNotThrowAnyException();
    }

    @Test
    void shouldThrowBusinessValidationIntegrationExceptionOnSenderValidate() {
        FailureInjector failureInjector = new FailureInjector(FailureStep.SENDER_VALIDATE);
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setHeader("X_THROW_IN", "sender-validate");
        exchange.getIn().setHeader("X_THROW_TYPE", "business-validation");

        assertThatThrownBy(() -> failureInjector.process(exchange))
          .isInstanceOfSatisfying(IntegrationException.class, exception -> {
              org.assertj.core.api.Assertions.assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
              org.assertj.core.api.Assertions.assertThat(exception.getErrors())
                .singleElement()
                .satisfies(error -> org.assertj.core.api.Assertions.assertThat(error.code())
                  .isEqualTo("VALIDATION_FAILED"));
          });
    }

    @Test
    void shouldThrowBusinessUnauthorizedIntegrationExceptionOnSenderAuth() {
        FailureInjector failureInjector = new FailureInjector(FailureStep.SENDER_AUTH);
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setHeader("X_THROW_IN", "sender-auth");
        exchange.getIn().setHeader("X_THROW_TYPE", "business-unauthorized");

        assertThatThrownBy(() -> failureInjector.process(exchange))
          .isInstanceOfSatisfying(IntegrationException.class, exception -> {
              org.assertj.core.api.Assertions.assertThat(exception.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
              org.assertj.core.api.Assertions.assertThat(exception.getErrors())
                .singleElement()
                .satisfies(error -> org.assertj.core.api.Assertions.assertThat(error.code())
                  .isEqualTo("UNAUTHORIZED"));
          });
    }
}
