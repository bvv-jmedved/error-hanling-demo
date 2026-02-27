package cz.bvv.errorhandlingdemo.builder.technicalsender;

import cz.bvv.errorhandlingdemo.builder.common.soap.BaseSoapTechnicalSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.technicalsender.soap.DemoSoapEndpointConfig;
import org.springframework.stereotype.Component;

@Component
public class DemoSoapTechnicalSender extends BaseSoapTechnicalSenderRouteBuilder {
    public static final String SOAP_ENDPOINT = "/services" + DemoSoapEndpointConfig.ENDPOINT_ADDRESS;

    @Override
    protected void config() {
        from("cxf:bean:" + DemoSoapEndpointConfig.ENDPOINT_BEAN_ID)
          .routeId("demo-soap-technical-sender")
          .to("direct:demo-soap-sender?exchangePattern=InOut");
    }
}
