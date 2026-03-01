package cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging;

import cz.bvv.errorhandlingdemo.builder.common.IntegrationExchangeProperties;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class RecoveryActionResolver {

    public RecoveryAction currentAction(Exchange exchange) {
        RecoveryAction action = exchange.getProperty(
          IntegrationExchangeProperties.RECOVERY_ACTION,
          RecoveryAction.class
        );
        return action == null ? RecoveryAction.NONE : action;
    }

    public void markAction(Exchange exchange, RecoveryAction action) {
        exchange.setProperty(IntegrationExchangeProperties.RECOVERY_ACTION, action);
    }
}
