package cz.bvv.errorhandlingdemo.builder.base;

import cz.bvv.errorhandlingdemo.policy.route.RouteConfigurationModule;
import java.util.List;

/**
 * Base type for receiver-facing routes.
 *
 * <p>Receiver routes typically own retry and downstream failure interpretation logic.
 */
public abstract class BaseReceiverRouteBuilder extends BaseRouteBuilder {
    protected BaseReceiverRouteBuilder(List<RouteConfigurationModule> modules) {
        super(modules);
    }
}
