package cz.bvv.errorhandlingdemo.support.exchange;

/**
 * Canonical Camel message headers used by the architecture demo.
 *
 * <p>Headers are used for metadata that must travel with messages across route boundaries,
 * including component hops that may create a fresh exchange (for example queue enqueue/dequeue).
 */
public final class IntegrationExchangeHeaders {
  public static final String CORRELATION_ID = "X-Correlation-Id";
  public static final String FLOW_SOURCE = "X-Flow-Source";
  public static final String FLOW_TARGET = "X-Flow-Target";
  public static final String FLOW_PROCESS = "X-Flow-Process";

  private IntegrationExchangeHeaders() {}
}
