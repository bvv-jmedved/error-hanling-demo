package cz.bvv.errorhanlingdemo.builder.process;

import cz.bvv.errorhanlingdemo.builder.common.BaseProcessRouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class DemoProcessRouteBuilder extends BaseProcessRouteBuilder {
    @Override
    protected void config() {

        from("seda:demo-process")
          .routeId("demo-process")
          .log("Processor. Processing request: ${body}")
          .to("seda:demo-receiver?exchangePattern=InOut")
          .log("Processor. Processing response: ${body}");

    }

  }

