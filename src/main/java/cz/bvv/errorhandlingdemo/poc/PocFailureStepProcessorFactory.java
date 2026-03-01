package cz.bvv.errorhandlingdemo.poc;

import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * PoC-only component used to simulate failures and retry scenarios for the architecture demo.
 *
 * <p>Types in this package are intentionally non-architectural and exist only to make behavior
 * observable in tests and local runs.
 */
@Component
@Profile("poc")
public class PocFailureStepProcessorFactory implements FailureStepProcessorFactory {

  private static final Logger LOG = LoggerFactory.getLogger(PocFailureStepProcessorFactory.class);

  @Override
  public Processor forStep(FailureStep step) {
    FailureInjector injector = new FailureInjector(step);
    return exchange -> {
      LOG.info("{} - BEGIN", step.description());
      try {
        injector.process(exchange);
        LOG.info("{} - SUCCESS", step.description());
      } catch (Exception ex) {
        LOG.info("{} - ERROR", step.description());
        throw ex;
      }
    };
  }
}
