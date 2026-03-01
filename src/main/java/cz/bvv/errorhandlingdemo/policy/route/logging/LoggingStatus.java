package cz.bvv.errorhandlingdemo.policy.route.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoggingStatus {
    ExchangeLoggingStatus onStart() default ExchangeLoggingStatus.RUNNING;

    ExchangeLoggingStatus onComplete() default ExchangeLoggingStatus.RUNNING;

    ExchangeLoggingStatus onFailure() default ExchangeLoggingStatus.FAILURE;
}
