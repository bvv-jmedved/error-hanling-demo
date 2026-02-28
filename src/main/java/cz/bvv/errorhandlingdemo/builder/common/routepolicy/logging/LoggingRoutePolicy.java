package cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging;

import lombok.Getter;
import org.apache.camel.Route;
import org.apache.camel.support.RoutePolicySupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class LoggingRoutePolicy extends RoutePolicySupport {
    private static final Logger LOG = LoggerFactory.getLogger(LoggingRoutePolicy.class);

    private final String service;
    private final boolean critical;

    public LoggingRoutePolicy(String service, boolean critical) {
        this.service = service;
        this.critical = critical;
    }

    @Override
    public void onStart(Route route) {
        LOG.info(
          "route_policy=monitor event=start routeId={} service={} critical={}",
          route.getRouteId(),
          service,
          critical
        );
    }

    @Override
    public void onStop(Route route) {
        LOG.info(
          "route_policy=monitor event=stop routeId={} service={} critical={}",
          route.getRouteId(),
          service,
          critical
        );
    }
}
