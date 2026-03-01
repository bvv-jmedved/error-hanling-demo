package cz.bvv.errorhandlingdemo.builder.technicalsender.soap;

import org.apache.camel.component.cxf.common.DataFormat;
import org.apache.camel.component.cxf.jaxws.CxfEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * DemoSoapEndpointConfig is part of the Camel error-handling architecture demo.
 *
 * <p>The type is documented to serve as an implementation guide for teams that want
 * to apply the same separation of route DSL, policy composition, and failure contracts.
 */
@Configuration
public class DemoSoapEndpointConfig {
    public static final String ENDPOINT_BEAN_ID = "demoSoapEndpoint";
    public static final String ENDPOINT_ADDRESS = "/demo-soap";

    @Bean(name = ENDPOINT_BEAN_ID)
    CxfEndpoint demoSoapEndpoint() {
        CxfEndpoint endpoint = new CxfEndpoint();
        endpoint.setAddress(ENDPOINT_ADDRESS);
        endpoint.setServiceClass(DemoSoapService.class);
        endpoint.setDataFormat(DataFormat.RAW);
        return endpoint;
    }
}
