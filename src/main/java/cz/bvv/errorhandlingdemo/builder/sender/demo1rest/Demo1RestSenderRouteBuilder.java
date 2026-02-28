package cz.bvv.errorhandlingdemo.builder.sender.demo1rest;

import cz.bvv.errorhandlingdemo.builder.common.rest.BaseRestSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.poc.FailureStep;
import org.springframework.stereotype.Component;

@Component
public class Demo1RestSenderRouteBuilder extends BaseRestSenderRouteBuilder {
    protected Demo1RestSenderRouteBuilder(
      Demo1RestFailureContractRoutePolicy failureContractRoutePolicy) {
        super(failureContractRoutePolicy);
    }

    @Override
    public void configureRoutes() {

        from("direct:demo-sender")
          .routeId("demo-sender")
          .process(pocStep(FailureStep.SENDER_VALIDATE))
          .process(pocStep(FailureStep.SENDER_AUTH))
          .to("direct:demo-process?exchangePattern=InOut");
    }
}
