package cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.camel.builder.RouteBuilder;
import org.junit.jupiter.api.Test;

class LoggingRoutePolicyFactoryTest {

    private final LoggingRoutePolicyFactory factory = new LoggingRoutePolicyFactory();

    @Test
    void shouldUseDefaultsWhenAnnotationMissing() throws Exception {
        try (LoggingRoutePolicy policy = factory.create(new NonAnnotatedBuilder(), null)) {

            assertThat(policy.getService()).isEqualTo("NonAnnotatedBuilder");
            assertThat(policy.isCritical()).isFalse();
        }
    }

    @Test
    void shouldUseAnnotationValuesWhenPresent() throws Exception {
        Monitored monitored = AnnotatedBuilder.class.getAnnotation(Monitored.class);
        try (LoggingRoutePolicy policy = factory.create(new AnnotatedBuilder(), monitored)) {

            assertThat(policy.getService()).isEqualTo("orders-api");
            assertThat(policy.isCritical()).isTrue();
        }
    }

    @Test
    void shouldFallbackToClassNameWhenAnnotatedServiceBlank() throws Exception {
        Monitored monitored = BlankServiceAnnotatedBuilder.class.getAnnotation(Monitored.class);
        try (LoggingRoutePolicy policy = factory.create(new BlankServiceAnnotatedBuilder(), monitored)) {

            assertThat(policy.getService()).isEqualTo("BlankServiceAnnotatedBuilder");
            assertThat(policy.isCritical()).isTrue();
        }
    }

    private static final class NonAnnotatedBuilder extends RouteBuilder {
        @Override
        public void configure() {
        }
    }

    @Monitored(service = "orders-api", critical = true)
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
