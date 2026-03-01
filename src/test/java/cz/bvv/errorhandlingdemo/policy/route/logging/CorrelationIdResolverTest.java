package cz.bvv.errorhandlingdemo.policy.route.logging;

import static org.assertj.core.api.Assertions.assertThat;

import cz.bvv.errorhandlingdemo.builder.common.IntegrationExchangeHeaders;
import cz.bvv.errorhandlingdemo.builder.common.IntegrationExchangeProperties;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.Test;

class CorrelationIdResolverTest {

    private final CorrelationIdResolver resolver = new CorrelationIdResolver();

    @Test
    void shouldGenerateCorrelationIdWhenMissing() {
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());

        String correlationId = resolver.ensureCorrelationId(exchange);

        assertThat(correlationId).isNotBlank();
        assertThat(exchange.getProperty(IntegrationExchangeProperties.CORRELATION_ID, String.class))
          .isEqualTo(correlationId);
        assertThat(exchange.getIn().getHeader(IntegrationExchangeHeaders.CORRELATION_ID, String.class))
          .isEqualTo(correlationId);
    }

    @Test
    void shouldReuseHeaderCorrelationIdWhenPresent() {
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setHeader(IntegrationExchangeHeaders.CORRELATION_ID, "abc-123");

        String correlationId = resolver.ensureCorrelationId(exchange);

        assertThat(correlationId).isEqualTo("abc-123");
        assertThat(exchange.getProperty(IntegrationExchangeProperties.CORRELATION_ID, String.class))
          .isEqualTo("abc-123");
    }
}
