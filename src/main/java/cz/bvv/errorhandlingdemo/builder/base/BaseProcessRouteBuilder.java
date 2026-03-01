package cz.bvv.errorhandlingdemo.builder.base;

import cz.bvv.errorhandlingdemo.policy.route.RouteConfigurationModule;
import java.util.List;

public abstract class BaseProcessRouteBuilder extends BaseRouteBuilder {
    protected BaseProcessRouteBuilder(List<RouteConfigurationModule> modules) {
        super(modules);
    }
}
