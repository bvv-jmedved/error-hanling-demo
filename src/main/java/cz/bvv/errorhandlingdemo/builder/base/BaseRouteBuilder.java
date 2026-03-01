package cz.bvv.errorhandlingdemo.builder.base;

import cz.bvv.errorhandlingdemo.policy.route.RouteConfigurationModule;
import cz.bvv.errorhandlingdemo.policy.route.capability.MonitoringRoute;
import java.util.List;
import org.apache.camel.builder.RouteBuilder;

/**
 * Base Camel {@code RouteBuilder} used as the primary architecture template in this demo.
 *
 * <p>Subclasses define only route DSL in {@link #configureRoutes()}. Infrastructure concerns
 * are applied afterwards by ordered {@code RouteConfigurationModule} beans.
 *
 * <p>This class is intentionally final in lifecycle orchestration to keep behavior deterministic
 * and to avoid hidden side effects from overridden {@code configure()} implementations.
 */
public abstract class BaseRouteBuilder extends RouteBuilder implements MonitoringRoute {
    private final List<RouteConfigurationModule> modules;
    private boolean configured;

    protected BaseRouteBuilder(List<RouteConfigurationModule> modules) {
        this.modules = modules == null ? List.of() : List.copyOf(modules);
    }

    /**
     * Executes the architecture-defined route bootstrap sequence exactly once.
     *
     * <p>Execution order:
     * <ol>
     *   <li>Build route DSL via {@link #configureRoutes()}.</li>
     *   <li>Validate that each route has an explicit {@code routeId}.</li>
     *   <li>Apply all supporting {@code RouteConfigurationModule} instances.</li>
     * </ol>
     */
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

    /**
     * Implement route DSL only.
     *
     * <p>Do not apply cross-cutting concerns directly here. Use policy modules for logging,
     * failure-contract mapping, and other architectural infrastructure.
     */
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
