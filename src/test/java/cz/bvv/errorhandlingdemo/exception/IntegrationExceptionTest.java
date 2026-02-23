package cz.bvv.errorhandlingdemo.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class IntegrationExceptionTest {

    @Test
    void shouldThrowWhenStatusIsNullForSingleErrorConstructor() {
        assertThatThrownBy(() -> new IntegrationException(
          null,
          "ERR_CODE",
          "Error message",
          null
        ))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("status must not be null");
    }

    @Test
    void shouldThrowWhenStatusIsNullForListConstructor() {
        assertThatThrownBy(() -> new IntegrationException(
          null,
          List.of(new IntegrationError("ERR_CODE", "Error message")),
          "Error message",
          null
        ))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("status must not be null");
    }

    @Test
    void shouldThrowWhenErrorsListIsNull() {
        assertThatThrownBy(() -> new IntegrationException(
          HttpStatus.BAD_REQUEST,
          (List<IntegrationError>) null,
          "Error message",
          null
        ))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("errors must not be null or empty");
    }

    @Test
    void shouldThrowWhenErrorsListIsEmpty() {
        assertThatThrownBy(() -> new IntegrationException(
          HttpStatus.BAD_REQUEST,
          List.of(),
          "Error message",
          null
        ))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("errors must not be null or empty");
    }

    @Test
    void shouldCreateExceptionWithValidInputsForListConstructor() {
        IntegrationError firstError = new IntegrationError("CODE_1", "Message 1");
        IntegrationError secondError = new IntegrationError("CODE_2", "Message 2");

        IntegrationException exception = new IntegrationException(
          HttpStatus.BAD_REQUEST,
          List.of(firstError, secondError),
          "Error message",
          null
        );

        assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getErrors()).containsExactly(firstError, secondError);
    }

    @Test
    void shouldCreateExceptionWithValidInputsForSingleErrorConstructor() {
        IntegrationException exception = new IntegrationException(
          HttpStatus.BAD_REQUEST,
          "ERR_CODE",
          "Error message",
          null
        );

        assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getErrors()).containsExactly(new IntegrationError("ERR_CODE", "Error message"));
    }

    @Test
    void shouldCreateUnknownErrorWithValidInvariantValues() {
        IntegrationException exception = IntegrationException.unknownError();

        assertThat(exception.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(exception.getErrors()).hasSize(1);
        assertThat(exception.getErrors().getFirst().code()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.name());
        assertThat(exception.getErrors().getFirst().message()).isEqualTo("Unknown error");
    }
}
