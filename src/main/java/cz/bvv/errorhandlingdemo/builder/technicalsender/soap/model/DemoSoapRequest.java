package cz.bvv.errorhandlingdemo.builder.technicalsender.soap.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Data model used by the demo integration contract.
 *
 * <p>This type keeps transport payload structure explicit and decoupled from route logic.
 */
@Setter
@Getter
public class DemoSoapRequest {
  private String payload;
}
