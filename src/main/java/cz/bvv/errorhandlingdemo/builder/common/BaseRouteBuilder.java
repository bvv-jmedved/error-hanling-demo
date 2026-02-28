package cz.bvv.errorhandlingdemo.builder.common;

import cz.bvv.errorhandlingdemo.builder.common.routepolicy.RouteConfigurationModule;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.capability.MonitoringRoute;
import java.util.List;
import org.apache.camel.builder.RouteBuilder;

public abstract class BaseRouteBuilder extends RouteBuilder implements MonitoringRoute {
    private final List<RouteConfigurationModule> modules;
    private boolean configured;

    protected BaseRouteBuilder(List<RouteConfigurationModule> modules) {
        this.modules = modules == null ? List.of() : List.copyOf(modules);
    }

    @Override
    public final void configure() {
        if (configured) {
            throw new IllegalStateException("RouteBuilder already configured: " + getClass().getName());
        }
        configured = true;

        configureRoutes();
        validateRouteIds();
        modules.stream()
          .filter(module -> module.supports(this))
          .forEach(module -> module.afterRoutes(this));
    }

    protected abstract void configureRoutes();

    private void validateRouteIds() {
        getRouteCollection()
          .getRoutes()
          .forEach(route -> {
              String routeId = route.getId();
              if (routeId == null || routeId.isBlank()) {
                  throw new IllegalStateException("Explicit routeId required: " + route);
              }
          });
    }
}
