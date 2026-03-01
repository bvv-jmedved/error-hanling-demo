package cz.bvv.errorhandlingdemo.contract.failure;

import cz.bvv.errorhandlingdemo.exception.IntegrationException;

/**
 * Failure-contract mapping component in the architecture demo.
 *
 * <p>Types in this package define how technical exceptions are transformed into integration-facing
 * error contracts outside route DSL code.
 */
public interface FailureContractMapper<T> {
  /**
   * Maps canonical exception data to a transport-specific error DTO.
   *
   * @param integrationException canonical integration exception
   * @return contract DTO for the transport layer
   */
  T map(IntegrationException integrationException);
}
