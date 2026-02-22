package cz.bvv.errorhandlingdemo.builder.process;

import cz.bvv.errorhandlingdemo.builder.common.BaseProcessRouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class DemoProcessRouteBuilder extends BaseProcessRouteBuilder {
    @Override
    protected void config() {

        from("direct:demo-process")
          .routeId("demo-process")
          .log("Processor. Processing request: ${body}")
          .to("direct:demo-receiver?exchangePattern=InOut")
          .log("Processor. Processing response: ${body}");

    }

  }

