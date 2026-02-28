package cz.bvv.errorhandlingdemo.builder.common.routepolicy;

import static org.assertj.core.api.Assertions.assertThat;

import cz.bvv.errorhandlingdemo.builder.common.BaseRouteBuilder;
import java.util.ArrayList;
import java.util.List;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.jupiter.api.Test;

class RouteConfigurationModuleOrchestrationTest {

    @Test
    void shouldRunModulesAfterDslInConfiguredOrder() {
        List<String> invocationOrder = new ArrayList<>();
        TestRouteBuilder builder = new TestRouteBuilder(List.of(
          new RecordingModule("first", invocationOrder),
          new RecordingModule("second", invocationOrder)
        ));
        builder.setCamelContext(new DefaultCamelContext());

        builder.configure();

        assertThat(invocationOrder).containsExactly("first:1", "second:1");
        assertThat(builder.getRouteCollection().getRoutes()).hasSize(1);
        assertThat(builder.getRouteCollection().getRoutes().getFirst().getId()).isEqualTo("module-order-test-route");
    }

    private record RecordingModule(
      String name,
      List<String> invocationOrder
    ) implements RouteConfigurationModule {

        @Override
            public boolean supports(RouteBuilder builder) {
                return true;
            }

            @Override
            public void afterRoutes(RouteBuilder builder) {
                invocationOrder.add(name + ":" + builder.getRouteCollection().getRoutes().size());
            }
        }

    private static final class TestRouteBuilder extends BaseRouteBuilder {
        private TestRouteBuilder(List<RouteConfigurationModule> modules) {
            super(modules);
        }

        @Override
        protected void configureRoutes() {
            from("direct:module-order-test")
              .routeId("module-order-test-route")
              .to("mock:module-order-test");
        }
    }
}
