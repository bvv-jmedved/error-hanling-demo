package cz.bvv.errorhanlingdemo.builder.receiver;

import cz.bvv.errorhanlingdemo.builder.common.BaseReceiverRouteBuilder;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.stereotype.Component;

@Component
public class DemoReceiverRouteBuilder extends BaseReceiverRouteBuilder {
    @Override
    protected void config() {
        onException(HttpOperationFailedException.class)
          .maximumRedeliveries(2).redeliveryDelay(0)
          .onRedelivery( exchange -> System.out.println("Receiver. Getting new token"))
          .log("Redelivery failied")
          .process( exchange ->
            System.out.println("Receiver. Processing exception: " + exchange.getException()))
          .handled(false);

        from("direct:demo-receiver")
          .routeId("demo-receiver")
          .log("Receiver. Preparing call with request: ${body}")
          .to("direct:technicalreceiver")
          .log("Receiver. Handling response: ${body}");
    }
}
