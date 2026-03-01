package cz.bvv.errorhandlingdemo.policy.route;

import org.apache.camel.builder.RouteBuilder;

public interface RouteConfigurationModule {
    boolean supports(RouteBuilder builder);

    void afterRoutes(RouteBuilder builder);
}
