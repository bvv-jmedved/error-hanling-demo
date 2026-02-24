package cz.bvv.errorhandlingdemo;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.bvv.errorhandlingdemo.builder.common.rest.BaseRestSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.common.rest.DefaultRestFailureContractRoutePolicy;
import cz.bvv.errorhandlingdemo.exception.IntegrationError;
import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import java.util.List;
import org.apache.camel.Exchange;
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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("poc")
@Import(DefaultRestFailureContractMultiErrorIntegrationTest.MultiErrorRouteConfig.class)
class DefaultRestFailureContractMultiErrorIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldMapAllIntegrationErrorsWithoutDerivingCodeFromHttpStatus() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.exchange(
          "http://localhost:" + port + "/td024",
          HttpMethod.POST,
          new HttpEntity<>("{}", headers),
          String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.path("errors").isArray()).isTrue();
        assertThat(body.path("errors")).hasSize(2);
        assertThat(body.path("errors").get(0).path("code").asText()).isEqualTo("BUSINESS_RULE_A");
        assertThat(body.path("errors").get(0).path("message").asText()).isEqualTo("Business rule A failed");
        assertThat(body.path("errors").get(1).path("code").asText()).isEqualTo("BUSINESS_RULE_B");
        assertThat(body.path("errors").get(1).path("message").asText()).isEqualTo("Business rule B failed");
    }

    @TestConfiguration
    static class MultiErrorRouteConfig {

        @Bean
        Td024SenderRouteBuilder td024SenderRouteBuilder(
          DefaultRestFailureContractRoutePolicy failureContractRoutePolicy) {
            return new Td024SenderRouteBuilder(failureContractRoutePolicy);
        }
    }

    static class Td024SenderRouteBuilder extends BaseRestSenderRouteBuilder {

        Td024SenderRouteBuilder(DefaultRestFailureContractRoutePolicy failureContractRoutePolicy) {
            super(failureContractRoutePolicy);
        }

        @Override
        protected void config() {
            from("rest:post:/td024")
              .routeId("td024-sender")
              .process(exchange -> {
                  throw new IntegrationException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    List.of(
                      new IntegrationError("BUSINESS_RULE_A", "Business rule A failed"),
                      new IntegrationError("BUSINESS_RULE_B", "Business rule B failed")
                    ),
                    "Multiple business errors",
                    null
                  );
              });
        }
    }
}
