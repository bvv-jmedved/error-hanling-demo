package cz.bvv.errorhandlingdemo.builder.sender;

import cz.bvv.errorhandlingdemo.builder.common.rest.BaseRestSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.poc.FailureStep;
import org.springframework.stereotype.Component;

@Component
public class DemoSenderRouteBuilder extends BaseRestSenderRouteBuilder {
    protected DemoSenderRouteBuilder(DemoFailureContractRoutePolicy failureContractRoutePolicy) {
        super(failureContractRoutePolicy);
    }

    @Override
    public void config() {

        from("direct:demo-sender")
          .routeId("demo-sender")
          .process(pocStep(FailureStep.SENDER_VALIDATE))
          .log("Sender. Validating authorizationSending message: ${body}")
          .log("Sender. Unmarshalling request: ${body}")
          .to("direct:demo-process?exchangePattern=InOut")
          .log("Sender. Marshalling response: ${body}");
    }
}
