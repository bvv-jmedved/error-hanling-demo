package cz.bvv.errorhandlingdemo.builder.common.failure;

import static org.assertj.core.api.Assertions.assertThat;

import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class DefaultIntegrationExceptionMapperImplTest {

    private final DefaultIntegrationExceptionMapperImpl mapper = new DefaultIntegrationExceptionMapperImpl();

    @Test
    void shouldMap500DownstreamToBadGatewayWithDeterministicErrorCode() {
        HttpOperationFailedException exception = new HttpOperationFailedException(
          "http://downstream",
          500,
          "Downstream Internal Server Error",
          null,
          null,
          "{\"error\":\"secret\"}"
        );

        IntegrationException mapped = mapper.map(exception);

        assertThat(mapped.getStatus()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(mapped.getErrors()).singleElement().satisfies(error -> {
            assertThat(error.code()).isEqualTo("DOWNSTREAM_HTTP_500");
            assertThat(error.message()).isEqualTo("Downstream Internal Server Error");
        });
        assertThat(mapped.getCause()).isSameAs(exception);
    }

    @Test
    void shouldMap404DownstreamToClient404WithDeterministicErrorCode() {
        HttpOperationFailedException exception = new HttpOperationFailedException(
          "http://downstream",
          404,
          "Not Found",
          null,
          null,
          null
        );

        IntegrationException mapped = mapper.map(exception);

        assertThat(mapped.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(mapped.getErrors()).singleElement().satisfies(error -> {
            assertThat(error.code()).isEqualTo("DOWNSTREAM_HTTP_404");
            assertThat(error.message()).isEqualTo("Not Found");
        });
        assertThat(mapped.getCause()).isSameAs(exception);
    }

    @Test
    void shouldMap401DownstreamToBadGatewayWithDeterministicErrorCode() {
        HttpOperationFailedException exception = new HttpOperationFailedException(
          "http://downstream",
          401,
          "Unauthorized",
          null,
          null,
          null
        );

        IntegrationException mapped = mapper.map(exception);

        assertThat(mapped.getStatus()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(mapped.getErrors()).singleElement().satisfies(error -> {
            assertThat(error.code()).isEqualTo("DOWNSTREAM_HTTP_401");
            assertThat(error.message()).isEqualTo("Unauthorized");
        });
        assertThat(mapped.getCause()).isSameAs(exception);
    }

    @Test
    void shouldFallbackToSanitizedMessageWhenStatusTextIsBlank() {
        HttpOperationFailedException exception = new HttpOperationFailedException(
          "http://downstream",
          503,
          "  ",
          null,
          null,
          "{\"internal\":\"secret\"}"
        );

        IntegrationException mapped = mapper.map(exception);

        assertThat(mapped.getErrors()).singleElement().satisfies(error -> {
            assertThat(error.code()).isEqualTo("DOWNSTREAM_HTTP_503");
            assertThat(error.message()).contains("statusCode: 503");
            assertThat(error.message()).doesNotContain("secret");
            assertThat(error.message()).doesNotContain("with body:");
        });
    }
}
