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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("poc")
class DownstreamHttpMappingAlignmentIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldMapDownstream400ToClient400() throws Exception {
        ResponseEntity<String> response = callWithDownstreamStatus(400);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertErrorCode(response, "DOWNSTREAM_HTTP_400");
    }

    @Test
    void shouldMapDownstream404ToClient404() throws Exception {
        ResponseEntity<String> response = callWithDownstreamStatus(404);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertErrorCode(response, "DOWNSTREAM_HTTP_404");
    }

    @Test
    void shouldMapDownstream401ToClient502() throws Exception {
        ResponseEntity<String> response = callWithDownstreamStatus(401);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertErrorCode(response, "DOWNSTREAM_HTTP_401");
    }

    @Test
    void shouldMapDownstream500ToClient502() throws Exception {
        ResponseEntity<String> response = callWithDownstreamStatus(500);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertErrorCode(response, "DOWNSTREAM_HTTP_500");
    }

    private ResponseEntity<String> callWithDownstreamStatus(int downstreamStatus) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X_THROW_IN", "technical-call");
        headers.set("X_THROW_TYPE", "http");
        headers.set("X_THROW_STATUS", String.valueOf(downstreamStatus));

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
}
