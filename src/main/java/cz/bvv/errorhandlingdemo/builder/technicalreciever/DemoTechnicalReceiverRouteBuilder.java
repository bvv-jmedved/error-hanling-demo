package cz.bvv.errorhandlingdemo.builder.technicalreciever;

import cz.bvv.errorhandlingdemo.builder.common.BaseTechnicalReceiverRouteBuilder;
import cz.bvv.errorhandlingdemo.poc.FailureStep;
import org.springframework.stereotype.Component;

@Component
public class DemoTechnicalReceiverRouteBuilder extends BaseTechnicalReceiverRouteBuilder {
    @Override
    protected void config() {
        errorHandler(noErrorHandler());

        from("direct:technical-receiver")
          .routeId("technical-receiver")
          .process(pocStep(FailureStep.TECHNICAL_CALL))
          .setBody(constant("{\"status\":\"ok\"}"));

    }
}
