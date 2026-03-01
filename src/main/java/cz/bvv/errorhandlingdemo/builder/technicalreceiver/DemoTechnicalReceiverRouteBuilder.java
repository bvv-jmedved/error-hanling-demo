package cz.bvv.errorhandlingdemo.builder.technicalreceiver;

import cz.bvv.errorhandlingdemo.builder.base.BaseTechnicalReceiverRouteBuilder;
import cz.bvv.errorhandlingdemo.support.exchange.IntegrationExchangeProperties;
import cz.bvv.errorhandlingdemo.policy.route.RouteConfigurationModule;
import cz.bvv.errorhandlingdemo.policy.route.logging.Monitored;
import cz.bvv.errorhandlingdemo.poc.FailureStepProcessorFactory;
import cz.bvv.errorhandlingdemo.poc.FailureStep;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Concrete route definition used in the error-handling architecture walkthrough.
 *
 * <p>The class intentionally focuses on Camel DSL only and delegates cross-cutting behavior
 * (logging, failure-contract mapping, monitoring status) to policy modules.
 */
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
