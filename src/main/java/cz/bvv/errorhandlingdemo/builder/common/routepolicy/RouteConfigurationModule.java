package cz.bvv.errorhandlingdemo.builder.common.routepolicy;

import org.apache.camel.builder.RouteBuilder;

public interface RouteConfigurationModule {
    boolean supports(RouteBuilder builder);

    void afterRoutes(RouteBuilder builder);
}
