package cz.bvv.errorhandlingdemo.policy.route.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Overrides default status lifecycle emitted by {@link LoggingRoutePolicy}.
 *
 * <p>Use this annotation on ingress/terminal routes when default
 * {@code RUNNING/RUNNING/FAILURE} semantics should be replaced by terminal
 * status markers such as {@code STARTED/COMPLETED/FAILED}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoggingStatus {
    /**
     * Status emitted when exchange processing begins.
     */
    ExchangeLoggingStatus onStart() default ExchangeLoggingStatus.RUNNING;

    /**
     * Status emitted when exchange processing ends successfully.
     */
    ExchangeLoggingStatus onComplete() default ExchangeLoggingStatus.RUNNING;

    /**
     * Status emitted when exchange processing ends with failure.
     */
    ExchangeLoggingStatus onFailure() default ExchangeLoggingStatus.FAILURE;
}
