package cz.bvv.errorhandlingdemo.builder.sender.demoftp;

import cz.bvv.errorhandlingdemo.builder.base.BaseSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.policy.route.RouteConfigurationModule;
import cz.bvv.errorhandlingdemo.policy.route.logging.Monitored;
import cz.bvv.errorhandlingdemo.poc.FailureStepProcessorFactory;
import cz.bvv.errorhandlingdemo.poc.FailureStep;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
@Monitored(service = "sender-demo-ftp")
public class DemoFtpSenderRouteBuilder extends BaseSenderRouteBuilder {

    private final DemoFtpFailureTriggerParser failureTriggerParser;
    private final FailureStepProcessorFactory failureStepProcessorFactory;

    public DemoFtpSenderRouteBuilder(List<RouteConfigurationModule> modules,
      DemoFtpFailureTriggerParser failureTriggerParser,
      FailureStepProcessorFactory failureStepProcessorFactory) {
        super(modules);
        this.failureTriggerParser = failureTriggerParser;
        this.failureStepProcessorFactory = failureStepProcessorFactory;
    }

    @Override
    protected void configureRoutes() {
        from("direct:demo-ftp-sender")
          .routeId("demo-ftp-sender")
          .process(failureTriggerParser)
          .process(failureStepProcessorFactory.forStep(FailureStep.SENDER_VALIDATE))
          .process(failureStepProcessorFactory.forStep(FailureStep.SENDER_AUTH))
          .to("direct:demo-process?exchangePattern=InOut");
    }
}
