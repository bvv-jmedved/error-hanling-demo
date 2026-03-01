package cz.bvv.errorhandlingdemo.policy.route.capability;

import cz.bvv.errorhandlingdemo.policy.route.FailContractRoutePolicy;

public interface FailContractRoute {
    FailContractRoutePolicy failureContractRoutePolicy();
}
