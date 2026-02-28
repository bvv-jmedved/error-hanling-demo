package cz.bvv.errorhandlingdemo.builder.common.rest;

import cz.bvv.errorhandlingdemo.builder.common.BaseSyncSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.common.failure.FailureContractRoutePolicy;

public abstract class BaseRestSenderRouteBuilder extends BaseSyncSenderRouteBuilder {
    protected BaseRestSenderRouteBuilder(FailureContractRoutePolicy failureContractRoutePolicy) {
        super(failureContractRoutePolicy);
    }
}
