package cz.bvv.errorhandlingdemo;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("poc")
class DefaultRestFailureContractRoutePolicyTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnNonBlankJsonErrorBody() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X_THROW_IN", "technical-call");
        headers.set("X_THROW_TYPE", "http");
        headers.set("X_THROW_STATUS", "500");

        ResponseEntity<String> response = restTemplate.exchange(
          "http://localhost:" + port + "/demo",
          HttpMethod.POST,
          new HttpEntity<>("{}", headers),
          String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(response.getHeaders().getContentType()).isNotNull();
        assertThat(response.getHeaders().getContentType().isCompatibleWith(MediaType.APPLICATION_JSON)).isTrue();
        assertThat(response.getBody()).isNotBlank();

        JsonNode jsonBody = objectMapper.readTree(response.getBody());
        assertThat(jsonBody.isObject()).isTrue();
        assertThat(jsonBody.path("errors").isArray()).isTrue();
        assertThat(jsonBody.path("errors")).isNotEmpty();
        assertThat(jsonBody.path("errors").get(0).path("code").asText()).isEqualTo("DOWNSTREAM_HTTP_500");
    }
}
