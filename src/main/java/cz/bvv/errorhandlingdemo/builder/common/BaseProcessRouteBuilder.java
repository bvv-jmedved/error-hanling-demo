package cz.bvv.errorhandlingdemo.builder.common;

import cz.bvv.errorhandlingdemo.builder.common.routepolicy.RouteConfigurationModule;
import java.util.List;

public abstract class BaseProcessRouteBuilder extends BaseRouteBuilder {
    protected BaseProcessRouteBuilder(List<RouteConfigurationModule> modules) {
        super(modules);
    }
}
