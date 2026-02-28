package cz.bvv.errorhandlingdemo.builder.common;

import cz.bvv.errorhandlingdemo.builder.common.routepolicy.FailContractRoutePolicy;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.RouteConfigurationModule;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.capability.FailContractRoute;
import java.util.List;

public abstract class BaseSyncSenderRouteBuilder extends BaseSenderRouteBuilder implements FailContractRoute {
    private final FailContractRoutePolicy failureContractRoutePolicy;

    protected BaseSyncSenderRouteBuilder(
      List<RouteConfigurationModule> modules,
      FailContractRoutePolicy failureContractRoutePolicy) {
        super(modules);
        this.failureContractRoutePolicy = failureContractRoutePolicy;
    }

    @Override
    public final FailContractRoutePolicy failureContractRoutePolicy() {
        return failureContractRoutePolicy;
    }
}
