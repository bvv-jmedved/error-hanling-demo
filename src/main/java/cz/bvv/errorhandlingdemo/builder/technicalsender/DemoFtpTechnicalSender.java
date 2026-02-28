package cz.bvv.errorhandlingdemo.builder.technicalsender;

import cz.bvv.errorhandlingdemo.builder.common.BaseTechnicalSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.ftp.FtpRuntimeSettings;
import org.springframework.stereotype.Component;

@Component
public class DemoFtpTechnicalSender extends BaseTechnicalSenderRouteBuilder {
    private final FtpRuntimeSettings ftpRuntimeSettings;

    public DemoFtpTechnicalSender(FtpRuntimeSettings ftpRuntimeSettings) {
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
            + "&delay=1000"
            + "&move=.archive/${date:now:yyyyMMddHHmmssSSS}-${file:name}"
            + "&moveFailed=.error/${date:now:yyyyMMddHHmmssSSS}-${file:name}")
          .routeId("demo-ftp-technical-sender")
          .to("direct:demo-ftp-sender");
    }
}
