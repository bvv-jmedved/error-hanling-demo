package cz.bvv.errorhandlingdemo.builder.technicalreceiver;

import cz.bvv.errorhandlingdemo.builder.common.BaseTechnicalReceiverRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.common.IntegrationExchangeProperties;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging.Monitored;
import cz.bvv.errorhandlingdemo.poc.FailureStep;
import org.springframework.stereotype.Component;

@Component
@Monitored(service = "technical-receiver-demo", critical = false)
public class DemoTechnicalReceiverRouteBuilder extends BaseTechnicalReceiverRouteBuilder {
    @Override
    protected void configureRoutes() {
        errorHandler(noErrorHandler());

        from("direct:technical-receiver")
          .routeId("technical-receiver")
          .process(pocStep(FailureStep.TECHNICAL_CALL))
          .choice()
          .when(exchangeProperty(IntegrationExchangeProperties.POC_HTTP_RESPONSE_BODY_SET).isNotEqualTo(true))
          .setBody(constant("{\"status\":\"ok\"}"))
          .end();

    }
}
