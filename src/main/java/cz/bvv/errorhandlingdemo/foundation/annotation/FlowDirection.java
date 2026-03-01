package cz.bvv.errorhandlingdemo.foundation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares stable business flow metadata for monitoring.
 *
 * <p>When present on a {@code RouteBuilder}, values are projected into exchange headers/properties
 * and emitted by logging policies in canonical process-status logs.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FlowDirection {
    /**
     * Code of the source system that initiated the business flow.
     */
    String source() default "";

    /**
     * Code of the target system that receives business output.
     */
    String target() default "";

    /**
     * End-to-end business process code (for example {@code create-order}).
     */
    String process() default "";
}
