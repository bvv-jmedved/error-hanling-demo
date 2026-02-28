package cz.bvv.errorhandlingdemo.builder.common.routepolicy;

import static org.assertj.core.api.Assertions.assertThat;

import cz.bvv.errorhandlingdemo.builder.common.failure.BaseFailContractRoutePolicy;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging.LoggingRoutePolicy;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.module.FailContractModule;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.module.LoggingModule;
import java.util.List;
import org.apache.camel.CamelContext;
import org.apache.camel.Route;
import org.apache.camel.spi.RoutePolicy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class RouteConfigurationModuleSpringIntegrationTest {

    @Autowired
    private List<RouteConfigurationModule> modules;

    @Autowired
    private CamelContext camelContext;

    @Test
    void shouldInjectModulesInSpringOrder() {
        int failContractIndex = indexOf(FailContractModule.class, modules);
        int loggingIndex = indexOf(LoggingModule.class, modules);

        assertThat(failContractIndex).isGreaterThanOrEqualTo(0);
        assertThat(loggingIndex).isGreaterThanOrEqualTo(0);
        assertThat(failContractIndex).isLessThan(loggingIndex);
    }

    @Test
    void shouldApplyOrderedPoliciesToSyncSenderRouteInSpringContext() throws Exception {
        Route route = camelContext.getRoute("demo2-sender");
        List<RoutePolicy> policies = route.getRoutePolicyList();

        assertThat(route).isNotNull();
        assertThat(policies).hasSizeGreaterThanOrEqualTo(2);
        assertThat(policies.get(0)).isInstanceOf(BaseFailContractRoutePolicy.class);
        assertThat(policies.get(1)).isInstanceOf(LoggingRoutePolicy.class);
    }

    private static int indexOf(Class<?> moduleClass, List<RouteConfigurationModule> modules) {
        for (int i = 0; i < modules.size(); i++) {
            if (moduleClass.isInstance(modules.get(i))) {
                return i;
            }
        }
        return -1;
    }
}
