package cz.bvv.errorhandlingdemo.builder.technicalreciever;

import cz.bvv.errorhandlingdemo.builder.common.BaseTechnicalReceiverRouteBuilder;
import cz.bvv.errorhandlingdemo.poc.FailureStep;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.stereotype.Component;

@Component
public class DemoTechnicalReceiverRouteBuilder extends BaseTechnicalReceiverRouteBuilder {
    @Override
    protected void config() {
        errorHandler(noErrorHandler());

        from("direct:technical-receiver")
          .routeId("technical-receiver")
          .process(pocStep(FailureStep.TECHNICAL_CALL))
          .throwException(new HttpOperationFailedException(
            "http://fake-receiver",
            500,
            "Something wrong occurred in external system",
            null,
            null,
            "{\"Status\":\"Server Failed\"}"
          ));

    }
}
