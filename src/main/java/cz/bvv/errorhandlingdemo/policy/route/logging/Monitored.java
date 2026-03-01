package cz.bvv.errorhandlingdemo.policy.route.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Monitored {
    /**
     * Convention for production RouteBuilders:
     * - use stable lowercase kebab-case service identifiers,
     * - keep them aligned with the primary route role/process naming.
     */
    String service() default "";

    /**
     * Mark externally exposed ingress routes as critical. Internal orchestration routes stay non-critical.
     */
    boolean critical() default false;
}
