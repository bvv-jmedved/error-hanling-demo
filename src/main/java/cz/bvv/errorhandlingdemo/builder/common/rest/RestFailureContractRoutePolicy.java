package cz.bvv.errorhandlingdemo.builder.common.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.bvv.errorhandlingdemo.builder.common.failure.FailureContractRoutePolicy;
import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Component
@Scope("prototype")
public abstract class RestFailureContractRoutePolicy<T> extends FailureContractRoutePolicy {
    private static final Logger LOG = LoggerFactory.getLogger(RestFailureContractRoutePolicy.class);
    private static final String FALLBACK_ERROR_BODY = """
      {
        "errors": [
          {
            "code": "INTERNAL_ERROR",
            "message": "Internal error while preparing error response"
          }
        ]
      }
      """.trim();
    private static final String FALLBACK_RESPONSE_TEXT = "Internal error while preparing error response";

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void mapContract(IntegrationException exception,  Exchange exchange) {
        Message message = exchange.getMessage();
        try {
            T errorDto = createRestError(exception);
            String body = objectMapper.writeValueAsString(errorDto);

            message.setHeader(Exchange.HTTP_RESPONSE_CODE, exception.getStatus().value());
            message.setHeader(Exchange.HTTP_RESPONSE_TEXT, exception.getMessage());
            message.setHeader(Exchange.CONTENT_TYPE, "application/json");
            message.setBody(body);
        } catch (Exception e) {
            LOG.error("Failed to prepare REST error contract response", e);
            message.setHeader(Exchange.HTTP_RESPONSE_CODE, 500);
            message.setHeader(Exchange.HTTP_RESPONSE_TEXT, FALLBACK_RESPONSE_TEXT);
            message.setHeader(Exchange.CONTENT_TYPE, "application/json");
            message.setBody(FALLBACK_ERROR_BODY);
        }
    }

    protected abstract T createRestError(IntegrationException exception);
}
