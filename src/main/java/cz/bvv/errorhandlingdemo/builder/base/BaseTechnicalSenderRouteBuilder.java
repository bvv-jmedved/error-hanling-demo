package cz.bvv.errorhandlingdemo.builder.base;

import cz.bvv.errorhandlingdemo.policy.route.RouteConfigurationModule;
import java.util.List;

public abstract class BaseTechnicalSenderRouteBuilder extends BaseRouteBuilder {
    protected BaseTechnicalSenderRouteBuilder(List<RouteConfigurationModule> modules) {
        super(modules);
    }
}
