package cz.bvv.errorhandlingdemo.builder.sender.demo1rest;

import cz.bvv.errorhandlingdemo.builder.common.rest.BaseRestSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.RouteConfigurationModule;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging.Monitored;
import cz.bvv.errorhandlingdemo.poc.FailureStep;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
@Monitored(service = "sender-demo1-rest")
public class Demo1RestSenderRouteBuilder extends BaseRestSenderRouteBuilder {
    protected Demo1RestSenderRouteBuilder(
      List<RouteConfigurationModule> modules,
      Demo1RestFailureContractRoutePolicy failureContractRoutePolicy) {
        super(modules, failureContractRoutePolicy);
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
