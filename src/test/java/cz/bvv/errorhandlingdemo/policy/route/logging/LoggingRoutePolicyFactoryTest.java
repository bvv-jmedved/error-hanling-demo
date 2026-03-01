package cz.bvv.errorhandlingdemo.policy.route.logging;

import static org.assertj.core.api.Assertions.assertThat;

import cz.bvv.errorhandlingdemo.foundation.annotation.FlowDirection;
import org.apache.camel.builder.RouteBuilder;
import org.junit.jupiter.api.Test;

class LoggingRoutePolicyFactoryTest {

    private final LoggingRoutePolicyFactory factory = new LoggingRoutePolicyFactory(
      new ExchangeOutcomeResolver(),
      new CorrelationIdResolver(),
      new ProcessStatusTracker()
    );

    @Test
    void shouldUseDefaultsWhenAnnotationMissing() throws Exception {
        try (LoggingRoutePolicy policy = factory.create(new NonAnnotatedBuilder())) {

            assertThat(policy.getService()).isEqualTo("NonAnnotatedBuilder");
            assertThat(policy.isCritical()).isFalse();
            assertThat(policy.getFlowDirection()).isEqualTo(FlowDirectionConfig.EMPTY);
            assertThat(policy.getLoggingStatus().onStart()).isEqualTo(ExchangeLoggingStatus.RUNNING);
            assertThat(policy.getLoggingStatus().onComplete()).isEqualTo(ExchangeLoggingStatus.RUNNING);
            assertThat(policy.getLoggingStatus().onFailure()).isEqualTo(ExchangeLoggingStatus.FAILURE);
        }
    }

    @Test
    void shouldUseAnnotationValuesWhenPresent() throws Exception {
        try (LoggingRoutePolicy policy = factory.create(new AnnotatedBuilder())) {

            assertThat(policy.getService()).isEqualTo("orders-api");
            assertThat(policy.isCritical()).isTrue();
            assertThat(policy.getFlowDirection().source()).isEqualTo("source-a");
            assertThat(policy.getFlowDirection().target()).isEqualTo("target-b");
            assertThat(policy.getFlowDirection().process()).isEqualTo("create-order");
            assertThat(policy.getLoggingStatus().onStart()).isEqualTo(ExchangeLoggingStatus.STARTED);
            assertThat(policy.getLoggingStatus().onComplete()).isEqualTo(ExchangeLoggingStatus.COMPLETED);
            assertThat(policy.getLoggingStatus().onFailure()).isEqualTo(ExchangeLoggingStatus.FAILED);
        }
    }

    @Test
    void shouldFallbackToClassNameWhenAnnotatedServiceBlank() throws Exception {
        try (LoggingRoutePolicy policy = factory.create(new BlankServiceAnnotatedBuilder())) {

            assertThat(policy.getService()).isEqualTo("BlankServiceAnnotatedBuilder");
            assertThat(policy.isCritical()).isTrue();
            assertThat(policy.getFlowDirection()).isEqualTo(FlowDirectionConfig.EMPTY);
            assertThat(policy.getLoggingStatus().onStart()).isEqualTo(ExchangeLoggingStatus.RUNNING);
            assertThat(policy.getLoggingStatus().onComplete()).isEqualTo(ExchangeLoggingStatus.RUNNING);
            assertThat(policy.getLoggingStatus().onFailure()).isEqualTo(ExchangeLoggingStatus.FAILURE);
        }
    }

    private static final class NonAnnotatedBuilder extends RouteBuilder {
        @Override
        public void configure() {
        }
    }

    @Monitored(service = "orders-api", critical = true)
    @FlowDirection(source = "source-a", target = "target-b", process = "create-order")
    @LoggingStatus(
      onStart = ExchangeLoggingStatus.STARTED,
      onComplete = ExchangeLoggingStatus.COMPLETED,
      onFailure = ExchangeLoggingStatus.FAILED
    )
    private static final class AnnotatedBuilder extends RouteBuilder {
        @Override
        public void configure() {
        }
    }

    @Monitored(service = "   ", critical = true)
    private static final class BlankServiceAnnotatedBuilder extends RouteBuilder {
        @Override
        public void configure() {
        }
    }
}
