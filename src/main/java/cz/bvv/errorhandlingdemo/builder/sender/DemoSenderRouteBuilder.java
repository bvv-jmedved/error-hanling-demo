package cz.bvv.errorhandlingdemo.builder.sender;

import cz.bvv.errorhandlingdemo.builder.common.rest.BaseRestSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.poc.FailureInjector;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DemoSenderRouteBuilder extends BaseRestSenderRouteBuilder {
    private final ObjectProvider<FailureInjector> senderValidateFailureInjectorObjectProvider;

    protected DemoSenderRouteBuilder(
      ObjectProvider<DemoFailureContractRoutePolicy> failureContractRoutePolicyObjectProvider,
      @Qualifier("senderValidateFailureInjector")
      ObjectProvider<FailureInjector> senderValidateFailureInjectorObjectProvider) {
        super(failureContractRoutePolicyObjectProvider);
        this.senderValidateFailureInjectorObjectProvider = senderValidateFailureInjectorObjectProvider;
    }

    @Override
    public void config() {

        from("direct:demo-sender")
          .routeId("demo-sender")
          .process(exchange -> senderValidateFailureInjectorObjectProvider.ifAvailable(
            failureInjector -> failureInjector.process(exchange)))
          .log("Sender. Validating authorizationSending message: ${body}")
          .log("Sender. Unmarshalling request: ${body}")
          .to("direct:demo-process?exchangePattern=InOut")
          .log("Sender. Marshalling response: ${body}");
    }
}
