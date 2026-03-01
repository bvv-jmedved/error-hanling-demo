package cz.bvv.errorhandlingdemo.builder.common.routepolicy;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.bvv.errorhandlingdemo.builder.common.BaseRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.common.BaseSyncSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.common.failure.DefaultIntegrationExceptionMapperImpl;
import cz.bvv.errorhandlingdemo.builder.common.failure.BaseFailContractRoutePolicy;
import cz.bvv.errorhandlingdemo.builder.common.failure.IntegrationExceptionMapper;
import cz.bvv.errorhandlingdemo.builder.common.rest.DefaultRestFailureContractRoutePolicy;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.FailContractRoutePolicy;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging.ExchangeLoggingStatus;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging.LoggingRoutePolicy;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging.LoggingRoutePolicyFactory;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.module.FailContractModule;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.module.LoggingModule;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging.CorrelationIdResolver;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging.ExchangeOutcomeResolver;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging.ProcessStatusTracker;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging.RecoveryActionResolver;
import cz.bvv.errorhandlingdemo.builder.sender.demo2rest.Demo2RestSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.sender.demosoap.DemoSoapFailureContractRoutePolicy;
import cz.bvv.errorhandlingdemo.builder.sender.demosoap.DemoSoapMessageMapper;
import cz.bvv.errorhandlingdemo.builder.sender.demosoap.DemoSoapSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.technicalsender.DemoSoapTechnicalSender;
import cz.bvv.errorhandlingdemo.poc.FailureStepProcessorFactory;
import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import java.lang.reflect.Constructor;
import java.util.List;
import lombok.NonNull;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.RoutePolicy;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class RoutePolicyCompositionTest {

    @Test
    void shouldApplyFailContractThenLoggingOnSyncRouteBuilder() {
        SyncRouteBuilder
          builder = getSyncRouteBuilder();

        builder.configure();

        List<RoutePolicy> policies = builder.getRouteCollection().getRoutes().getFirst().getRoutePolicies();
        assertThat(policies).hasSize(2);
        assertThat(policies.get(0)).isInstanceOf(DummyFailureContractRoutePolicy.class);
        assertThat(policies.get(1)).isInstanceOf(LoggingRoutePolicy.class);
    }

    private @NonNull SyncRouteBuilder getSyncRouteBuilder() {
        List<RouteConfigurationModule> modules = List.of(
          new FailContractModule(),
          new LoggingModule(loggingRoutePolicyFactory())
        );
        SyncRouteBuilder builder = new SyncRouteBuilder(
          modules,
          new DummyFailureContractRoutePolicy(exception -> new IntegrationException(
            HttpStatus.BAD_GATEWAY,
            "MAPPED",
            "mapped",
            exception
          ))
        );
        builder.setCamelContext(new DefaultCamelContext());
        return builder;
    }

    @Test
    void shouldApplyOnlyLoggingOnNonSyncRouteBuilder() {
        List<RouteConfigurationModule> modules = List.of(
          new FailContractModule(),
          new LoggingModule(loggingRoutePolicyFactory())
        );
        NonSyncRouteBuilder builder = new NonSyncRouteBuilder(modules);
        builder.setCamelContext(new DefaultCamelContext());

        builder.configure();

        List<RoutePolicy> policies = builder.getRouteCollection().getRoutes().getFirst().getRoutePolicies();
        assertThat(policies).hasSize(1);
        assertThat(policies.getFirst()).isInstanceOf(LoggingRoutePolicy.class);
    }

    @Test
    void shouldApplyConfiguredMonitoringMetadataOnRestSenderRouteBuilder() throws Exception {
        List<RouteConfigurationModule> modules = List.of(
          new FailContractModule(),
          new LoggingModule(loggingRoutePolicyFactory())
        );
        Demo2RestSenderRouteBuilder builder = createDemo2RestSenderRouteBuilder(modules);
        builder.setCamelContext(new DefaultCamelContext());

        builder.configure();

        List<RoutePolicy> policies = builder.getRouteCollection().getRoutes().getFirst().getRoutePolicies();
        assertThat(policies).hasSize(2);
        assertThat(policies.get(0)).isInstanceOf(BaseFailContractRoutePolicy.class);
        assertThat(policies.get(1)).isInstanceOf(LoggingRoutePolicy.class);
        LoggingRoutePolicy loggingPolicy = (LoggingRoutePolicy) policies.get(1);
        assertThat(loggingPolicy.getService()).isEqualTo("sender-demo2-rest");
        assertThat(loggingPolicy.isCritical()).isFalse();
        assertThat(loggingPolicy.getLoggingStatus().onStart()).isEqualTo(ExchangeLoggingStatus.RUNNING);
        assertThat(loggingPolicy.getLoggingStatus().onComplete()).isEqualTo(ExchangeLoggingStatus.RUNNING);
        assertThat(loggingPolicy.getLoggingStatus().onFailure()).isEqualTo(ExchangeLoggingStatus.FAILURE);
    }

    @Test
    void shouldApplyConfiguredMonitoringMetadataOnSoapSenderRouteBuilder() throws Exception {
        List<RouteConfigurationModule> modules = List.of(
          new FailContractModule(),
          new LoggingModule(loggingRoutePolicyFactory())
        );
        DemoSoapSenderRouteBuilder builder = createDemoSoapSenderRouteBuilder(modules);
        builder.setCamelContext(new DefaultCamelContext());

        builder.configure();

        List<RoutePolicy> policies = builder.getRouteCollection().getRoutes().getFirst().getRoutePolicies();
        assertThat(policies).hasSize(2);
        assertThat(policies.get(0)).isInstanceOf(BaseFailContractRoutePolicy.class);
        assertThat(policies.get(1)).isInstanceOf(LoggingRoutePolicy.class);
        LoggingRoutePolicy loggingPolicy = (LoggingRoutePolicy) policies.get(1);
        assertThat(loggingPolicy.getService()).isEqualTo("sender-demo-soap");
        assertThat(loggingPolicy.isCritical()).isFalse();
        assertThat(loggingPolicy.getLoggingStatus().onStart()).isEqualTo(ExchangeLoggingStatus.RUNNING);
        assertThat(loggingPolicy.getLoggingStatus().onComplete()).isEqualTo(ExchangeLoggingStatus.RUNNING);
        assertThat(loggingPolicy.getLoggingStatus().onFailure()).isEqualTo(ExchangeLoggingStatus.FAILURE);
    }

    @Test
    void shouldApplyConfiguredMonitoringMetadataOnTechnicalIngressRouteBuilder() {
        List<RouteConfigurationModule> modules = List.of(
          new FailContractModule(),
          new LoggingModule(loggingRoutePolicyFactory())
        );
        DemoSoapTechnicalSender builder = new DemoSoapTechnicalSender(modules);
        builder.setCamelContext(new DefaultCamelContext());

        builder.configure();

        List<RoutePolicy> policies = builder.getRouteCollection().getRoutes().getFirst().getRoutePolicies();
        assertThat(policies).hasSize(1);
        assertThat(policies.getFirst()).isInstanceOf(LoggingRoutePolicy.class);
        LoggingRoutePolicy loggingPolicy = (LoggingRoutePolicy) policies.getFirst();
        assertThat(loggingPolicy.getService()).isEqualTo("technical-sender-demo-soap");
        assertThat(loggingPolicy.isCritical()).isTrue();
        assertThat(loggingPolicy.getLoggingStatus().onStart()).isEqualTo(ExchangeLoggingStatus.STARTED);
        assertThat(loggingPolicy.getLoggingStatus().onComplete()).isEqualTo(ExchangeLoggingStatus.COMPLETED);
        assertThat(loggingPolicy.getLoggingStatus().onFailure()).isEqualTo(ExchangeLoggingStatus.FAILED);
    }

    private static Demo2RestSenderRouteBuilder createDemo2RestSenderRouteBuilder(List<RouteConfigurationModule> modules) throws Exception {
        DefaultIntegrationExceptionMapperImpl exceptionMapper = new DefaultIntegrationExceptionMapperImpl();
        DefaultRestFailureContractRoutePolicy failurePolicy = new DefaultRestFailureContractRoutePolicy(
          new ObjectMapper(),
          exceptionMapper
        );
        Constructor<Demo2RestSenderRouteBuilder> constructor =
          Demo2RestSenderRouteBuilder.class.getDeclaredConstructor(
            List.class,
            DefaultRestFailureContractRoutePolicy.class,
            FailureStepProcessorFactory.class
          );
        constructor.setAccessible(true);
        FailureStepProcessorFactory failureStepProcessorFactory = step -> exchange -> {
        };
        return constructor.newInstance(modules, failurePolicy, failureStepProcessorFactory);
    }

    private static LoggingRoutePolicyFactory loggingRoutePolicyFactory() {
        return new LoggingRoutePolicyFactory(
          new ExchangeOutcomeResolver(),
          new CorrelationIdResolver(),
          new ProcessStatusTracker(),
          new RecoveryActionResolver()
        );
    }

    private static DemoSoapSenderRouteBuilder createDemoSoapSenderRouteBuilder(List<RouteConfigurationModule> modules) throws Exception {
        DefaultIntegrationExceptionMapperImpl exceptionMapper = new DefaultIntegrationExceptionMapperImpl();
        DemoSoapMessageMapper messageMapper = new DemoSoapMessageMapper(new ObjectMapper());
        DemoSoapFailureContractRoutePolicy failurePolicy = new DemoSoapFailureContractRoutePolicy(
          messageMapper,
          exceptionMapper
        );
        Constructor<DemoSoapSenderRouteBuilder> constructor =
          DemoSoapSenderRouteBuilder.class.getDeclaredConstructor(
            List.class,
            DemoSoapFailureContractRoutePolicy.class,
            DemoSoapMessageMapper.class,
            FailureStepProcessorFactory.class
          );
        constructor.setAccessible(true);
        FailureStepProcessorFactory failureStepProcessorFactory = step -> exchange -> {
        };
        return constructor.newInstance(
          modules,
          failurePolicy,
          messageMapper,
          failureStepProcessorFactory
        );
    }

    private static final class SyncRouteBuilder extends BaseSyncSenderRouteBuilder {
        private SyncRouteBuilder(List<RouteConfigurationModule> modules,
          FailContractRoutePolicy failureContractRoutePolicy) {
            super(modules, failureContractRoutePolicy);
        }

        @Override
        protected void configureRoutes() {
            from("direct:route-policy-composition-sync")
              .routeId("route-policy-composition-sync")
              .to("mock:sync");
        }
    }

    private static final class NonSyncRouteBuilder extends BaseRouteBuilder {
        private NonSyncRouteBuilder(List<RouteConfigurationModule> modules) {
            super(modules);
        }

        @Override
        protected void configureRoutes() {
            from("direct:route-policy-composition-nonsync")
              .routeId("route-policy-composition-nonsync")
              .to("mock:nonsync");
        }
    }

    private static final class DummyFailureContractRoutePolicy extends BaseFailContractRoutePolicy {
        private DummyFailureContractRoutePolicy(IntegrationExceptionMapper integrationExceptionMapper) {
            super(integrationExceptionMapper);
        }

        @Override
        protected void mapContract(IntegrationException exception, Exchange exchange) {
            exchange.getMessage().setBody(exception.getMessage());
        }
    }
}
