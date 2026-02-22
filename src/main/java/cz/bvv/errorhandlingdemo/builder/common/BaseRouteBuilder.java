package cz.bvv.errorhandlingdemo.builder.common;

import org.apache.camel.builder.RouteBuilder;

public abstract class BaseRouteBuilder extends RouteBuilder {

    @Override
    public void configure() {

        config();
    }

    protected abstract void config();
}

