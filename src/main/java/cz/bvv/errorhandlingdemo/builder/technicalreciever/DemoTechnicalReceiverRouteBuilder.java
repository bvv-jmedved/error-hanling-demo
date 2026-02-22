package cz.bvv.errorhandlingdemo.builder.technicalreciever;

import cz.bvv.errorhandlingdemo.builder.common.BaseTechnicalReceiverRouteBuilder;
import cz.bvv.errorhandlingdemo.poc.FailureInjector;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DemoTechnicalReceiverRouteBuilder extends BaseTechnicalReceiverRouteBuilder {
    private final ObjectProvider<FailureInjector> technicalCallFailureInjectorObjectProvider;

    public DemoTechnicalReceiverRouteBuilder(
      @Qualifier("technicalCallFailureInjector")
      ObjectProvider<FailureInjector> technicalCallFailureInjectorObjectProvider) {
        this.technicalCallFailureInjectorObjectProvider = technicalCallFailureInjectorObjectProvider;
    }

    @Override
    protected void config() {
        errorHandler(noErrorHandler());

        from("direct:technical-receiver")
          .routeId("technical-receiver")
          .process(exchange -> technicalCallFailureInjectorObjectProvider.ifAvailable(
            failureInjector -> failureInjector.process(exchange)))
          .log("Technical receiver. Calling target system with message: ${body}")
          .throwException(new HttpOperationFailedException(
            "http://fake-receiver",
            500,
            "Something wrong occurred in external system",
            null,
            null,
            "{\"Status\":\"Server Failed\"}"
          ))
          .log("Technical receiver. Cleaning headers")
        ;

    }
}
