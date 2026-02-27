package cz.bvv.errorhandlingdemo.builder.sender.demoftp;

import java.util.Set;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class DemoFtpFailureTriggerParser implements Processor {
    private static final Set<String> SUPPORTED_TRIGGER_HEADERS = Set.of(
      "X_THROW_IN",
      "X_THROW_TYPE",
      "X_THROW_STATUS",
      "X_THROW_BODY"
    );

    @Override
    public void process(Exchange exchange) {
        String rawBody = exchange.getIn().getBody(String.class);
        if (rawBody == null || rawBody.isBlank()) {
            return;
        }

        String payload = null;
        for (String line : rawBody.split("\\R")) {
            int separator = line.indexOf('=');
            if (separator < 1) {
                continue;
            }

            String key = line.substring(0, separator).trim();
            String value = line.substring(separator + 1).trim();

            if ("payload".equals(key)) {
                payload = value;
                continue;
            }

            if (SUPPORTED_TRIGGER_HEADERS.contains(key)) {
                exchange.getIn().setHeader(key, value);
            }
        }

        if (payload != null) {
            exchange.getIn().setBody(payload);
        }
    }
}
