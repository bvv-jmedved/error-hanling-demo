package cz.bvv.errorhandlingdemo.builder.sender.demoftp;

import cz.bvv.errorhandlingdemo.builder.common.BaseSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.RouteConfigurationModule;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging.Monitored;
import cz.bvv.errorhandlingdemo.poc.FailureStep;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
@Monitored(service = "sender-demo-ftp")
public class DemoFtpSenderRouteBuilder extends BaseSenderRouteBuilder {

    private final DemoFtpFailureTriggerParser failureTriggerParser;

    public DemoFtpSenderRouteBuilder(
      List<RouteConfigurationModule> modules,
      DemoFtpFailureTriggerParser failureTriggerParser) {
        super(modules);
        this.failureTriggerParser = failureTriggerParser;
    }

    @Override
    protected void configureRoutes() {
        from("direct:demo-ftp-sender")
          .routeId("demo-ftp-sender")
          .process(failureTriggerParser)
          .process(pocStep(FailureStep.SENDER_VALIDATE))
          .process(pocStep(FailureStep.SENDER_AUTH))
          .to("direct:demo-process?exchangePattern=InOut");
    }
}
