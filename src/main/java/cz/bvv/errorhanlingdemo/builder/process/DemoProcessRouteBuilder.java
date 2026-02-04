package cz.bvv.errorhanlingdemo.builder.process;

import cz.bvv.errorhanlingdemo.builder.common.BaseRouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class DemoProcessRouteBuilder extends BaseRouteBuilder {
    @Override
    protected void config() {

        from("seda:demo-process")
          .routeId("demo-process")
          .log("Processing message: ${body}")
          .to("seda:demo-receiver?exchangePattern=InOut");
    }
}
