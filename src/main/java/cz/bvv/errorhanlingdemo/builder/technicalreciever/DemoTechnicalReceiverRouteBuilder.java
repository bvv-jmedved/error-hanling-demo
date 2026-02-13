package cz.bvv.errorhanlingdemo.builder.technicalreciever;

import cz.bvv.errorhanlingdemo.builder.common.BaseRouteBuilder;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.stereotype.Component;

@Component
public class DemoTechnicalReceiverRouteBuilder extends BaseRouteBuilder {
    @Override
    protected void config() {
        errorHandler(noErrorHandler());

        from("direct:technicalreceiver")
          .routeId("technicalreceiver")
          .log("Technical receiver called with message: ${body}")
          .throwException(new HttpOperationFailedException(
            "http://fake-receiver",
            400,
            "Internal server error",
            null,
            null,
            "{\"Status\":\"Server Failed\"}"
          ));

    }
}
