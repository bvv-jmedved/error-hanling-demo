package cz.bvv.errorhandlingdemo.builder.base;

import cz.bvv.errorhandlingdemo.policy.route.RouteConfigurationModule;
import java.util.List;

public abstract class BaseTechnicalReceiverRouteBuilder extends BaseRouteBuilder {
    protected BaseTechnicalReceiverRouteBuilder(List<RouteConfigurationModule> modules) {
        super(modules);
    }
}
