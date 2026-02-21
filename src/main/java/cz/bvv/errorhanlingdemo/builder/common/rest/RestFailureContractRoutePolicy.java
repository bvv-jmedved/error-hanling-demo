package cz.bvv.errorhanlingdemo.builder.common.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.bvv.errorhanlingdemo.builder.common.FailureContractRoutePolicy;
import cz.bvv.errorhanlingdemo.exception.IntegrationException;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public abstract class RestFailureContractRoutePolicy<T> extends FailureContractRoutePolicy {
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void mapContract(IntegrationException exception, Route route, Exchange exchange) {
        Message message = exchange.getMessage();
        message.setHeader(Exchange.HTTP_RESPONSE_CODE, exception.getStatus().value());
        message.setHeader(Exchange.HTTP_RESPONSE_TEXT, exception.getMessage());
        message.setHeader(Exchange.CONTENT_TYPE, "application/json");

        try {
            message.setBody(objectMapper
              .writeValueAsString(createRestError(exception)));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot marshal DefaultRestError to JSON response body",
              e);
        }
    }

    protected abstract T createRestError(IntegrationException exception);
}
