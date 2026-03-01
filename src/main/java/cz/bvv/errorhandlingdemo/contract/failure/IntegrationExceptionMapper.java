package cz.bvv.errorhandlingdemo.contract.failure;

import cz.bvv.errorhandlingdemo.exception.IntegrationException;

/**
 * Failure-contract mapping component in the architecture demo.
 *
 * <p>Types in this package define how technical exceptions are transformed into integration-facing
 * error contracts outside route DSL code.
 */
public interface IntegrationExceptionMapper {
  /**
   * Converts arbitrary technical exception into canonical {@link IntegrationException}.
   *
   * @param exception caught technical exception (may be {@code null})
   * @return canonical integration exception for failure-contract mapping
   */
  IntegrationException map(Exception exception);
}
