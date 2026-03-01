package cz.bvv.errorhandlingdemo.builder.technicalsender;

import cz.bvv.errorhandlingdemo.builder.base.soap.BaseSoapTechnicalSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.policy.route.RouteConfigurationModule;
import cz.bvv.errorhandlingdemo.policy.route.logging.ExchangeLoggingStatus;
import cz.bvv.errorhandlingdemo.policy.route.logging.LoggingStatus;
import cz.bvv.errorhandlingdemo.policy.route.logging.Monitored;
import cz.bvv.errorhandlingdemo.builder.technicalsender.soap.DemoSoapEndpointConfig;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
@Monitored(service = "technical-sender-demo-soap", critical = true)
@LoggingStatus(
  onStart = ExchangeLoggingStatus.STARTED,
  onComplete = ExchangeLoggingStatus.COMPLETED,
  onFailure = ExchangeLoggingStatus.FAILED
)
public class DemoSoapTechnicalSender extends BaseSoapTechnicalSenderRouteBuilder {
    public static final String SOAP_ENDPOINT = "/services" + DemoSoapEndpointConfig.ENDPOINT_ADDRESS;

    public DemoSoapTechnicalSender(List<RouteConfigurationModule> modules) {
        super(modules);
    }

    @Override
    protected void configureRoutes() {
        from("cxf:bean:" + DemoSoapEndpointConfig.ENDPOINT_BEAN_ID)
          .routeId("demo-soap-technical-sender")
          .to("direct:demo-soap-sender?exchangePattern=InOut");
    }
}
