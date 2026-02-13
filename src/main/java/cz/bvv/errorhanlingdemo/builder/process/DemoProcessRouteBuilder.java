package cz.bvv.errorhanlingdemo.builder.process;

import cz.bvv.errorhanlingdemo.builder.common.BaseRouteBuilder;
import org.apache.camel.Message;
import org.apache.camel.Processor;
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

//    @Override
//    protected Processor getFailureProcessor() {
//        return exchange -> {
//            Message message = exchange.getIn();
//            message.setBody(simple("[Process completition]: ${body}"));
//        };
//    }
}
