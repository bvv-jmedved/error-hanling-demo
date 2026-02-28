package cz.bvv.errorhandlingdemo.builder.sender.demo2rest;

import cz.bvv.errorhandlingdemo.builder.common.rest.BaseRestSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.common.rest.DefaultRestFailureContractRoutePolicy;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.RouteConfigurationModule;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging.Monitored;
import cz.bvv.errorhandlingdemo.poc.FailureStepProcessorFactory;
import cz.bvv.errorhandlingdemo.poc.FailureStep;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
@Monitored(service = "sender-demo2-rest")
public class Demo2RestSenderRouteBuilder extends BaseRestSenderRouteBuilder {
    private final FailureStepProcessorFactory failureStepProcessorFactory;

    protected Demo2RestSenderRouteBuilder(List<RouteConfigurationModule> modules,
      DefaultRestFailureContractRoutePolicy failureContractRoutePolicy,
      FailureStepProcessorFactory failureStepProcessorFactory) {
        super(modules, failureContractRoutePolicy);
        this.failureStepProcessorFactory = failureStepProcessorFactory;
    }

    @Override
    public void configureRoutes() {
        from("direct:demo2-sender")
          .routeId("demo2-sender")
          .process(failureStepProcessorFactory.forStep(FailureStep.SENDER_VALIDATE))
          .process(failureStepProcessorFactory.forStep(FailureStep.SENDER_AUTH))
          .to("direct:demo-process?exchangePattern=InOut");
    }
}
