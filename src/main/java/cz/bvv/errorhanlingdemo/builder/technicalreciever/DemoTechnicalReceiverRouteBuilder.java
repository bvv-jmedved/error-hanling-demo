package cz.bvv.errorhanlingdemo.builder.technicalreciever;

import cz.bvv.errorhanlingdemo.builder.common.BaseTechnicalReceiverRouteBuilder;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.stereotype.Component;

@Component
public class DemoTechnicalReceiverRouteBuilder extends BaseTechnicalReceiverRouteBuilder {
    @Override
    protected void config() {
        errorHandler(noErrorHandler());

        from("direct:technicalreceiver")
          .routeId("technicalreceiver")
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
