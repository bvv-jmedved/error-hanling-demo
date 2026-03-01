package cz.bvv.errorhandlingdemo.builder.base.soap;

import cz.bvv.errorhandlingdemo.builder.base.BaseTechnicalSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.policy.route.RouteConfigurationModule;
import java.util.List;

public abstract class BaseSoapTechnicalSenderRouteBuilder extends BaseTechnicalSenderRouteBuilder {
    protected BaseSoapTechnicalSenderRouteBuilder(List<RouteConfigurationModule> modules) {
        super(modules);
    }
}
