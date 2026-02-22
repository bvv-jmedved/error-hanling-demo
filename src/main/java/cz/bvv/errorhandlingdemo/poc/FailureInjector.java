package cz.bvv.errorhandlingdemo.poc;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.context.annotation.Profile;

/**
 * PoC-only failure injection component.
 * <p>
 * This processor exists solely to simulate failures in different layers of the integration pipeline
 * for demonstration and testing of the error handling architecture (DD-016 validation).
 * <p>
 * NOT part of the production error handling design. MUST NOT be copied to integration-services
 * project.
 * <p>
 * Activated only under Spring profile "poc".
 */
@Profile("poc")
public class FailureInjector implements Processor {

    private static final String X_THROW_IN = "X_THROW_IN";
    private static final String X_THROW_TYPE = "X_THROW_TYPE";
    private static final String X_THROW_STATUS = "X_THROW_STATUS";
    private static final String X_THROW_STATUS_TEXT = "X_THROW_STATUS_TEXT";
    private static final String X_THROW_BODY = "X_THROW_BODY";
    private static final int DEFAULT_HTTP_STATUS = 500;
    private static final String DEFAULT_HTTP_STATUS_TEXT = "Simulated HTTP failure";
    private final FailureStep step;

    public FailureInjector(FailureStep step) {
        this.step = step;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String throwIn = exchange.getIn().getHeader(X_THROW_IN, String.class);

        if (throwIn == null) {
            return;
        }

        if (!step.headerValue().equals(throwIn)) {
            return;
        }

        String throwType = exchange.getIn().getHeader(X_THROW_TYPE, String.class);
        if ("http".equals(throwType)) {
            int statusCode =
              parseStatusCode(exchange.getIn().getHeader(X_THROW_STATUS, String.class));
            String statusText = exchange.getIn().getHeader(X_THROW_STATUS_TEXT, String.class);
            String responseBody = exchange.getIn().getHeader(X_THROW_BODY, String.class);

            throw new HttpOperationFailedException(
              "http://poc-downstream",
              statusCode,
              statusText == null ? DEFAULT_HTTP_STATUS_TEXT : statusText,
              null,
              null,
              responseBody
            );
        }
        throw new RuntimeException("Injected failure at " + step.headerValue());

    }

    private static int parseStatusCode(String rawStatusCode) {
        if (rawStatusCode == null) {
            return DEFAULT_HTTP_STATUS;
        }
        try {
            return Integer.parseInt(rawStatusCode);
        } catch (NumberFormatException ex) {
            return DEFAULT_HTTP_STATUS;
        }
    }

}
