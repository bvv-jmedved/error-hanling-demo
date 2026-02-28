package cz.bvv.errorhandlingdemo.builder.common.soap;

import cz.bvv.errorhandlingdemo.builder.common.BaseSyncSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.common.failure.FailureContractRoutePolicy;

public abstract class BaseSoapSenderRouteBuilder extends BaseSyncSenderRouteBuilder {
    protected BaseSoapSenderRouteBuilder(FailureContractRoutePolicy failureContractRoutePolicy) {
        super(failureContractRoutePolicy);
    }
}
