package cz.bvv.errorhandlingdemo.builder.sender.demosoap.model;

public record SoapFailureTrigger(
  String step,
  String type,
  Integer status,
  String body
) {
}
