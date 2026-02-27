package cz.bvv.errorhandlingdemo.builder.technicalsender;

import cz.bvv.errorhandlingdemo.builder.common.BaseTechnicalSenderRouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class DemoFtpTechnicalSender extends BaseTechnicalSenderRouteBuilder {
    @Override
    protected void config() {
        from("ftp://user@localhost:2121/test"
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
