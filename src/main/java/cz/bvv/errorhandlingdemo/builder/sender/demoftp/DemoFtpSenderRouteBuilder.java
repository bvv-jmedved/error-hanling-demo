package cz.bvv.errorhandlingdemo.builder.sender.demoftp;

import cz.bvv.errorhandlingdemo.builder.common.BaseSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.poc.FailureStep;
import org.springframework.stereotype.Component;

@Component
public class DemoFtpSenderRouteBuilder extends BaseSenderRouteBuilder {

    private final DemoFtpFailureTriggerParser failureTriggerParser;

    public DemoFtpSenderRouteBuilder(DemoFtpFailureTriggerParser failureTriggerParser) {
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
