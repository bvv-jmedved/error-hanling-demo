package cz.bvv.errorhanlingdemo.builder.technicalsender;

import cz.bvv.errorhanlingdemo.builder.common.BaseTechnicalSenderRouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class DemoTechnicalSender extends BaseTechnicalSenderRouteBuilder {
    @Override
    protected void config() {
        rest()
          .post("/demo")
          .to("seda:demo-sender");
    }
}
