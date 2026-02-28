package cz.bvv.errorhandlingdemo.builder.process;

import cz.bvv.errorhandlingdemo.builder.common.BaseProcessRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging.Monitored;
import cz.bvv.errorhandlingdemo.poc.FailureStep;
import org.springframework.stereotype.Component;

@Component
@Monitored(service = "process-demo", critical = false)
public class DemoProcessRouteBuilder extends BaseProcessRouteBuilder {
    @Override
    protected void configureRoutes() {

        from("direct:demo-process")
          .routeId("demo-process")
          .process(pocStep(FailureStep.PROCESS_TRANSFORM_REQUEST))
          .to("direct:demo-receiver?exchangePattern=InOut")
          .process(pocStep(FailureStep.PROCESS_TRANSFORM_RESPONSE));

    }

  }

