package cz.bvv.errorhanlingdemo.builder.common;

import org.apache.camel.builder.RouteBuilder;

public abstract class BaseRouteBuilder extends RouteBuilder {

    @Override
    public final void configure() {

        onException(Exception.class)
          .log("Exception occurred in route: ${routeId}")
          .handled(exchange -> isLast());

        errorHandler(defaultErrorHandler()
          .logStackTrace(false)
          .logExhausted(false));


        config();

    }

    protected boolean isLast() {
        return false;
    }

    protected abstract void config();
}
