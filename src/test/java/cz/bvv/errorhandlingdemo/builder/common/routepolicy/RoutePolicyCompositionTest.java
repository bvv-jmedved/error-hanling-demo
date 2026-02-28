package cz.bvv.errorhandlingdemo.builder.common.routepolicy;

import static org.assertj.core.api.Assertions.assertThat;

import cz.bvv.errorhandlingdemo.builder.common.BaseRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.common.BaseSyncSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.common.failure.FailureContractRoutePolicy;
import cz.bvv.errorhandlingdemo.builder.common.failure.IntegrationExceptionMapper;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging.LoggingRoutePolicy;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging.LoggingRoutePolicyFactory;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.module.FailContractModule;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.module.LoggingModule;
import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import java.lang.reflect.Field;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.RoutePolicy;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class RoutePolicyCompositionTest {

    @Test
    void shouldApplyFailContractThenLoggingOnSyncRouteBuilder() throws Exception {
        SyncRouteBuilder builder = new SyncRouteBuilder(
          new DummyFailureContractRoutePolicy(exception -> new IntegrationException(
            HttpStatus.BAD_GATEWAY,
            "MAPPED",
            "mapped",
            exception
          ))
        );
        builder.setCamelContext(new DefaultCamelContext());
        setModules(builder, List.of(
          new FailContractModule(),
          new LoggingModule(new LoggingRoutePolicyFactory())
        ));

        builder.configure();

        List<RoutePolicy> policies = builder.getRouteCollection().getRoutes().get(0).getRoutePolicies();
        assertThat(policies).hasSize(2);
        assertThat(policies.get(0)).isInstanceOf(DummyFailureContractRoutePolicy.class);
        assertThat(policies.get(1)).isInstanceOf(LoggingRoutePolicy.class);
    }

    @Test
    void shouldApplyOnlyLoggingOnNonSyncRouteBuilder() throws Exception {
        NonSyncRouteBuilder builder = new NonSyncRouteBuilder();
        builder.setCamelContext(new DefaultCamelContext());
        setModules(builder, List.of(
          new FailContractModule(),
          new LoggingModule(new LoggingRoutePolicyFactory())
        ));

        builder.configure();

        List<RoutePolicy> policies = builder.getRouteCollection().getRoutes().get(0).getRoutePolicies();
        assertThat(policies).hasSize(1);
        assertThat(policies.get(0)).isInstanceOf(LoggingRoutePolicy.class);
    }

    private static void setModules(BaseRouteBuilder builder, List<RouteConfigurationModule> modules) throws Exception {
        Field modulesField = BaseRouteBuilder.class.getDeclaredField("modules");
        modulesField.setAccessible(true);
        modulesField.set(builder, modules);
    }

    private static final class SyncRouteBuilder extends BaseSyncSenderRouteBuilder {
        private SyncRouteBuilder(FailureContractRoutePolicy failureContractRoutePolicy) {
            super(failureContractRoutePolicy);
        }

        @Override
        protected void configureRoutes() {
            from("direct:route-policy-composition-sync")
              .routeId("route-policy-composition-sync")
              .to("mock:sync");
        }
    }

    private static final class NonSyncRouteBuilder extends BaseRouteBuilder {
        @Override
        protected void configureRoutes() {
            from("direct:route-policy-composition-nonsync")
              .routeId("route-policy-composition-nonsync")
              .to("mock:nonsync");
        }
    }

    private static final class DummyFailureContractRoutePolicy extends FailureContractRoutePolicy {
        private DummyFailureContractRoutePolicy(IntegrationExceptionMapper integrationExceptionMapper) {
            super(integrationExceptionMapper);
        }

        @Override
        protected void mapContract(IntegrationException exception, Exchange exchange) {
            exchange.getMessage().setBody(exception.getMessage());
        }
    }
}
