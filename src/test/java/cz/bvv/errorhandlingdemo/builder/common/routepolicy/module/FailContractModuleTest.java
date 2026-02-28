package cz.bvv.errorhandlingdemo.builder.common.routepolicy.module;

import static org.assertj.core.api.Assertions.assertThat;

import cz.bvv.errorhandlingdemo.builder.common.BaseRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.common.BaseSyncSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.common.failure.FailureContractRoutePolicy;
import cz.bvv.errorhandlingdemo.builder.common.failure.IntegrationExceptionMapper;
import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class FailContractModuleTest {

    @Test
    void shouldSupportOnlyFailContractRouteBuilders() {
        FailContractModule module = new FailContractModule();
        IntegrationExceptionMapper mapper = exception -> IntegrationException.unknownError();

        assertThat(module.supports(new SyncRouteBuilder(new DummyFailureContractRoutePolicy(mapper)))).isTrue();
        assertThat(module.supports(new NonSyncRouteBuilder())).isFalse();
    }

    @Test
    void shouldAttachPolicyOnlyOnceWhenAppliedMultipleTimes() {
        FailContractModule module = new FailContractModule();
        IntegrationExceptionMapper mapper = exception -> new IntegrationException(
          HttpStatus.BAD_GATEWAY,
          "MAPPED",
          "mapped",
          exception
        );
        DummyFailureContractRoutePolicy policy = new DummyFailureContractRoutePolicy(mapper);
        SyncRouteBuilder builder = new SyncRouteBuilder(policy);
        builder.setCamelContext(new DefaultCamelContext());
        builder.configure();

        module.afterRoutes(builder);
        module.afterRoutes(builder);

        assertThat(builder.getRouteCollection().getRoutes()).hasSize(1);
        assertThat(builder.getRouteCollection().getRoutes().getFirst().getRoutePolicies()).hasSize(1);
        assertThat(builder.getRouteCollection().getRoutes().getFirst().getRoutePolicies().getFirst()).isSameAs(policy);
    }

    private static final class SyncRouteBuilder extends BaseSyncSenderRouteBuilder {
        private SyncRouteBuilder(FailureContractRoutePolicy failureContractRoutePolicy) {
            super(failureContractRoutePolicy);
        }

        @Override
        protected void configureRoutes() {
            from("direct:fail-contract-module-sync")
              .routeId("fail-contract-module-sync")
              .to("mock:sync");
        }
    }

    private static final class NonSyncRouteBuilder extends BaseRouteBuilder {
        @Override
        protected void configureRoutes() {
            from("direct:fail-contract-module-nonsync")
              .routeId("fail-contract-module-nonsync")
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
