package cz.bvv.errorhandlingdemo.poc;

import org.apache.camel.Processor;

public interface FailureStepProcessorFactory {
    Processor forStep(FailureStep step);
}
