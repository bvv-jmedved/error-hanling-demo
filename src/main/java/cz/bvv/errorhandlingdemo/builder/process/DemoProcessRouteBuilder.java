package cz.bvv.errorhandlingdemo.builder.process;

import cz.bvv.errorhandlingdemo.builder.common.BaseProcessRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.RouteConfigurationModule;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging.Monitored;
import cz.bvv.errorhandlingdemo.poc.FailureStepProcessorFactory;
import cz.bvv.errorhandlingdemo.poc.FailureStep;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
@Monitored(service = "process-demo")
public class DemoProcessRouteBuilder extends BaseProcessRouteBuilder {
    private final FailureStepProcessorFactory failureStepProcessorFactory;

    public DemoProcessRouteBuilder(List<RouteConfigurationModule> modules,
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
