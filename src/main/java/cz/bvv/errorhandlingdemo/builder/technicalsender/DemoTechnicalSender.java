package cz.bvv.errorhandlingdemo.builder.technicalsender;

import cz.bvv.errorhandlingdemo.builder.common.BaseTechnicalSenderRouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class DemoTechnicalSender extends BaseTechnicalSenderRouteBuilder {
    @Override
    protected void config() {
        rest()
          .post("/demo")
          .to("direct:demo-sender");
    }
}
