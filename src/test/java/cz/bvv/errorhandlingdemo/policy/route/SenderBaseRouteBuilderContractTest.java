package cz.bvv.errorhandlingdemo.policy.route;

import static org.assertj.core.api.Assertions.assertThat;

import cz.bvv.errorhandlingdemo.builder.base.rest.BaseRestSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.base.soap.BaseSoapSenderRouteBuilder;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

/**
 * SenderBaseRouteBuilderContractTest unit test.
 *
 * <p>Verifies the local component contract and documents expected architecture behavior.
 */
class SenderBaseRouteBuilderContractTest {

  @Test
  void shouldNotOverrideConfigureInRestAndSoapSenderBaseBuilders() {
    assertThat(declaresConfigureMethod(BaseRestSenderRouteBuilder.class)).isFalse();
    assertThat(declaresConfigureMethod(BaseSoapSenderRouteBuilder.class)).isFalse();
  }

  private static boolean declaresConfigureMethod(Class<?> type) {
    return Arrays.stream(type.getDeclaredMethods())
        .anyMatch(method -> method.getName().equals("configure"));
  }
}
