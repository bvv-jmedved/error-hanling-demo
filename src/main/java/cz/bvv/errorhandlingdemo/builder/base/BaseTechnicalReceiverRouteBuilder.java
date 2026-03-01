package cz.bvv.errorhandlingdemo.builder.base;

import cz.bvv.errorhandlingdemo.policy.route.RouteConfigurationModule;
import java.util.List;

/**
 * Base type for technical receiver routes that execute downstream calls.
 *
 * <p>Technical receiver routes are usually invoked from receiver routes and may intentionally
 * propagate exceptions to let outer error policies decide retries or termination behavior.
 */
public abstract class BaseTechnicalReceiverRouteBuilder extends BaseRouteBuilder {
    protected BaseTechnicalReceiverRouteBuilder(List<RouteConfigurationModule> modules) {
        super(modules);
    }
}
