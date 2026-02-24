package cz.bvv.errorhandlingdemo;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
@Import(SenderBusinessErrorModelingIntegrationTest.CamelProbeConfig.class)
class SenderBusinessErrorModelingIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SenderCompletionProbe senderCompletionProbe;

    @Test
    void shouldMapSenderValidationBusinessFailureToClient400() throws Exception {
        senderCompletionProbe.reset();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X_THROW_IN", "sender-validate");
        headers.set("X_THROW_TYPE", "business-validation");

        ResponseEntity<String> response = callDemo(headers);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertErrorCode(response, "VALIDATION_FAILED");
        assertThat(senderCompletionProbe.getSenderExchangeFailed()).isFalse();
    }

    @Test
    void shouldMapSenderAuthorizationBusinessFailureToClient401() throws Exception {
        senderCompletionProbe.reset();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X_THROW_IN", "sender-auth");
        headers.set("X_THROW_TYPE", "business-unauthorized");

        ResponseEntity<String> response = callDemo(headers);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertErrorCode(response, "UNAUTHORIZED");
        assertThat(senderCompletionProbe.getSenderExchangeFailed()).isFalse();
    }

    private ResponseEntity<String> callDemo(HttpHeaders headers) {
        return restTemplate.exchange(
          "http://localhost:" + port + "/demo",
          HttpMethod.POST,
          new HttpEntity<>("{}", headers),
          String.class
        );
    }

    private void assertErrorCode(ResponseEntity<String> response, String expectedCode) throws Exception {
        assertThat(response.getBody()).isNotBlank();
        JsonNode jsonBody = objectMapper.readTree(response.getBody());
        assertThat(jsonBody.path("errors").isArray()).isTrue();
        assertThat(jsonBody.path("errors")).isNotEmpty();
        assertThat(jsonBody.path("errors").get(0).path("code").asText()).isEqualTo(expectedCode);
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

        boolean getSenderExchangeFailed() {
            return senderExchangeFailed.get();
        }

        void reset() {
            senderExchangeFailed.set(true);
        }
    }
}
