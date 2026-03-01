package cz.bvv.errorhandlingdemo.foundation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FlowDirection {
    String source() default "";

    String target() default "";

    String process() default "";
}
