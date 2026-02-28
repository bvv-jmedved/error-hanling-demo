package cz.bvv.errorhandlingdemo.builder.common;

import cz.bvv.errorhandlingdemo.builder.common.routepolicy.RouteConfigurationModule;
import java.util.List;

public abstract class BaseSenderRouteBuilder extends BaseRouteBuilder {
    protected BaseSenderRouteBuilder(List<RouteConfigurationModule> modules) {
        super(modules);
    }
}
