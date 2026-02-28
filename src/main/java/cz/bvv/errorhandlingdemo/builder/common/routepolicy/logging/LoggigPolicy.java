package cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging;

/**
 * Compatibility alias for typo-based references to LoggigPolicy.
 */
public class LoggigPolicy extends LoggingRoutePolicy {
    public LoggigPolicy(String service, boolean critical) {
        super(service, critical);
    }
}
