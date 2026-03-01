package cz.bvv.errorhandlingdemo.builder.sender.demosoap.model;

import java.util.List;

/**
 * Data model used by the demo integration contract.
 *
 * <p>This type keeps transport payload structure explicit and decoupled from route logic.
 */
public record SoapError(
  List<Failure> failures
) {
    public record Failure(
      String id,
      String failureText
    ) {
    }
}
