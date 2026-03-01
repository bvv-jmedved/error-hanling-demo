package cz.bvv.errorhandlingdemo.support.exchange;

public final class IntegrationExchangeHeaders {
    public static final String CORRELATION_ID = "X-Correlation-Id";
    public static final String FLOW_SOURCE = "X-Flow-Source";
    public static final String FLOW_TARGET = "X-Flow-Target";
    public static final String FLOW_PROCESS = "X-Flow-Process";

    private IntegrationExchangeHeaders() {
    }
}
