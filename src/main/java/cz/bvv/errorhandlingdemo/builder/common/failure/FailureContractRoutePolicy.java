package cz.bvv.errorhandlingdemo.builder.common.failure;

import cz.bvv.errorhandlingdemo.builder.common.IntegrationExchangeProperties;
import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.support.RoutePolicySupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class FailureContractRoutePolicy extends RoutePolicySupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(FailureContractRoutePolicy.class);

    @Autowired
    private IntegrationExceptionMapper integrationExceptionMapper;

    @Override
    public final void onExchangeDone(Route route, Exchange exchange) {
        if (!exchange.isFailed()) {
            if (exchange.getProperty(IntegrationExchangeProperties.EXCEPTION_OVERRIDE) != null) {
                LOGGER.warn("Ignoring {} because exchange is not failed", IntegrationExchangeProperties.EXCEPTION_OVERRIDE);
            }
            return;
        }
        if (Boolean.TRUE.equals(exchange.getProperty(IntegrationExchangeProperties.FAILURE_CONTRACT_APPLIED, Boolean.class))) {
            return;
        }

        Object override = exchange.getProperty(IntegrationExchangeProperties.EXCEPTION_OVERRIDE);
        IntegrationException integrationException = override instanceof IntegrationException ?
          (IntegrationException) override :
          null;
        if (integrationException == null) {
            Exception exceptionCaught =
              exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
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
