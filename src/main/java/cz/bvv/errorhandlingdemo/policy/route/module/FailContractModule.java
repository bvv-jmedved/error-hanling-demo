package cz.bvv.errorhandlingdemo.policy.route.module;

import cz.bvv.errorhandlingdemo.policy.route.FailContractRoutePolicy;
import cz.bvv.errorhandlingdemo.policy.route.RouteConfigurationModule;
import cz.bvv.errorhandlingdemo.policy.route.capability.FailContractRoute;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(10)
public class FailContractModule implements RouteConfigurationModule {

    @Override
    public boolean supports(RouteBuilder builder) {
        return builder instanceof FailContractRoute;
    }

    @Override
    public void afterRoutes(RouteBuilder builder) {
        FailContractRoute failContractRoute = (FailContractRoute) builder;
        FailContractRoutePolicy policy = failContractRoute.failureContractRoutePolicy();
        builder.getRouteCollection()
          .getRoutes()
          .forEach(route -> route.routePolicy(policy));
    }
}
