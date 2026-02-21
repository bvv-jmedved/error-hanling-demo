package cz.bvv.errorhanlingdemo.builder.sender;

import cz.bvv.errorhanlingdemo.builder.common.ContractMapper;
import cz.bvv.errorhanlingdemo.builder.sender.model.DemoError;
import cz.bvv.errorhanlingdemo.exception.IntegrationException;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class RestContractMapper implements ContractMapper {

    @Override
    public void map(Exchange exchange, IntegrationException exception) {

        exchange.getMessage().setHeader(
          Exchange.HTTP_RESPONSE_CODE,
          exception.getStatus().value()
        );

        DemoError body = new DemoError(
          exception.getErrors().stream()
            .map(e -> new DemoError.Error(e.code(), e.message()))
            .toList()
        );

        exchange.getMessage().setBody(body);
    }
}
