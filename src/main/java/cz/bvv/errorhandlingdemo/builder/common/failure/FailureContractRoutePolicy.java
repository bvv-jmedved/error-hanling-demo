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

    private static final Logger LOG = LoggerFactory.getLogger(FailureContractRoutePolicy.class);
    
    @Autowired
    private IntegrationExceptionMapper integrationExceptionMapper;

    @Override
    public final void onExchangeDone(Route route, Exchange exchange) {
        if (!exchange.isFailed()) {
            return;
        }
        if (Boolean.TRUE.equals(exchange.getProperty(IntegrationExchangeProperties.FAILURE_CONTRACT_APPLIED, Boolean.class))) {
            return;
        }

        Exception exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
        if (exception == null) {
            exception = exchange.getException();
        }
        if (exception == null) {
            LOG.warn(
              "Violation: exchange marked failed but no failure source present (EXCEPTION_CAUGHT and getException are null). routeId={}",
              route != null ? route.getId() : "unknown");
        }

        mapContract(
          exception == null ? IntegrationException.unknownError() : integrationExceptionMapper.map(exception),
          exchange);
        exchange.setProperty(IntegrationExchangeProperties.FAILURE_CONTRACT_APPLIED, true);
        exchange.setProperty(IntegrationExchangeProperties.FAILURE_HANDLED, true);
        exchange.setException(null);
    }

    protected abstract void mapContract(IntegrationException exception, Exchange exchange);
}
