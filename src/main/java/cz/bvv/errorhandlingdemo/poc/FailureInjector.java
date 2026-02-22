package cz.bvv.errorhandlingdemo.poc;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * PoC-only failure injection component.
 *
 * This processor exists solely to simulate failures in different
 * layers of the integration pipeline for demonstration and testing
 * of the error handling architecture (DD-016 validation).
 *
 * NOT part of the production error handling design.
 * MUST NOT be copied to integration-services project.
 *
 * Activated only under Spring profile "poc".
 */
@Component
@Profile("poc")
public class FailureInjector implements Processor {

    private static final String X_THROW_IN = "X_THROW_IN";
    private final String stepId;

    public FailureInjector(String stepId) {
        this.stepId = stepId;
    }

    @Override
    public void process(Exchange exchange) {
        String throwIn = exchange.getIn().getHeader(X_THROW_IN, String.class);

        if (throwIn == null) {
            return;
        }

        if (stepId.equals(throwIn)) {
            throw new RuntimeException("Injected failure at " + stepId);
        }
    }
}
