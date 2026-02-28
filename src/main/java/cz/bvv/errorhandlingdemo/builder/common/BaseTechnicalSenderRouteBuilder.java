package cz.bvv.errorhandlingdemo.builder.common;

import cz.bvv.errorhandlingdemo.builder.common.routepolicy.RouteConfigurationModule;
import java.util.List;

public abstract class BaseTechnicalSenderRouteBuilder extends BaseRouteBuilder {
    protected BaseTechnicalSenderRouteBuilder(List<RouteConfigurationModule> modules) {
        super(modules);
    }
}
