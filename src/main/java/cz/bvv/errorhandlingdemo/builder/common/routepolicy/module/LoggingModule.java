package cz.bvv.errorhandlingdemo.builder.common.routepolicy.module;

import cz.bvv.errorhandlingdemo.builder.common.routepolicy.RouteConfigurationModule;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.capability.MonitoringRoute;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging.LoggingRoutePolicy;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging.LoggingRoutePolicyFactory;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging.Monitored;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(100)
public class LoggingModule implements RouteConfigurationModule {
    private final LoggingRoutePolicyFactory loggingRoutePolicyFactory;

    public LoggingModule(LoggingRoutePolicyFactory loggingRoutePolicyFactory) {
        this.loggingRoutePolicyFactory = loggingRoutePolicyFactory;
    }

    @Override
    public boolean supports(RouteBuilder builder) {
        return builder instanceof MonitoringRoute;
    }

    @Override
    @SuppressWarnings("resource")
    public void afterRoutes(RouteBuilder builder) {
        Monitored monitored = builder.getClass().getAnnotation(Monitored.class);
        LoggingRoutePolicy policy = loggingRoutePolicyFactory.create(builder, monitored);
        builder.getRouteCollection()
          .getRoutes()
          .forEach(route -> {
              if (route.getRoutePolicies() != null
                && route.getRoutePolicies().stream().anyMatch(existing -> existing == policy)) {
                  return;
              }
              route.routePolicy(policy);
          });
    }
}
