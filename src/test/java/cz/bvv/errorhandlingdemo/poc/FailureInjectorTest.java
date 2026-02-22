package cz.bvv.errorhandlingdemo.poc;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.Test;

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
}
