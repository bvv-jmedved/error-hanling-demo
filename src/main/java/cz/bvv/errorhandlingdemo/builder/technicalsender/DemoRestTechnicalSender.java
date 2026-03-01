package cz.bvv.errorhandlingdemo.builder.technicalsender;

import cz.bvv.errorhandlingdemo.builder.base.BaseTechnicalSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.policy.route.RouteConfigurationModule;
import cz.bvv.errorhandlingdemo.policy.route.logging.ExchangeLoggingStatus;
import cz.bvv.errorhandlingdemo.policy.route.logging.annotation.FlowDirection;
import cz.bvv.errorhandlingdemo.policy.route.logging.annotation.LoggingStatus;
import cz.bvv.errorhandlingdemo.policy.route.logging.annotation.Monitored;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
@Monitored(service = "technical-sender-demo-rest", critical = true)
@FlowDirection(source = "rest-client", target = "order-system", process = "create-order")
@LoggingStatus(
    onStart = ExchangeLoggingStatus.STARTED,
    onComplete = ExchangeLoggingStatus.COMPLETED,
    onFailure = ExchangeLoggingStatus.FAILED)
/**
 * DemoRestTechnicalSender is part of the Camel error-handling architecture demo.
 *
 * <p>The type is documented to serve as an implementation guide for teams that want to apply the
 * same separation of route DSL, policy composition, and failure contracts.
 */
public class DemoRestTechnicalSender extends BaseTechnicalSenderRouteBuilder {
  public static final String DEMO1_ENDPOINT = "/demo1";
  public static final String DEMO2_ENDPOINT = "/demo2";

  public DemoRestTechnicalSender(List<RouteConfigurationModule> modules) {
    super(modules);
  }

  @Override
  protected void configureRoutes() {
    rest().post(DEMO1_ENDPOINT).to("direct:demo-sender");

    rest().post(DEMO2_ENDPOINT).to("direct:demo2-sender");
  }
}
