package cz.bvv.errorhandlingdemo.support.auth;

import org.apache.camel.Exchange;

/**
 * Shared support utility used by the architecture demo.
 *
 * <p>Support types isolate reusable technical concerns from route DSL and contract mapping.
 */
public interface TokenManager {
  /**
   * Refreshes authentication token for the current integration exchange context.
   *
   * @param exchange Camel exchange that requires renewed authentication
   */
  void refreshToken(Exchange exchange);
}
