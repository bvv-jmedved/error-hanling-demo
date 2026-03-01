package cz.bvv.errorhandlingdemo.poc;

import org.apache.camel.Processor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * PoC-only component used to simulate failures and retry scenarios for the architecture demo.
 *
 * <p>Types in this package are intentionally non-architectural and exist only to make behavior
 * observable in tests and local runs.
 */
@Component
@Profile("!poc")
public class NoopFailureStepProcessorFactory implements FailureStepProcessorFactory {

  @Override
  public Processor forStep(FailureStep step) {
    return exchange -> {};
  }
}
