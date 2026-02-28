package cz.bvv.errorhandlingdemo.builder.common;

import cz.bvv.errorhandlingdemo.builder.common.failure.FailureContractRoutePolicy;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.capability.FailContractRoute;

public abstract class BaseSyncSenderRouteBuilder extends BaseSenderRouteBuilder implements FailContractRoute {
    private final FailureContractRoutePolicy failureContractRoutePolicy;

    protected BaseSyncSenderRouteBuilder(FailureContractRoutePolicy failureContractRoutePolicy) {
        this.failureContractRoutePolicy = failureContractRoutePolicy;
    }

    @Override
    public final FailureContractRoutePolicy failureContractRoutePolicy() {
        return failureContractRoutePolicy;
    }
}
