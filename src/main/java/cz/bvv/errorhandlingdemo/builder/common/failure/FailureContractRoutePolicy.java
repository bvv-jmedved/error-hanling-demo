package cz.bvv.errorhandlingdemo.builder.common.failure;

import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.support.RoutePolicySupport;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class FailureContractRoutePolicy extends RoutePolicySupport {

    @Autowired
    private IntegrationExceptionMapper integrationExceptionMapper;

    @Override
    public final void onExchangeDone(Route route, Exchange exchange) {
        if (!exchange.isFailed()) {
            return;
        }

        Exception exceptionCaught =
          exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);

        IntegrationException integrationException =
          integrationExceptionMapper.map(exceptionCaught);

        mapContract(
          integrationException == null ? IntegrationException.unknownError() : integrationException,
          exchange);
        exchange.setException(null);
    }

    protected abstract void mapContract(IntegrationException exception, Exchange exchange);


}
