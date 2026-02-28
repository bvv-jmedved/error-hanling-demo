package cz.bvv.errorhandlingdemo.builder.common.routepolicy;

import static org.assertj.core.api.Assertions.assertThat;

import cz.bvv.errorhandlingdemo.builder.common.rest.BaseRestSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.common.soap.BaseSoapSenderRouteBuilder;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

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
