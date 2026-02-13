package cz.bvv.errorhanlingdemo.builder.receiver;

import cz.bvv.errorhanlingdemo.builder.common.BaseReceiverRouteBuilder;
import cz.bvv.errorhanlingdemo.builder.common.BaseRouteBuilder;
import java.io.IOException;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.stereotype.Component;

@Component
public class DemoReceiverRouteBuilder extends BaseReceiverRouteBuilder {
    @Override
    protected void config() {
        onException(HttpOperationFailedException.class)
          .maximumRedeliveries(2).redeliveryDelay(0)
          .handled(false);
        ;

        from("seda:demo-receiver")
          .routeId("demo-receiver")
          .log("Calling target system with  message: ${body}")
          .to("direct:technicalreceiver")
        ;
    }
}
