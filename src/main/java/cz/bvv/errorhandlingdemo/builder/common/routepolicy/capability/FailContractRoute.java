package cz.bvv.errorhandlingdemo.builder.common.routepolicy.capability;

import cz.bvv.errorhandlingdemo.builder.common.routepolicy.FailContractRoutePolicy;

public interface FailContractRoute {
    FailContractRoutePolicy failureContractRoutePolicy();
}
