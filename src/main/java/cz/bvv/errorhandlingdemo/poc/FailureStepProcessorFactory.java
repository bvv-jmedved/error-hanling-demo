package cz.bvv.errorhandlingdemo.poc;

import org.apache.camel.Processor;

/**
 * PoC-only component used to simulate failures and retry scenarios for the architecture demo.
 *
 * <p>Types in this package are intentionally non-architectural and exist only
 * to make behavior observable in tests and local runs.
 */
public interface FailureStepProcessorFactory {
    /**
     * Provides processor that simulates behavior for the selected PoC step.
     *
     * @param step simulated business/technical step
     * @return processor used in route DSL for the selected step
     */
    Processor forStep(FailureStep step);
}
