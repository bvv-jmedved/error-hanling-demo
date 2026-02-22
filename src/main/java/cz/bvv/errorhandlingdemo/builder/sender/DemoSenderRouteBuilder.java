package cz.bvv.errorhandlingdemo.builder.sender;

import cz.bvv.errorhandlingdemo.builder.common.rest.BaseRestSenderRouteBuilder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Component
public class DemoSenderRouteBuilder extends BaseRestSenderRouteBuilder {

    protected DemoSenderRouteBuilder(
      ObjectProvider<DemoFailureContractRoutePolicy> failureContractRoutePolicyObjectProvider) {
        super(failureContractRoutePolicyObjectProvider);
    }

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
