package cz.bvv.errorhandlingdemo.builder.sender.demo2rest;

import cz.bvv.errorhandlingdemo.builder.common.rest.BaseRestSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.common.rest.DefaultRestFailureContractRoutePolicy;
import cz.bvv.errorhandlingdemo.poc.FailureStep;
import org.springframework.stereotype.Component;

@Component
public class Demo2RestSenderRouteBuilder extends BaseRestSenderRouteBuilder {
    protected Demo2RestSenderRouteBuilder(
      DefaultRestFailureContractRoutePolicy failureContractRoutePolicy) {
        super(failureContractRoutePolicy);
    }

    @Override
    public void config() {
        from("direct:demo2-sender")
          .routeId("demo2-sender")
          .process(pocStep(FailureStep.SENDER_VALIDATE))
          .process(pocStep(FailureStep.SENDER_AUTH))
          .to("direct:demo-process?exchangePattern=InOut");
    }
}
