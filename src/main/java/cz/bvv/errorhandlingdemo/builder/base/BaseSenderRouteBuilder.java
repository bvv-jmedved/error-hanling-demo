package cz.bvv.errorhandlingdemo.builder.base;

import cz.bvv.errorhandlingdemo.policy.route.RouteConfigurationModule;
import java.util.List;

/**
 * Base type for sender-side business routes.
 *
 * <p>Sender routes usually represent ingress points for a business process.
 */
public abstract class BaseSenderRouteBuilder extends BaseRouteBuilder {
  protected BaseSenderRouteBuilder(List<RouteConfigurationModule> modules) {
    super(modules);
  }
}
