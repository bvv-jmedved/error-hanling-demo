package cz.bvv.errorhanlingdemo.builder.receiver;

import cz.bvv.errorhanlingdemo.builder.common.BaseRouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class DemoReceiverRouteBuilder extends BaseRouteBuilder {
    @Override
    protected void config() {

        from("seda:demo-receiver")
          .routeId("demo-receiver")
          .log("Calling target system with  message: ${body}")
          .process(e -> {
              throw new RuntimeException("Simulated exception");
          })
          .setBody(constant("ResponseL"))
        ;
    }
}
