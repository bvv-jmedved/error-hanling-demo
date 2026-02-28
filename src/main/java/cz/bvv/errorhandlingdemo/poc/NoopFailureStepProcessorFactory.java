package cz.bvv.errorhandlingdemo.poc;

import org.apache.camel.Processor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!poc")
public class NoopFailureStepProcessorFactory implements FailureStepProcessorFactory {

    @Override
    public Processor forStep(FailureStep step) {
        return exchange -> {
        };
    }
}
