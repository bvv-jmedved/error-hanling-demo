package cz.bvv.errorhandlingdemo;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(DefaultRestFailureContractSerializationFallbackTest.SerializationFailureConfig.class)
class DefaultRestFailureContractSerializationFallbackTest {

    private static final String FALLBACK_ERROR_BODY = """
      {
        "errors": [
          {
            "code": "INTERNAL_ERROR",
            "message": "Cannot serialize error response"
          }
        ]
      }
      """.trim();

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldFallbackToInternalErrorJsonWhenSerializationFails() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.exchange(
          "http://localhost:" + port + "/demo",
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
    static class SerializationFailureConfig {
        @Bean
        @Primary
        ObjectMapper failingObjectMapper() {
            return new ObjectMapper() {
                @Override
                public String writeValueAsString(Object value) throws JsonProcessingException {
                    throw new JsonProcessingException("forced serialization failure") { };
                }
            };
        }
    }
}
