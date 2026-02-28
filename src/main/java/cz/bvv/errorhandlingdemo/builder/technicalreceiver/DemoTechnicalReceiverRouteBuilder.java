package cz.bvv.errorhandlingdemo.builder.technicalreceiver;

import cz.bvv.errorhandlingdemo.builder.common.BaseTechnicalReceiverRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.common.IntegrationExchangeProperties;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.RouteConfigurationModule;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging.Monitored;
import cz.bvv.errorhandlingdemo.poc.FailureStepProcessorFactory;
import cz.bvv.errorhandlingdemo.poc.FailureStep;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
@Monitored(service = "technical-receiver-demo")
public class DemoTechnicalReceiverRouteBuilder extends BaseTechnicalReceiverRouteBuilder {
    private final FailureStepProcessorFactory failureStepProcessorFactory;

    public DemoTechnicalReceiverRouteBuilder(List<RouteConfigurationModule> modules,
      FailureStepProcessorFactory failureStepProcessorFactory) {
        super(modules);
        this.failureStepProcessorFactory = failureStepProcessorFactory;
    }

    @Override
    protected void configureRoutes() {
        // Intentionally disabled so technical exceptions propagate to the receiver route,
        // where retry/termination policy is defined and fail-contract mapping is applied.
        errorHandler(noErrorHandler());

        from("direct:technical-receiver")
          .routeId("technical-receiver")
          .process(failureStepProcessorFactory.forStep(FailureStep.TECHNICAL_CALL))
          .choice()
          .when(exchangeProperty(IntegrationExchangeProperties.POC_HTTP_RESPONSE_BODY_SET).isNotEqualTo(true))
          .setBody(constant("{\"status\":\"ok\"}"))
          .end();

    }
}
