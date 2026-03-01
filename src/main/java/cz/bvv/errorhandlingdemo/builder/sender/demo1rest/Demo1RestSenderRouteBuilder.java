package cz.bvv.errorhandlingdemo.builder.sender.demo1rest;

import cz.bvv.errorhandlingdemo.builder.base.rest.BaseRestSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.policy.route.RouteConfigurationModule;
import cz.bvv.errorhandlingdemo.policy.route.logging.Monitored;
import cz.bvv.errorhandlingdemo.poc.FailureStepProcessorFactory;
import cz.bvv.errorhandlingdemo.poc.FailureStep;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
@Monitored(service = "sender-demo1-rest")
public class Demo1RestSenderRouteBuilder extends BaseRestSenderRouteBuilder {
    private final FailureStepProcessorFactory failureStepProcessorFactory;

    protected Demo1RestSenderRouteBuilder(List<RouteConfigurationModule> modules,
      Demo1RestFailureContractRoutePolicy failureContractRoutePolicy,
      FailureStepProcessorFactory failureStepProcessorFactory) {
        super(modules, failureContractRoutePolicy);
        this.failureStepProcessorFactory = failureStepProcessorFactory;
    }

    @Override
    public void configureRoutes() {

        from("direct:demo-sender")
          .routeId("demo-sender")
          .process(failureStepProcessorFactory.forStep(FailureStep.SENDER_VALIDATE))
          .process(failureStepProcessorFactory.forStep(FailureStep.SENDER_AUTH))
          .to("direct:demo-process?exchangePattern=InOut");
    }
}
