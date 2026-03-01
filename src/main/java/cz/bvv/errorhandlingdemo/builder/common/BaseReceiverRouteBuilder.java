package cz.bvv.errorhandlingdemo.builder.common;

import cz.bvv.errorhandlingdemo.policy.route.RouteConfigurationModule;
import java.util.List;

public abstract class BaseReceiverRouteBuilder extends BaseRouteBuilder {
    protected BaseReceiverRouteBuilder(List<RouteConfigurationModule> modules) {
        super(modules);
    }
}
