package cz.bvv.errorhandlingdemo.builder.sender.demo1rest.model;

import java.util.List;

/**
 * Data model used by the demo integration contract.
 *
 * <p>This type keeps transport payload structure explicit and decoupled from route logic.
 */
public record Demo1RestError(List<Error> errors) {
  public record Error(String code, String message) {}
}
