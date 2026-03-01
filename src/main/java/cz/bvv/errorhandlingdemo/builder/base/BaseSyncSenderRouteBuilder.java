package cz.bvv.errorhandlingdemo.builder.base;

import cz.bvv.errorhandlingdemo.policy.route.FailContractRoutePolicy;
import cz.bvv.errorhandlingdemo.policy.route.RouteConfigurationModule;
import cz.bvv.errorhandlingdemo.policy.route.capability.FailContractRoute;
import java.util.List;

public abstract class BaseSyncSenderRouteBuilder extends BaseSenderRouteBuilder implements FailContractRoute {
    private final FailContractRoutePolicy failureContractRoutePolicy;

    protected BaseSyncSenderRouteBuilder(List<RouteConfigurationModule> modules,
      FailContractRoutePolicy failureContractRoutePolicy) {
        super(modules);
        this.failureContractRoutePolicy = failureContractRoutePolicy;
    }

    @Override
    public final FailContractRoutePolicy failureContractRoutePolicy() {
        return failureContractRoutePolicy;
    }
}
