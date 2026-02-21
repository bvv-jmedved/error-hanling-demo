package cz.bvv.errorhanlingdemo.builder.common;

import cz.bvv.errorhanlingdemo.exception.IntegrationException;
import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.spi.RoutePolicy;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class FailureContractRoutePolicy implements RoutePolicy {

    @Autowired
    private IntegrationExceptionMapper integrationExceptionMapper;

    @Override
    public final void onInit(Route route) {
        
    }

    @Override
    public final void onRemove(Route route) {

    }

    @Override
    public final void onStart(Route route) {

    }

    @Override
    public final void onStop(Route route) {

    }

    @Override
    public final void onSuspend(Route route) {

    }

    @Override
    public final void onResume(Route route) {

    }

    @Override
    public final void onExchangeBegin(Route route, Exchange exchange) {

    }

    @Override
    public final void onExchangeDone(Route route, Exchange exchange) {
        IntegrationException integrationException = integrationExceptionMapper
          .map(getExceptionCaught(exchange));
        if (exchange.isFailed()) {
            mapContract(integrationException, exchange);
            exchange.setException(null);
        }
    }

    private Exception getExceptionCaught(Exchange exchange) {
           return exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
    }

    protected abstract void mapContract(IntegrationException exception, Exchange exchange);


}
