package cz.bvv.errorhanlingdemo.builder.sender;

import cz.bvv.errorhanlingdemo.builder.common.BaseRouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class DemoSender extends BaseRouteBuilder {

    @Override
    public void config() {

        from("scheduler:scheduler?repeatCount=1")
          .routeId("demo-sender")
          .setBody(constant("Demo message"))
          .log("Sending message: ${body}")
          .to("seda:demo-process?exchangePattern=InOut")
          .log("Sender received response message: ${body}");
    }

    @Override
    protected boolean isLast() {
        return true;
    }
}
