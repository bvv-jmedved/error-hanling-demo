package cz.bvv.errorhandlingdemo.builder.receiver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.bvv.errorhandlingdemo.builder.common.BaseReceiverRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.common.ExchangePropertyKeys;
import cz.bvv.errorhandlingdemo.builder.common.TokenManager;
import cz.bvv.errorhandlingdemo.exception.IntegrationError;
import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class DemoReceiverRouteBuilder extends BaseReceiverRouteBuilder {

    private final TokenManager tokenManager;
    private final ObjectMapper objectMapper;

    public DemoReceiverRouteBuilder(TokenManager tokenManager, ObjectMapper objectMapper) {
        this.tokenManager = tokenManager;
        this.objectMapper = objectMapper;
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
          .to("direct:technical-receiver")
          .process(this::throwIfBusinessErrorIn200Response);
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

    private void throwIfBusinessErrorIn200Response(Exchange exchange) throws Exception {
        String body = exchange.getMessage().getBody(String.class);
        if (body == null || body.isBlank()) {
            return;
        }

        JsonNode jsonBody = objectMapper.readTree(body);
        if (!jsonBody.isObject()) {
            return;
        }

        String errorCode = jsonBody.path("errCode").asText("");
        if (errorCode.isBlank()) {
            return;
        }

        String errorMessage = jsonBody.path("errMsg").asText("Business error");
        throw new IntegrationException(
          HttpStatus.BAD_REQUEST,
          List.of(new IntegrationError(errorCode, errorMessage)),
          errorMessage,
          null
        );
    }
}
