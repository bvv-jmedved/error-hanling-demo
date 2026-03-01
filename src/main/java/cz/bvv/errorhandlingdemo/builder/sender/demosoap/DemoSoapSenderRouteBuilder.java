package cz.bvv.errorhandlingdemo.builder.sender.demosoap;

import cz.bvv.errorhandlingdemo.builder.base.soap.BaseSoapSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.foundation.annotation.FlowDirection;
import cz.bvv.errorhandlingdemo.policy.route.RouteConfigurationModule;
import cz.bvv.errorhandlingdemo.policy.route.logging.Monitored;
import cz.bvv.errorhandlingdemo.poc.FailureStepProcessorFactory;
import cz.bvv.errorhandlingdemo.poc.FailureStep;
import java.util.List;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

/**
 * Concrete route definition used in the error-handling architecture walkthrough.
 *
 * <p>The class intentionally focuses on Camel DSL only and delegates cross-cutting behavior
 * (logging, failure-contract mapping, monitoring status) to policy modules.
 */
@Component
@Monitored(service = "sender-demo-soap")
@FlowDirection(source = "soap-client", target = "order-system", process = "create-order")
public class DemoSoapSenderRouteBuilder extends BaseSoapSenderRouteBuilder {
    private final DemoSoapMessageMapper demoSoapMessageMapper;
    private final FailureStepProcessorFactory failureStepProcessorFactory;

    protected DemoSoapSenderRouteBuilder(List<RouteConfigurationModule> modules,
      DemoSoapFailureContractRoutePolicy failureContractRoutePolicy,
      DemoSoapMessageMapper demoSoapMessageMapper,
      FailureStepProcessorFactory failureStepProcessorFactory) {
        super(modules, failureContractRoutePolicy);
        this.demoSoapMessageMapper = demoSoapMessageMapper;
        this.failureStepProcessorFactory = failureStepProcessorFactory;
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
          .process(failureStepProcessorFactory.forStep(FailureStep.SENDER_VALIDATE))
          .process(failureStepProcessorFactory.forStep(FailureStep.SENDER_AUTH))
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
