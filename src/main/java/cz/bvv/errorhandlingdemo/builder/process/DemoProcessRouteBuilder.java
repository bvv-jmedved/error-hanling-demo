package cz.bvv.errorhandlingdemo.builder.process;

import cz.bvv.errorhandlingdemo.builder.common.BaseProcessRouteBuilder;
import cz.bvv.errorhandlingdemo.poc.FailureStep;
import org.springframework.stereotype.Component;

@Component
public class DemoProcessRouteBuilder extends BaseProcessRouteBuilder {
    @Override
    protected void config() {

        from("direct:demo-process")
          .routeId("demo-process")
          .process(pocStep(FailureStep.PROCESS_TRANSFORM_REQUEST))
          .to("direct:demo-receiver?exchangePattern=InOut")
          .process(pocStep(FailureStep.PROCESS_TRANSFORM_RESPONSE));

    }

  }

