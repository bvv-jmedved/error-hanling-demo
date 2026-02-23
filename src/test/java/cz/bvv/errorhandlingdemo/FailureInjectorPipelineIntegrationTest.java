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
@Import(FailureInjectorPipelineIntegrationTest.CamelProbeConfig.class)
class FailureInjectorPipelineIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SenderCompletionProbe senderCompletionProbe;

    @Test
    void shouldMapFailureWhenInjectedInSenderValidation() throws Exception {
        senderCompletionProbe.reset();

        ResponseEntity<String> response = callDemoWithHeaders(headersFor("sender-validate"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertErrorCode(response, "INTERNAL_SERVER_ERROR");
        assertThat(senderCompletionProbe.getSenderExchangeFailed()).isFalse();
    }

    @Test
    void shouldMapFailureWhenInjectedInProcessTransformRequest() throws Exception {
        senderCompletionProbe.reset();

        ResponseEntity<String> response = callDemoWithHeaders(headersFor("process-transform-request"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertErrorCode(response, "INTERNAL_SERVER_ERROR");
        assertThat(senderCompletionProbe.getSenderExchangeFailed()).isFalse();
    }

    @Test
    void shouldMapHttpFailureToBadGatewayWhenInjectedInTechnicalCall() throws Exception {
        senderCompletionProbe.reset();

        HttpHeaders headers = headersFor("technical-call");
        headers.set("X_THROW_TYPE", "http");
        headers.set("X_THROW_STATUS", "500");

        ResponseEntity<String> response = callDemoWithHeaders(headers);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertErrorCode(response, "DOWNSTREAM_HTTP_500");
        assertThat(senderCompletionProbe.getSenderExchangeFailed()).isFalse();
    }

    @Test
    void shouldMapFailureWhenInjectedInProcessTransformResponse() throws Exception {
        senderCompletionProbe.reset();

        ResponseEntity<String> response = callDemoWithHeaders(headersFor("process-transform-response"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertErrorCode(response, "INTERNAL_SERVER_ERROR");
        assertThat(senderCompletionProbe.getSenderExchangeFailed()).isFalse();
    }

    private ResponseEntity<String> callDemoWithHeaders(HttpHeaders headers) {
        return restTemplate.exchange(
          "http://localhost:" + port + "/demo",
          HttpMethod.POST,
          new HttpEntity<>("{}", headers),
          String.class
        );
    }

    private static HttpHeaders headersFor(String stepId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X_THROW_IN", stepId);
        return headers;
    }

    private void assertErrorCode(ResponseEntity<String> response, String expectedErrorCode) throws Exception {
        assertThat(response.getBody()).isNotBlank();
        JsonNode jsonBody = objectMapper.readTree(response.getBody());
        assertThat(jsonBody.path("errors").isArray()).isTrue();
        assertThat(jsonBody.path("errors")).isNotEmpty();
        assertThat(jsonBody.path("errors").get(0).path("code").asText()).isEqualTo(expectedErrorCode);
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
