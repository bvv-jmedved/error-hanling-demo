package cz.bvv.errorhandlingdemo.builder.common.routepolicy.module;

import cz.bvv.errorhandlingdemo.builder.common.routepolicy.FailContractRoutePolicy;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.RouteConfigurationModule;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.capability.FailContractRoute;
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
