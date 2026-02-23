package cz.bvv.errorhandlingdemo.builder.receiver;

import cz.bvv.errorhandlingdemo.builder.common.BaseReceiverRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.common.ExchangePropertyKeys;
import cz.bvv.errorhandlingdemo.builder.common.TokenManager;
import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import org.apache.camel.Exchange;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.stereotype.Component;

@Component
public class DemoReceiverRouteBuilder extends BaseReceiverRouteBuilder {

    private final TokenManager tokenManager;

    public DemoReceiverRouteBuilder(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    protected void config() {
        onException(HttpOperationFailedException.class)
          .onWhen(simple("${exception.statusCode} == 401"))
          .retryWhile(method(this, "refreshAndCheckRetry"))
          .redeliveryDelay(100);

        onException(HttpOperationFailedException.class)
          .maximumRedeliveries(2).redeliveryDelay(0)
          .handled(false);

        from("direct:demo-receiver")
          .routeId("demo-receiver")
          .to("direct:technical-receiver");
    }

    @SuppressWarnings("unused")
    public boolean refreshAndCheckRetry(Exchange exchange) {

        Integer counter = exchange.getMessage()
          .getHeader(Exchange.REDELIVERY_COUNTER, Integer.class);

        if (counter == null || counter != 1) {
            return false;
        }

        try {
            tokenManager.refreshToken(exchange);
            return true;
        } catch (IntegrationException ie) {
            exchange.setProperty(ExchangePropertyKeys.INTEGRATION_EXCEPTION_OVERRIDE, ie);
            return false;
        }
    }
}
