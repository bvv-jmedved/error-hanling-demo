package cz.bvv.errorhandlingdemo.contract.rest.model;

import java.util.List;

/**
 * REST failure-contract component used by sender routes.
 *
 * <p>It demonstrates response shaping that keeps route DSL clean and architecture rules
 * centralized.
 */
public record DefaultRestError(List<Error> errors) {
  public record Error(String code, String message) {}
}
