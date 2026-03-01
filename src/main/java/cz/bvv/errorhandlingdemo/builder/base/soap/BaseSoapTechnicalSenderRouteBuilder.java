package cz.bvv.errorhandlingdemo.builder.base.soap;

import cz.bvv.errorhandlingdemo.builder.base.BaseTechnicalSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.policy.route.RouteConfigurationModule;
import java.util.List;

/**
 * Base class for SOAP technical ingress routes.
 *
 * <p>This layer hosts transport endpoint specifics and forwards requests to business sender routes.
 */
public abstract class BaseSoapTechnicalSenderRouteBuilder extends BaseTechnicalSenderRouteBuilder {
  protected BaseSoapTechnicalSenderRouteBuilder(List<RouteConfigurationModule> modules) {
    super(modules);
  }
}
