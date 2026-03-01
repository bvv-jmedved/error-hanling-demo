package cz.bvv.errorhandlingdemo.builder.base;

import cz.bvv.errorhandlingdemo.policy.route.RouteConfigurationModule;
import java.util.List;

/**
 * Base type for process/orchestration routes in the demo hierarchy.
 *
 * <p>Process routes represent integration middleware steps between sender and receiver boundaries.
 */
public abstract class BaseProcessRouteBuilder extends BaseRouteBuilder {
    protected BaseProcessRouteBuilder(List<RouteConfigurationModule> modules) {
        super(modules);
    }
}
