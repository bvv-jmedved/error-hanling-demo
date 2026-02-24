package cz.bvv.errorhandlingdemo.builder.receiver.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.bvv.errorhandlingdemo.exception.IntegrationError;
import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class DownstreamBusinessErrorProcessor implements Processor {

    private final ObjectMapper objectMapper;

    public DownstreamBusinessErrorProcessor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
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
