package cz.bvv.errorhandlingdemo.builder.common;

import cz.bvv.errorhandlingdemo.poc.FailureInjector;
import cz.bvv.errorhandlingdemo.poc.FailureStep;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

public abstract class BaseRouteBuilder extends RouteBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(BaseRouteBuilder.class);

    @Autowired
    private Environment environment;

    @Override
    public void configure() {

        config();
    }

    protected abstract void config();

    protected Processor pocStep(FailureStep step) {
        FailureInjector injector = new FailureInjector(step);

        return exchange -> {
            if (!environment.acceptsProfiles(Profiles.of("poc"))) {
                return;
            }

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

