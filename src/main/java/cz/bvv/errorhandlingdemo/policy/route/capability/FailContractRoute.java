package cz.bvv.errorhandlingdemo.policy.route.capability;

import cz.bvv.errorhandlingdemo.policy.route.FailContractRoutePolicy;

/**
 * Capability marker/provider used by policy modules to decide whether infrastructure
 * should be applied to a given {@code RouteBuilder}.
 */
public interface FailContractRoute {
    /**
     * Provides route-specific fail-contract policy instance.
     *
     * @return fail-contract policy to be attached by module orchestration
     */
    FailContractRoutePolicy failureContractRoutePolicy();
}
