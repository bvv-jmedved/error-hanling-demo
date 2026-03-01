package cz.bvv.errorhandlingdemo.builder.base;

import cz.bvv.errorhandlingdemo.policy.route.RouteConfigurationModule;
import java.util.List;

/**
 * Base type for technical ingress sender routes.
 *
 * <p>Technical sender routes expose concrete transport entry points (REST, SOAP, FTP)
 * and delegate business orchestration to sender/process/receiver routes.
 */
public abstract class BaseTechnicalSenderRouteBuilder extends BaseRouteBuilder {
    protected BaseTechnicalSenderRouteBuilder(List<RouteConfigurationModule> modules) {
        super(modules);
    }
}
