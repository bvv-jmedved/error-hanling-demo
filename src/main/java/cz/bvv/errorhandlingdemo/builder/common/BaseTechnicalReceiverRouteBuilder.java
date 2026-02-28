package cz.bvv.errorhandlingdemo.builder.common;

import cz.bvv.errorhandlingdemo.builder.common.routepolicy.RouteConfigurationModule;
import java.util.List;

public abstract class BaseTechnicalReceiverRouteBuilder extends BaseRouteBuilder {
    protected BaseTechnicalReceiverRouteBuilder(List<RouteConfigurationModule> modules) {
        super(modules);
    }
}
