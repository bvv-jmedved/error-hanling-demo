package cz.bvv.errorhandlingdemo.builder.common.rest;

import cz.bvv.errorhandlingdemo.builder.common.BaseSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.common.failure.FailureContractRoutePolicy;

public abstract class BaseRestSenderRouteBuilder extends BaseSenderRouteBuilder {
    private final FailureContractRoutePolicy failureContractRoutePolicy;

    protected BaseRestSenderRouteBuilder(
      FailureContractRoutePolicy failureContractRoutePolicy) {
        this.failureContractRoutePolicy = failureContractRoutePolicy;
    }

    @Override
    public void configure() {
        super.configure();

        getRouteCollection().getRoutes().forEach(route -> route.routePolicy(failureContractRoutePolicy));
    }
}
