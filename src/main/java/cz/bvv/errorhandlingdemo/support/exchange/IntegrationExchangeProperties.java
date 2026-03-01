package cz.bvv.errorhandlingdemo.support.exchange;

/**
 * Canonical Camel exchange properties used by policies and contract mapping.
 *
 * <p>Properties mirror selected headers for resilience and cross-step access where direct
 * message headers may not be sufficient.
 */
public final class IntegrationExchangeProperties {
    public static final String CORRELATION_ID = "integration.correlation.id";
    public static final String FLOW_SOURCE = "integration.flow.source";
    public static final String FLOW_TARGET = "integration.flow.target";
    public static final String FLOW_PROCESS = "integration.flow.process";
    public static final String PROCESS_STATUS = "integration.process.status";
    public static final String EXCEPTION_OVERRIDE = "integration.exception.override";
    public static final String FAILURE_CONTRACT_APPLIED = "integration.failure.contract.applied";
    public static final String FAILURE_HANDLED = "integration.failure.handled";
    public static final String POC_HTTP_RESPONSE_BODY_SET = "integration.poc.http.response.body.set";

    private IntegrationExchangeProperties() {
    }
}
