package cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LoggigPolicyAliasTest {

    @Test
    void shouldBehaveAsLoggingRoutePolicyAlias() {
        LoggigPolicy policy = new LoggigPolicy(
          "demo-service",
          true,
          LoggingStatusConfig.defaults(),
          new ExchangeOutcomeResolver(),
          new CorrelationIdResolver(),
          new RecoveryActionResolver()
        );

        assertThat(policy).isInstanceOf(LoggingRoutePolicy.class);
        assertThat(policy.getService()).isEqualTo("demo-service");
        assertThat(policy.isCritical()).isTrue();
    }
}
