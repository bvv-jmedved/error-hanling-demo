package cz.bvv.errorhandlingdemo.support.retry;

import cz.bvv.errorhandlingdemo.support.exchange.IntegrationExchangeProperties;
import cz.bvv.errorhandlingdemo.support.auth.TokenManager;
import cz.bvv.errorhandlingdemo.policy.route.logging.CorrelationIdResolver;
import cz.bvv.errorhandlingdemo.policy.route.logging.ExchangeLoggingStatus;
import cz.bvv.errorhandlingdemo.policy.route.logging.RecoveryAction;
import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Retry support component used by receiver-side error handling.
 *
 * <p>The implementation demonstrates how retry decisions are extracted from route DSL
 * into a focused, testable service.
 */
@Component
public class TokenRefreshRetryDecider {
    private static final Logger LOG = LoggerFactory.getLogger(TokenRefreshRetryDecider.class);

    private final TokenManager tokenManager;
    private final CorrelationIdResolver correlationIdResolver;

    public TokenRefreshRetryDecider(
      TokenManager tokenManager,
      CorrelationIdResolver correlationIdResolver) {
        this.tokenManager = tokenManager;
        this.correlationIdResolver = correlationIdResolver;
    }

    @SuppressWarnings("unused")
    public boolean shouldRetry(Exchange exchange) {
        Integer counter = exchange.getIn()
          .getHeader(Exchange.REDELIVERY_COUNTER, Integer.class);

        if (counter == null || counter != 1) {
            return false;
        }

        try {
            logIntermediate(exchange, ExchangeLoggingStatus.RUNNING, RecoveryAction.TOKEN_REFRESH_START);
            tokenManager.refreshToken(exchange);
            logIntermediate(exchange, ExchangeLoggingStatus.RUNNING, RecoveryAction.TOKEN_REFRESH_OK);
            logIntermediate(exchange, ExchangeLoggingStatus.RUNNING, RecoveryAction.RETRY_AFTER_REFRESH);
            return true;
        } catch (IntegrationException ie) {
            logIntermediate(exchange, ExchangeLoggingStatus.FAILURE, RecoveryAction.TOKEN_REFRESH_FAILED);
            exchange.setProperty(IntegrationExchangeProperties.EXCEPTION_OVERRIDE, ie);
            return false;
        }
    }

    private void logIntermediate(Exchange exchange, ExchangeLoggingStatus status, RecoveryAction action) {
        String correlationId = correlationIdResolver.ensureCorrelationId(exchange);
        String routeId = exchange.getFromRouteId() == null ? "unknown" : exchange.getFromRouteId();
        LOG.info(
          "event=process_detail detailState={} recoveryAction={} routeId={} correlationId={}",
          status,
          action,
          routeId,
          correlationId
        );
    }
}
