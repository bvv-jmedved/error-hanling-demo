package cz.bvv.errorhandlingdemo.builder.common.routepolicy;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.bvv.errorhandlingdemo.builder.common.BaseRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.common.BaseSyncSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.common.failure.DefaultIntegrationExceptionMapperImpl;
import cz.bvv.errorhandlingdemo.builder.common.failure.FailureContractRoutePolicy;
import cz.bvv.errorhandlingdemo.builder.common.failure.IntegrationExceptionMapper;
import cz.bvv.errorhandlingdemo.builder.common.rest.DefaultRestFailureContractRoutePolicy;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.FailContractRoutePolicy;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging.LoggingRoutePolicy;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging.LoggingRoutePolicyFactory;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.module.FailContractModule;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.module.LoggingModule;
import cz.bvv.errorhandlingdemo.builder.sender.demo2rest.Demo2RestSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.sender.demosoap.DemoSoapFailureContractRoutePolicy;
import cz.bvv.errorhandlingdemo.builder.sender.demosoap.DemoSoapMessageMapper;
import cz.bvv.errorhandlingdemo.builder.sender.demosoap.DemoSoapSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.technicalsender.DemoSoapTechnicalSender;
import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import java.lang.reflect.Constructor;
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

        List<RoutePolicy> policies = builder.getRouteCollection().getRoutes().getFirst().getRoutePolicies();
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

        List<RoutePolicy> policies = builder.getRouteCollection().getRoutes().getFirst().getRoutePolicies();
        assertThat(policies).hasSize(1);
        assertThat(policies.getFirst()).isInstanceOf(LoggingRoutePolicy.class);
    }

    @Test
    void shouldApplyConfiguredMonitoringMetadataOnRestSenderRouteBuilder() throws Exception {
        Demo2RestSenderRouteBuilder builder = createDemo2RestSenderRouteBuilder();
        builder.setCamelContext(new DefaultCamelContext());
        setModules(builder, List.of(
          new FailContractModule(),
          new LoggingModule(new LoggingRoutePolicyFactory())
        ));

        builder.configure();

        List<RoutePolicy> policies = builder.getRouteCollection().getRoutes().getFirst().getRoutePolicies();
        assertThat(policies).hasSize(2);
        assertThat(policies.get(0)).isInstanceOf(FailureContractRoutePolicy.class);
        assertThat(policies.get(1)).isInstanceOf(LoggingRoutePolicy.class);
        LoggingRoutePolicy loggingPolicy = (LoggingRoutePolicy) policies.get(1);
        assertThat(loggingPolicy.getService()).isEqualTo("sender-demo2-rest");
        assertThat(loggingPolicy.isCritical()).isFalse();
    }

    @Test
    void shouldApplyConfiguredMonitoringMetadataOnSoapSenderRouteBuilder() throws Exception {
        DemoSoapSenderRouteBuilder builder = createDemoSoapSenderRouteBuilder();
        builder.setCamelContext(new DefaultCamelContext());
        setModules(builder, List.of(
          new FailContractModule(),
          new LoggingModule(new LoggingRoutePolicyFactory())
        ));

        builder.configure();

        List<RoutePolicy> policies = builder.getRouteCollection().getRoutes().getFirst().getRoutePolicies();
        assertThat(policies).hasSize(2);
        assertThat(policies.get(0)).isInstanceOf(FailureContractRoutePolicy.class);
        assertThat(policies.get(1)).isInstanceOf(LoggingRoutePolicy.class);
        LoggingRoutePolicy loggingPolicy = (LoggingRoutePolicy) policies.get(1);
        assertThat(loggingPolicy.getService()).isEqualTo("sender-demo-soap");
        assertThat(loggingPolicy.isCritical()).isFalse();
    }

    @Test
    void shouldApplyConfiguredMonitoringMetadataOnTechnicalIngressRouteBuilder() throws Exception {
        DemoSoapTechnicalSender builder = new DemoSoapTechnicalSender();
        builder.setCamelContext(new DefaultCamelContext());
        setModules(builder, List.of(
          new FailContractModule(),
          new LoggingModule(new LoggingRoutePolicyFactory())
        ));

        builder.configure();

        List<RoutePolicy> policies = builder.getRouteCollection().getRoutes().getFirst().getRoutePolicies();
        assertThat(policies).hasSize(1);
        assertThat(policies.getFirst()).isInstanceOf(LoggingRoutePolicy.class);
        LoggingRoutePolicy loggingPolicy = (LoggingRoutePolicy) policies.getFirst();
        assertThat(loggingPolicy.getService()).isEqualTo("technical-sender-demo-soap");
        assertThat(loggingPolicy.isCritical()).isTrue();
    }

    private static void setModules(BaseRouteBuilder builder, List<RouteConfigurationModule> modules) throws Exception {
        Field modulesField = BaseRouteBuilder.class.getDeclaredField("modules");
        modulesField.setAccessible(true);
        modulesField.set(builder, modules);
    }

    private static Demo2RestSenderRouteBuilder createDemo2RestSenderRouteBuilder() throws Exception {
        DefaultIntegrationExceptionMapperImpl exceptionMapper = new DefaultIntegrationExceptionMapperImpl();
        DefaultRestFailureContractRoutePolicy failurePolicy = new DefaultRestFailureContractRoutePolicy(
          new ObjectMapper(),
          exceptionMapper
        );
        Constructor<Demo2RestSenderRouteBuilder> constructor =
          Demo2RestSenderRouteBuilder.class.getDeclaredConstructor(DefaultRestFailureContractRoutePolicy.class);
        constructor.setAccessible(true);
        return constructor.newInstance(failurePolicy);
    }

    private static DemoSoapSenderRouteBuilder createDemoSoapSenderRouteBuilder() throws Exception {
        DefaultIntegrationExceptionMapperImpl exceptionMapper = new DefaultIntegrationExceptionMapperImpl();
        DemoSoapMessageMapper messageMapper = new DemoSoapMessageMapper(new ObjectMapper());
        DemoSoapFailureContractRoutePolicy failurePolicy = new DemoSoapFailureContractRoutePolicy(
          messageMapper,
          exceptionMapper
        );
        Constructor<DemoSoapSenderRouteBuilder> constructor =
          DemoSoapSenderRouteBuilder.class.getDeclaredConstructor(
            DemoSoapFailureContractRoutePolicy.class,
            DemoSoapMessageMapper.class
          );
        constructor.setAccessible(true);
        return constructor.newInstance(failurePolicy, messageMapper);
    }

    private static final class SyncRouteBuilder extends BaseSyncSenderRouteBuilder {
        private SyncRouteBuilder(FailContractRoutePolicy failureContractRoutePolicy) {
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
