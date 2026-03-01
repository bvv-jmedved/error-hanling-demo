package cz.bvv.errorhandlingdemo.policy.route;

import org.apache.camel.builder.RouteBuilder;

/**
 * Core policy contract for module-based route infrastructure composition.
 *
 * <p>This API defines how architecture behavior is attached to routes after DSL creation.
 */
public interface RouteConfigurationModule {
    /**
     * Determines whether this module should be applied to the given builder.
     *
     * @param builder route builder instance currently being configured
     * @return {@code true} when module behavior is applicable
     */
    boolean supports(RouteBuilder builder);

    /**
     * Applies module infrastructure after route DSL has been fully created.
     *
     * @param builder route builder with created route definitions
     */
    void afterRoutes(RouteBuilder builder);
}
