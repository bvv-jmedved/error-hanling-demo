package cz.bvv.errorhandlingdemo.policy.route.module;

import cz.bvv.errorhandlingdemo.policy.route.RouteConfigurationModule;
import cz.bvv.errorhandlingdemo.policy.route.capability.MonitoringRoute;
import cz.bvv.errorhandlingdemo.policy.route.logging.LoggingRoutePolicy;
import cz.bvv.errorhandlingdemo.policy.route.logging.LoggingRoutePolicyFactory;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Policy module participating in post-DSL route configuration.
 *
 * <p>Modules provide deterministic, capability-driven infrastructure composition and are the main
 * extension point for adding architecture-wide behavior.
 */
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
    LoggingRoutePolicy policy = loggingRoutePolicyFactory.create(builder);
    builder.getRouteCollection().getRoutes().forEach(route -> route.routePolicy(policy));
  }
}
