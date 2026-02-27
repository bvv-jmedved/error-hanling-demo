package cz.bvv.errorhandlingdemo.builder.technicalsender;

import cz.bvv.errorhandlingdemo.builder.common.BaseTechnicalSenderRouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class DemoTechnicalSender extends BaseTechnicalSenderRouteBuilder {
    public static final String DEMO1_ENDPOINT = "/demo1";
    public static final String DEMO2_ENDPOINT = "/demo2";

    @Override
    protected void config() {
        rest()
          .post(DEMO1_ENDPOINT)
          .to("direct:demo-sender");

        rest()
          .post(DEMO2_ENDPOINT)
          .to("direct:demo2-sender");
    }
}
