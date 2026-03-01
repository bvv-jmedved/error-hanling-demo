package cz.bvv.errorhandlingdemo.builder.technicalsender;

import cz.bvv.errorhandlingdemo.builder.base.soap.BaseSoapTechnicalSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.technicalsender.soap.DemoSoapEndpointConfig;
import cz.bvv.errorhandlingdemo.policy.route.RouteConfigurationModule;
import cz.bvv.errorhandlingdemo.policy.route.logging.ExchangeLoggingStatus;
import cz.bvv.errorhandlingdemo.policy.route.logging.annotation.FlowDirection;
import cz.bvv.errorhandlingdemo.policy.route.logging.annotation.LoggingStatus;
import cz.bvv.errorhandlingdemo.policy.route.logging.annotation.Monitored;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
@Monitored(service = "technical-sender-demo-soap", critical = true)
@FlowDirection(source = "soap-client", target = "order-system", process = "create-order")
@LoggingStatus(
    onStart = ExchangeLoggingStatus.STARTED,
    onComplete = ExchangeLoggingStatus.COMPLETED,
    onFailure = ExchangeLoggingStatus.FAILED)
/**
 * DemoSoapTechnicalSender is part of the Camel error-handling architecture demo.
 *
 * <p>The type is documented to serve as an implementation guide for teams that want to apply the
 * same separation of route DSL, policy composition, and failure contracts.
 */
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
