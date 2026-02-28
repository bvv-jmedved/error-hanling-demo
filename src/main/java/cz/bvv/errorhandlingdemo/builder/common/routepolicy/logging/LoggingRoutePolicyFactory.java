package cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class LoggingRoutePolicyFactory {

    public LoggingRoutePolicy create(RouteBuilder builder, Monitored monitored) {
        String defaultService = builder.getClass().getSimpleName();
        if (monitored == null) {
            return new LoggingRoutePolicy(defaultService, false);
        }

        String service = StringUtils.hasText(monitored.service()) ? monitored.service() : defaultService;
        return new LoggingRoutePolicy(service, monitored.critical());
    }
}
