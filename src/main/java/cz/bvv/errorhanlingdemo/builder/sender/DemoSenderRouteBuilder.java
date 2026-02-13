package cz.bvv.errorhanlingdemo.builder.sender;

import cz.bvv.errorhanlingdemo.builder.common.BaseRouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class DemoSenderRouteBuilder extends BaseRouteBuilder {

    @Override
    public void config() {

        from("seda:demo-sender")
          .routeId("demo-sender")
          .log("Sending message: ${body}")
          .to("seda:demo-process?exchangePattern=InOut")
          .log("Sender received response message: ${body}");
    }

}
