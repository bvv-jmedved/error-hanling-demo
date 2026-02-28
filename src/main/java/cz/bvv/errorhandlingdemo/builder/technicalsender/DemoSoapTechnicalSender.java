package cz.bvv.errorhandlingdemo.builder.technicalsender;

import cz.bvv.errorhandlingdemo.builder.common.soap.BaseSoapTechnicalSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.RouteConfigurationModule;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging.Monitored;
import cz.bvv.errorhandlingdemo.builder.technicalsender.soap.DemoSoapEndpointConfig;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
@Monitored(service = "technical-sender-demo-soap", critical = true)
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
