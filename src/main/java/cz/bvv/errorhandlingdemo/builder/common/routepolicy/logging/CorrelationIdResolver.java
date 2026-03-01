package cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging;

import cz.bvv.errorhandlingdemo.builder.common.IntegrationExchangeHeaders;
import cz.bvv.errorhandlingdemo.builder.common.IntegrationExchangeProperties;
import java.util.UUID;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CorrelationIdResolver {

    public String ensureCorrelationId(Exchange exchange) {
        String correlationId = firstNonBlank(
          exchange.getProperty(IntegrationExchangeProperties.CORRELATION_ID, String.class),
          exchange.getIn().getHeader(IntegrationExchangeHeaders.CORRELATION_ID, String.class)
        );

        if (!StringUtils.hasText(correlationId)) {
            correlationId = UUID.randomUUID().toString();
        }

        exchange.setProperty(IntegrationExchangeProperties.CORRELATION_ID, correlationId);
        exchange.getIn().setHeader(IntegrationExchangeHeaders.CORRELATION_ID, correlationId);
        return correlationId;
    }

    private static String firstNonBlank(String first, String second) {
        if (StringUtils.hasText(first)) {
            return first;
        }
        if (StringUtils.hasText(second)) {
            return second;
        }
        return null;
    }
}
