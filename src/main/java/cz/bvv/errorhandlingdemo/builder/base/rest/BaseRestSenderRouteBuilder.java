package cz.bvv.errorhandlingdemo.builder.base.rest;

import cz.bvv.errorhandlingdemo.builder.base.BaseSyncSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.policy.route.FailContractRoutePolicy;
import cz.bvv.errorhandlingdemo.policy.route.RouteConfigurationModule;
import java.util.List;

/**
 * Base class for synchronous REST sender routes.
 *
 * <p>REST senders always include fail-contract policy support through {@code
 * BaseSyncSenderRouteBuilder}.
 */
public abstract class BaseRestSenderRouteBuilder extends BaseSyncSenderRouteBuilder {
  protected BaseRestSenderRouteBuilder(
      List<RouteConfigurationModule> modules, FailContractRoutePolicy failureContractRoutePolicy) {
    super(modules, failureContractRoutePolicy);
  }
}
