package cz.bvv.errorhandlingdemo.contract.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.bvv.errorhandlingdemo.contract.failure.BaseFailContractRoutePolicy;
import cz.bvv.errorhandlingdemo.contract.failure.IntegrationExceptionMapper;
import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Abstract fail-contract policy for JSON/REST responses.
 *
 * <p>It centralizes HTTP status/body mapping and provides a safe fallback response when
 * contract serialization fails.
 *
 * @param <T> DTO type representing REST error payload contract
 */
@Component
@Scope("prototype")
public abstract class RestFailureContractRoutePolicy<T> extends BaseFailContractRoutePolicy {
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

    private final ObjectMapper objectMapper;

    protected RestFailureContractRoutePolicy(
      ObjectMapper objectMapper,
      IntegrationExceptionMapper integrationExceptionMapper) {
        super(integrationExceptionMapper);
        this.objectMapper = objectMapper;
    }

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

    /**
     * Creates the transport error DTO from canonical {@link IntegrationException}.
     *
     * @param exception canonical integration exception
     * @return transport-specific REST error DTO
     */
    protected abstract T createRestError(IntegrationException exception);
}
