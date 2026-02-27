package cz.bvv.errorhandlingdemo.builder.common.soap;

import cz.bvv.errorhandlingdemo.builder.common.BaseSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.common.failure.FailureContractRoutePolicy;

public abstract class BaseSoapSenderRouteBuilder extends BaseSenderRouteBuilder {
    private final FailureContractRoutePolicy failureContractRoutePolicy;

    protected BaseSoapSenderRouteBuilder(
      FailureContractRoutePolicy failureContractRoutePolicy) {
        this.failureContractRoutePolicy = failureContractRoutePolicy;
    }

    @Override
    public void configure() {
        super.configure();
        getRouteCollection().getRoutes().forEach(route -> route.routePolicy(failureContractRoutePolicy));
    }
}
