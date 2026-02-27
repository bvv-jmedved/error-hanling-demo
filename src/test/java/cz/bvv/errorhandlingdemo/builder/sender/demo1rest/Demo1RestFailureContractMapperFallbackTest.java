package cz.bvv.errorhandlingdemo.builder.sender.demo1rest;
import cz.bvv.errorhandlingdemo.builder.technicalsender.DemoTechnicalSender;

import static org.assertj.core.api.Assertions.assertThat;

import cz.bvv.errorhandlingdemo.builder.sender.demo1rest.model.Demo1RestError;
import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("poc")
@Import(Demo1RestFailureContractMapperFallbackTest.MappingFailureConfig.class)
class Demo1RestFailureContractMapperFallbackTest {

    private static final String FALLBACK_ERROR_BODY = """
      {
        "errors": [
          {
            "code": "INTERNAL_ERROR",
            "message": "Internal error while preparing error response"
          }
        ]
      }
      """.trim();

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldFallbackToInternalErrorJsonWhenErrorMappingFails() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X_THROW_IN", "technical-call");
        headers.set("X_THROW_TYPE", "http");
        headers.set("X_THROW_STATUS", "500");

        ResponseEntity<String> response = restTemplate.exchange(
          "http://localhost:" + port + DemoTechnicalSender.DEMO1_ENDPOINT,
          HttpMethod.POST,
          new HttpEntity<>("{}", headers),
          String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getHeaders().getContentType()).isNotNull();
        assertThat(response.getHeaders().getContentType().isCompatibleWith(MediaType.APPLICATION_JSON)).isTrue();
        assertThat(response.getBody()).isEqualTo(FALLBACK_ERROR_BODY);
    }

    @TestConfiguration
    static class MappingFailureConfig {
        @Bean
        @Primary
        Demo1RestFailureContractMapper failingFailureContractMapper() {
            return new Demo1RestFailureContractMapper() {
                @Override
                public Demo1RestError map(IntegrationException integrationException) {
                    throw new RuntimeException("forced mapping failure");
                }
            };
        }
    }
}
