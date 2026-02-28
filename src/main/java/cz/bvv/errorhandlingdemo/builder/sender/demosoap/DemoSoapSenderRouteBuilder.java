package cz.bvv.errorhandlingdemo.builder.sender.demosoap;

import cz.bvv.errorhandlingdemo.builder.common.soap.BaseSoapSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.RouteConfigurationModule;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging.Monitored;
import cz.bvv.errorhandlingdemo.poc.FailureStep;
import java.util.List;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
@Monitored(service = "sender-demo-soap")
public class DemoSoapSenderRouteBuilder extends BaseSoapSenderRouteBuilder {
    private final DemoSoapMessageMapper demoSoapMessageMapper;

    protected DemoSoapSenderRouteBuilder(
      List<RouteConfigurationModule> modules,
      DemoSoapFailureContractRoutePolicy failureContractRoutePolicy,
      DemoSoapMessageMapper demoSoapMessageMapper) {
        super(modules, failureContractRoutePolicy);
        this.demoSoapMessageMapper = demoSoapMessageMapper;
    }

    @Override
    public void configureRoutes() {
        from("direct:demo-soap-sender")
          .routeId("demo-soap-sender")
          .process(exchange -> {
              String soapBody = demoSoapMessageMapper.readBody(exchange);
              exchange.getMessage().setBody(soapBody);
              demoSoapMessageMapper.applyFailureHeaders(exchange, soapBody);
          })
          .process(pocStep(FailureStep.SENDER_VALIDATE))
          .process(pocStep(FailureStep.SENDER_AUTH))
          .to("direct:demo-process?exchangePattern=InOut")
          .process(exchange -> {
              String soapResponse = demoSoapMessageMapper.createSuccessResponseXml(
                exchange.getMessage().getBody(String.class)
              );
              exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "text/xml; charset=UTF-8");
              exchange.getMessage().setBody(soapResponse);
          });
    }
}
