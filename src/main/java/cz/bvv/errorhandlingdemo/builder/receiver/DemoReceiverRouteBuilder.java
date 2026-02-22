package cz.bvv.errorhandlingdemo.builder.receiver;

import cz.bvv.errorhandlingdemo.builder.common.BaseReceiverRouteBuilder;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.stereotype.Component;

@Component
public class DemoReceiverRouteBuilder extends BaseReceiverRouteBuilder {
    @Override
    protected void config() {
        onException(HttpOperationFailedException.class)
          .maximumRedeliveries(2).redeliveryDelay(0)
          .onRedelivery( exchange -> System.out.println("Receiver. Getting new token"))
          .process( exchange ->
            System.out.println("Receiver. Processing exception: " + exchange.getException()))
          .handled(false);

        from("direct:demo-receiver")
          .routeId("demo-receiver")
          .to("direct:technical-receiver");
    }
}
