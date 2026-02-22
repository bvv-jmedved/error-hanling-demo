package cz.bvv.errorhandlingdemo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("poc")
@Import(HappyPathIntegrationTest.CamelProbeConfig.class)
class HappyPathIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SenderCompletionProbe senderCompletionProbe;

    @Test
    void shouldReturnSuccessWhenNoFailureIsInjected() {
        senderCompletionProbe.reset();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.exchange(
          "http://localhost:" + port + "/demo",
          HttpMethod.POST,
          new HttpEntity<>("{}", headers),
          String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("{\"status\":\"ok\"}");
        assertThat(senderCompletionProbe.getSenderExchangeFailed()).isFalse();
    }

    @TestConfiguration
    static class CamelProbeConfig {
        @Bean
        SenderCompletionProbe senderCompletionProbe(CamelContext camelContext) {
            SenderCompletionProbe probe = new SenderCompletionProbe();
            camelContext.getManagementStrategy().addEventNotifier(probe);
            return probe;
        }
    }

    static class SenderCompletionProbe extends EventNotifierSupport {
        private final AtomicBoolean senderExchangeFailed = new AtomicBoolean(true);

        @Override
        public void notify(CamelEvent event) {
            if (event instanceof CamelEvent.ExchangeCompletedEvent completedEvent) {
                Exchange exchange = completedEvent.getExchange();
                if ("demo-sender".equals(exchange.getFromRouteId())) {
                    senderExchangeFailed.set(exchange.isFailed());
                }
            }
        }

        @Override
        public boolean isEnabled(CamelEvent event) {
            return true;
        }

        boolean getSenderExchangeFailed() {
            return senderExchangeFailed.get();
        }

        void reset() {
            senderExchangeFailed.set(true);
        }
    }
}
