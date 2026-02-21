package cz.bvv.errorhanlingdemo.builder.common;

import cz.bvv.errorhanlingdemo.exception.IntegrationException;
import org.apache.camel.Exchange;

public interface ContractMapper {
    void map(Exchange exchange, IntegrationException exception);
}
