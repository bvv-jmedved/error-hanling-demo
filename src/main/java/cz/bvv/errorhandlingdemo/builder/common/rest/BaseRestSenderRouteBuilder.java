package cz.bvv.errorhandlingdemo.builder.common.rest;

import cz.bvv.errorhandlingdemo.builder.common.BaseSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.common.failure.FailureContractRoutePolicy;
import org.springframework.beans.factory.ObjectProvider;

public abstract class BaseRestSenderRouteBuilder extends BaseSenderRouteBuilder {
    private final ObjectProvider<? extends FailureContractRoutePolicy>
      failureContractRoutePolicyObjectProvider;

    protected BaseRestSenderRouteBuilder(
      ObjectProvider<? extends FailureContractRoutePolicy> failureContractRoutePolicyObjectProvider) {
        this.failureContractRoutePolicyObjectProvider = failureContractRoutePolicyObjectProvider;
    }

    @Override
    public void configure() {
        super.configure();

        getRouteCollection().getRoutes().forEach(route -> route.routePolicy(failureContractRoutePolicyObjectProvider.getObject()));
    }
}
