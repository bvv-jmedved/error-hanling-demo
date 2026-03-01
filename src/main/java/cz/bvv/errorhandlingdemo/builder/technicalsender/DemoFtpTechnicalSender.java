package cz.bvv.errorhandlingdemo.builder.technicalsender;

import cz.bvv.errorhandlingdemo.builder.base.BaseTechnicalSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.foundation.annotation.FlowDirection;
import cz.bvv.errorhandlingdemo.policy.route.RouteConfigurationModule;
import cz.bvv.errorhandlingdemo.policy.route.logging.ExchangeLoggingStatus;
import cz.bvv.errorhandlingdemo.policy.route.logging.LoggingStatus;
import cz.bvv.errorhandlingdemo.policy.route.logging.Monitored;
import cz.bvv.errorhandlingdemo.ftp.FtpRuntimeSettings;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
@Monitored(service = "technical-sender-demo-ftp", critical = true)
@FlowDirection(source = "ftp-client", target = "order-system", process = "create-order")
@LoggingStatus(
  onStart = ExchangeLoggingStatus.STARTED,
  onComplete = ExchangeLoggingStatus.COMPLETED,
  onFailure = ExchangeLoggingStatus.FAILED
)
/**
 * DemoFtpTechnicalSender is part of the Camel error-handling architecture demo.
 *
 * <p>The type is documented to serve as an implementation guide for teams that want
 * to apply the same separation of route DSL, policy composition, and failure contracts.
 */
public class DemoFtpTechnicalSender extends BaseTechnicalSenderRouteBuilder {
    private final FtpRuntimeSettings ftpRuntimeSettings;

    public DemoFtpTechnicalSender(List<RouteConfigurationModule> modules,
      FtpRuntimeSettings ftpRuntimeSettings) {
        super(modules);
        this.ftpRuntimeSettings = ftpRuntimeSettings;
    }

    @Override
    protected void configureRoutes() {
        from("ftp://user@localhost:" + ftpRuntimeSettings.port() + "/test"
            + "?password=password"
            + "&passiveMode=true"
            + "&binary=true"
            + "&autoCreate=true"
            + "&disconnect=true"
            + "&delay=10000"
            + "&move=.archive/${date:now:yyyyMMddHHmmssSSS}-${file:name}"
            + "&moveFailed=.error/${date:now:yyyyMMddHHmmssSSS}-${file:name}")
          .routeId("demo-ftp-technical-sender")
          .to("direct:demo-ftp-sender");
    }
}
