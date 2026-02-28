package cz.bvv.errorhandlingdemo.builder.common.routepolicy;

import static org.assertj.core.api.Assertions.assertThat;

import cz.bvv.errorhandlingdemo.builder.common.BaseRouteBuilder;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.jupiter.api.Test;

class RouteConfigurationModuleOrchestrationTest {

    @Test
    void shouldRunModulesAfterDslInConfiguredOrder() throws Exception {
        List<String> invocationOrder = new ArrayList<>();
        TestRouteBuilder builder = new TestRouteBuilder();
        builder.setCamelContext(new DefaultCamelContext());

        setModules(builder, List.of(
          new RecordingModule("first", invocationOrder),
          new RecordingModule("second", invocationOrder)
        ));

        builder.configure();

        assertThat(invocationOrder).containsExactly("first:1", "second:1");
        assertThat(builder.getRouteCollection().getRoutes()).hasSize(1);
        assertThat(builder.getRouteCollection().getRoutes().get(0).getId()).isEqualTo("module-order-test-route");
    }

    private static void setModules(BaseRouteBuilder builder, List<RouteConfigurationModule> modules) throws Exception {
        Field modulesField = BaseRouteBuilder.class.getDeclaredField("modules");
        modulesField.setAccessible(true);
        modulesField.set(builder, modules);
    }

    private static final class RecordingModule implements RouteConfigurationModule {
        private final String name;
        private final List<String> invocationOrder;

        private RecordingModule(String name, List<String> invocationOrder) {
            this.name = name;
            this.invocationOrder = invocationOrder;
        }

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
        @Override
        protected void configureRoutes() {
            from("direct:module-order-test")
              .routeId("module-order-test-route")
              .to("mock:module-order-test");
        }
    }
}
