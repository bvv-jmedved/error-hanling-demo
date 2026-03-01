package cz.bvv.errorhandlingdemo.builder.common.soap;

import cz.bvv.errorhandlingdemo.builder.common.BaseTechnicalSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.policy.route.RouteConfigurationModule;
import java.util.List;

public abstract class BaseSoapTechnicalSenderRouteBuilder extends BaseTechnicalSenderRouteBuilder {
    protected BaseSoapTechnicalSenderRouteBuilder(List<RouteConfigurationModule> modules) {
        super(modules);
    }
}
