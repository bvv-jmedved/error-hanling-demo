package cz.bvv.errorhandlingdemo.builder.common.failure;

import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.support.RoutePolicySupport;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class FailureContractRoutePolicy extends RoutePolicySupport {

    private static final String FAILURE_CONTRACT_APPLIED = "failure.contract.applied";
    private static final String INTEGRATION_EXCEPTION_OVERRIDE = "integration.exception.override";

    @Autowired
    private IntegrationExceptionMapper integrationExceptionMapper;

    @Override
    public final void onExchangeDone(Route route, Exchange exchange) {
        if (!exchange.isFailed()) {
            return;
        }
        if (Boolean.TRUE.equals(exchange.getProperty(FAILURE_CONTRACT_APPLIED, Boolean.class))) {
            return;
        }

        IntegrationException integrationException =
          exchange.getProperty(INTEGRATION_EXCEPTION_OVERRIDE, IntegrationException.class);
        if (integrationException == null) {
            Exception exceptionCaught =
              exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
            integrationException = integrationExceptionMapper.map(exceptionCaught);
        }

        mapContract(
          integrationException == null ? IntegrationException.unknownError() : integrationException,
          exchange);
        exchange.setProperty(FAILURE_CONTRACT_APPLIED, true);
        exchange.setException(null);
    }

    protected abstract void mapContract(IntegrationException exception, Exchange exchange);
}
