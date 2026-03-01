package cz.bvv.errorhandlingdemo.builder.sender.demosoap.model;

/**
 * Data model used by the demo integration contract.
 *
 * <p>This type keeps transport payload structure explicit and decoupled from route logic.
 */
public record SoapFailureTrigger(String step, String type, Integer status, String body) {}
