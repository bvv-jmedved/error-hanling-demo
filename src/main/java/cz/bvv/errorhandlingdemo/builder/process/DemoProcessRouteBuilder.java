package cz.bvv.errorhandlingdemo.builder.process;

import cz.bvv.errorhandlingdemo.builder.base.BaseProcessRouteBuilder;
import cz.bvv.errorhandlingdemo.poc.FailureStep;
import cz.bvv.errorhandlingdemo.poc.FailureStepProcessorFactory;
import cz.bvv.errorhandlingdemo.policy.route.RouteConfigurationModule;
import cz.bvv.errorhandlingdemo.policy.route.logging.annotation.Monitored;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Concrete route definition used in the error-handling architecture walkthrough.
 *
 * <p>The class intentionally focuses on Camel DSL only and delegates cross-cutting behavior
 * (logging, failure-contract mapping, monitoring status) to policy modules.
 */
@Component
@Monitored(service = "process-demo")
public class DemoProcessRouteBuilder extends BaseProcessRouteBuilder {
  private final FailureStepProcessorFactory failureStepProcessorFactory;

  public DemoProcessRouteBuilder(
      List<RouteConfigurationModule> modules,
      FailureStepProcessorFactory failureStepProcessorFactory) {
    super(modules);
    this.failureStepProcessorFactory = failureStepProcessorFactory;
  }

  @Override
  protected void configureRoutes() {

    from("direct:demo-process")
        .routeId("demo-process")
        .process(failureStepProcessorFactory.forStep(FailureStep.PROCESS_TRANSFORM_REQUEST))
        .to("direct:demo-receiver?exchangePattern=InOut")
        .process(failureStepProcessorFactory.forStep(FailureStep.PROCESS_TRANSFORM_RESPONSE));
  }
}
