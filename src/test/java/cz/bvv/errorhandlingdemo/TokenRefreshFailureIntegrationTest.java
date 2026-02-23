package cz.bvv.errorhandlingdemo;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
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
@Import(TokenRefreshFailureIntegrationTest.CamelProbeConfig.class)
class TokenRefreshFailureIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RetryProbe retryProbe;

    @Test
    void shouldMapTokenRefreshFailureAfterUnauthorizedDownstreamCall() throws Exception {
        retryProbe.reset();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X_THROW_IN", "technical-call");
        headers.set("X_THROW_TYPE", "http");
        headers.set("X_THROW_STATUS", "401");
        headers.set("X_TOKEN_REFRESH_FAIL", "true");

        ResponseEntity<String> response = restTemplate.exchange(
          "http://localhost:" + port + "/demo",
          HttpMethod.POST,
          new HttpEntity<>("{}", headers),
          String.class
        );

        assertThat(retryProbe.getTechnicalReceiverAttemptCount()).isEqualTo(1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(response.getBody()).isNotBlank();

        JsonNode jsonBody = objectMapper.readTree(response.getBody());
        assertThat(jsonBody.path("errors").isArray()).isTrue();
        assertThat(jsonBody.path("errors")).isNotEmpty();
        assertThat(jsonBody.path("errors").get(0).path("code").asText()).isEqualTo("TOKEN_REFRESH_FAILED");
        assertThat(retryProbe.getSenderExchangeFailed()).isFalse();
    }

    @TestConfiguration
    static class CamelProbeConfig {
        @Bean
        RetryProbe retryProbe(CamelContext camelContext) {
            RetryProbe probe = new RetryProbe();
            camelContext.getManagementStrategy().addEventNotifier(probe);
            return probe;
        }
    }

    static class RetryProbe extends EventNotifierSupport {
        private final AtomicInteger technicalReceiverAttemptCount = new AtomicInteger(0);
        private final AtomicBoolean senderExchangeFailed = new AtomicBoolean(true);

        @Override
        public void notify(CamelEvent event) {
            if (event instanceof CamelEvent.ExchangeSendingEvent sendingEvent) {
                Endpoint endpoint = sendingEvent.getEndpoint();
                if (endpoint != null && endpoint.getEndpointUri().contains("technical-receiver")) {
                    technicalReceiverAttemptCount.incrementAndGet();
                }
            }

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

        int getTechnicalReceiverAttemptCount() {
            return technicalReceiverAttemptCount.get();
        }

        boolean getSenderExchangeFailed() {
            return senderExchangeFailed.get();
        }

        void reset() {
            technicalReceiverAttemptCount.set(0);
            senderExchangeFailed.set(true);
        }
    }
}
