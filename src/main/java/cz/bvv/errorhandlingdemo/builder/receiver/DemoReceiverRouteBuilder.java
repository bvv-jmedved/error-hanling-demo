package cz.bvv.errorhandlingdemo.builder.receiver;

import cz.bvv.errorhandlingdemo.builder.common.BaseReceiverRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.receiver.processor.DownstreamBusinessErrorProcessor;
import cz.bvv.errorhandlingdemo.builder.receiver.retry.TokenRefreshRetryDecider;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.stereotype.Component;

@Component
public class DemoReceiverRouteBuilder extends BaseReceiverRouteBuilder {

    private final TokenRefreshRetryDecider tokenRefreshRetryDecider;
    private final DownstreamBusinessErrorProcessor downstreamBusinessErrorProcessor;

    public DemoReceiverRouteBuilder(
      TokenRefreshRetryDecider tokenRefreshRetryDecider,
      DownstreamBusinessErrorProcessor downstreamBusinessErrorProcessor) {
        this.tokenRefreshRetryDecider = tokenRefreshRetryDecider;
        this.downstreamBusinessErrorProcessor = downstreamBusinessErrorProcessor;
    }

    @Override
    protected void config() {
        onException(HttpOperationFailedException.class)
          .onWhen(simple("${exception.statusCode} == 401"))
          .retryWhile(method(tokenRefreshRetryDecider, "shouldRetry"))
          .redeliveryDelay(100);

        onException(HttpOperationFailedException.class)
          .maximumRedeliveries(2).redeliveryDelay(0)
          .handled(false);

        from("direct:demo-receiver")
          .routeId("demo-receiver")
          .to("direct:technical-receiver")
          .process(downstreamBusinessErrorProcessor);
    }
}
