package cz.bvv.errorhanlingdemo.builder.common;

import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public abstract class BaseRouteBuilder extends RouteBuilder {

    @Override
    public final void configure() {

        onCompletion().onFailureOnly().modeBeforeConsumer()
          .log("Handling onCompletion  in route: ${routeId} with body: ${body}")
          .process(getFailureProcessor());

        errorHandler(defaultErrorHandler()
          .logStackTrace(false)
          .logExhausted(false));

        config();
    }

    protected Processor getFailureProcessor() {
        return exchange -> {};
    }

    protected abstract void config();
}

