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
          .handled(false);

        from("seda:demo-receiver")
          .routeId("demo-receiver")
          .log("Receiver. Preparing call with request: ${body}")
          .to("direct:technicalreceiver")
          .log("Receiver. Handling response: ${body}")
        ;
    }
}
