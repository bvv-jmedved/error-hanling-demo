package cz.bvv.errorhandlingdemo.policy.route.logging;

import cz.bvv.errorhandlingdemo.support.exchange.IntegrationExchangeHeaders;
import cz.bvv.errorhandlingdemo.support.exchange.IntegrationExchangeProperties;
import java.util.UUID;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Logging policy component used by the architecture demo.
 *
 * <p>This type participates in canonical process-status monitoring and demonstrates
 * how route metadata is translated into stable operational logs.
 */
@Component
public class CorrelationIdResolver {

    /**
     * Ensures that a stable correlation id exists in both exchange property and message header.
     *
     * <p>Resolution order:
     * <ol>
     *   <li>Existing exchange property.</li>
     *   <li>Incoming message header.</li>
     *   <li>Generated UUID when missing.</li>
     * </ol>
     *
     * @param exchange Camel exchange being processed
     * @return resolved or generated correlation id
     */
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
