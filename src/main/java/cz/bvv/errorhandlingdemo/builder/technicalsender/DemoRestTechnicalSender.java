package cz.bvv.errorhandlingdemo.builder.technicalsender;

import cz.bvv.errorhandlingdemo.builder.common.BaseTechnicalSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging.Monitored;
import org.springframework.stereotype.Component;

@Component
@Monitored(service = "technical-sender-demo-rest", critical = true)
public class DemoRestTechnicalSender extends BaseTechnicalSenderRouteBuilder {
    public static final String DEMO1_ENDPOINT = "/demo1";
    public static final String DEMO2_ENDPOINT = "/demo2";

    @Override
    protected void configureRoutes() {
        rest()
          .post(DEMO1_ENDPOINT)
          .to("direct:demo-sender");

        rest()
          .post(DEMO2_ENDPOINT)
          .to("direct:demo2-sender");
    }
}
