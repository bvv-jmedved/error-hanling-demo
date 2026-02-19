package cz.bvv.errorhanlingdemo.builder.common;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public abstract class BaseRouteBuilder extends RouteBuilder {

    @Override
    public void configure() {

        config();
    }

    protected Processor getFailureProcessor() {
        return exchange -> {};
    }

    protected abstract void config();
}

