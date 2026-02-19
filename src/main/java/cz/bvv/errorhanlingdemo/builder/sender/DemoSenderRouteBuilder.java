package cz.bvv.errorhanlingdemo.builder.sender;

import cz.bvv.errorhanlingdemo.builder.common.BaseSenderRouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class DemoSenderRouteBuilder extends BaseSenderRouteBuilder {

    @Override
    public void config() {

        from("direct:demo-sender")
          .routeId("demo-sender")
          .log("Sender. Validating authorizationSending message: ${body}")
          .log("Sender. Unmarshalling request: ${body}")
          .to("direct:demo-process?exchangePattern=InOut")
          .log("Sender. Marshalling response: ${body}");
    }

}
