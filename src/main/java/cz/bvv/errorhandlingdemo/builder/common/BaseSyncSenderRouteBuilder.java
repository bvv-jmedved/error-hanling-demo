package cz.bvv.errorhandlingdemo.builder.common;

import cz.bvv.errorhandlingdemo.builder.common.routepolicy.FailContractRoutePolicy;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.capability.FailContractRoute;

public abstract class BaseSyncSenderRouteBuilder extends BaseSenderRouteBuilder implements FailContractRoute {
    private final FailContractRoutePolicy failureContractRoutePolicy;

    protected BaseSyncSenderRouteBuilder(FailContractRoutePolicy failureContractRoutePolicy) {
        this.failureContractRoutePolicy = failureContractRoutePolicy;
    }

    @Override
    public final FailContractRoutePolicy failureContractRoutePolicy() {
        return failureContractRoutePolicy;
    }
}
