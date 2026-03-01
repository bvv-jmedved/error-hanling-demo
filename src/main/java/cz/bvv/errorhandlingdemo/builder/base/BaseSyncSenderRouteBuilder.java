package cz.bvv.errorhandlingdemo.builder.base;

import cz.bvv.errorhandlingdemo.policy.route.FailContractRoutePolicy;
import cz.bvv.errorhandlingdemo.policy.route.RouteConfigurationModule;
import cz.bvv.errorhandlingdemo.policy.route.capability.FailContractRoute;
import java.util.List;

/**
 * Base sender for synchronous integrations that must expose a failure contract.
 *
 * <p>By implementing {@link FailContractRoute}, this type guarantees that the fail-contract policy
 * is always available for module-based route composition.
 */
public abstract class BaseSyncSenderRouteBuilder extends BaseSenderRouteBuilder
    implements FailContractRoute {
  private final FailContractRoutePolicy failureContractRoutePolicy;

  protected BaseSyncSenderRouteBuilder(
      List<RouteConfigurationModule> modules, FailContractRoutePolicy failureContractRoutePolicy) {
    super(modules);
    this.failureContractRoutePolicy = failureContractRoutePolicy;
  }

  /**
   * Provides the fail-contract route policy required by {@link FailContractRoute}.
   *
   * @return policy responsible for mapping failed exchanges to transport contracts
   */
  @Override
  public final FailContractRoutePolicy failureContractRoutePolicy() {
    return failureContractRoutePolicy;
  }
}
