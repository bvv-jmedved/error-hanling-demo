package cz.bvv.errorhandlingdemo.builder.base;

import cz.bvv.errorhandlingdemo.policy.route.RouteConfigurationModule;
import java.util.List;

public abstract class BaseSenderRouteBuilder extends BaseRouteBuilder {
    protected BaseSenderRouteBuilder(List<RouteConfigurationModule> modules) {
        super(modules);
    }
}
