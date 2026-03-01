package cz.bvv.errorhandlingdemo.policy.route;

import org.apache.camel.spi.RoutePolicy;

/**
 * Marker interface for route policies that map failed exchanges to transport-facing contracts.
 *
 * <p>Used by {@code FailContractRoute} capability and {@code FailContractModule}
 * to enforce deterministic fail-contract policy wiring.
 */
public interface FailContractRoutePolicy extends RoutePolicy {
}
