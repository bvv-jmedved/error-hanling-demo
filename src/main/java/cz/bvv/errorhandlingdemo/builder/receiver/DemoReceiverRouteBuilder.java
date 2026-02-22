package cz.bvv.errorhandlingdemo.builder.receiver;

import cz.bvv.errorhandlingdemo.builder.common.BaseReceiverRouteBuilder;
import cz.bvv.errorhandlingdemo.poc.FakeTokenManager;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.stereotype.Component;

@Component
public class DemoReceiverRouteBuilder extends BaseReceiverRouteBuilder {
    private final FakeTokenManager fakeTokenManager = new FakeTokenManager();
    private final TokenRefreshOnUnauthorizedProcessor tokenRefreshOnUnauthorizedProcessor =
      new TokenRefreshOnUnauthorizedProcessor(fakeTokenManager);

    @Override
    protected void config() {
        onException(HttpOperationFailedException.class)
          .onWhen(simple("${exception.statusCode} == 401"))
          .maximumRedeliveries(1).redeliveryDelay(0)
          .onRedelivery(tokenRefreshOnUnauthorizedProcessor)
          .handled(false);

        onException(HttpOperationFailedException.class)
          .maximumRedeliveries(2).redeliveryDelay(0)
          .handled(false);

        from("direct:demo-receiver")
          .routeId("demo-receiver")
          .to("direct:technical-receiver");
    }
}
