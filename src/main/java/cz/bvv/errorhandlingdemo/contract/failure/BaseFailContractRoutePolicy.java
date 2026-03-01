package cz.bvv.errorhandlingdemo.contract.failure;

import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import cz.bvv.errorhandlingdemo.policy.route.FailContractRoutePolicy;
import cz.bvv.errorhandlingdemo.support.exchange.IntegrationExchangeProperties;
import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.support.RoutePolicySupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base fail-contract policy that maps failures to integration-facing error contracts.
 *
 * <p>IMPORTANT: This policy clears {@link Exchange#getException()} after contract mapping.
 * Downstream interceptors and policies will not see the original exception instance.
 */
public abstract class BaseFailContractRoutePolicy extends RoutePolicySupport
    implements FailContractRoutePolicy {

  private static final Logger LOGGER = LoggerFactory.getLogger(BaseFailContractRoutePolicy.class);

  private final IntegrationExceptionMapper integrationExceptionMapper;

  protected BaseFailContractRoutePolicy(IntegrationExceptionMapper integrationExceptionMapper) {
    this.integrationExceptionMapper = integrationExceptionMapper;
  }

  @Override
  public final void onExchangeDone(Route route, Exchange exchange) {
    if (!exchange.isFailed()) {
      if (exchange.getProperty(IntegrationExchangeProperties.EXCEPTION_OVERRIDE) != null) {
        LOGGER.warn(
            "Ignoring {} because exchange is not failed",
            IntegrationExchangeProperties.EXCEPTION_OVERRIDE);
      }
      return;
    }
    if (Boolean.TRUE.equals(
        exchange.getProperty(
            IntegrationExchangeProperties.FAILURE_CONTRACT_APPLIED, Boolean.class))) {
      return;
    }

    Object override = exchange.getProperty(IntegrationExchangeProperties.EXCEPTION_OVERRIDE);
    IntegrationException integrationException =
        override instanceof IntegrationException ? (IntegrationException) override : null;
    if (integrationException == null) {
      Exception exceptionCaught = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
      integrationException = integrationExceptionMapper.map(exceptionCaught);
    }

    mapContract(
        integrationException == null ? IntegrationException.unknownError() : integrationException,
        exchange);
    exchange.setProperty(IntegrationExchangeProperties.FAILURE_CONTRACT_APPLIED, true);
    exchange.setProperty(IntegrationExchangeProperties.FAILURE_HANDLED, true);
    exchange.setException(null);
  }

  protected abstract void mapContract(IntegrationException exception, Exchange exchange);
}
