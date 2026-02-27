package cz.bvv.errorhandlingdemo.builder.sender.demosoap.model;

import java.util.List;

public record SoapError(
  List<Failure> failures
) {
    public record Failure(
      String id,
      String failureText
    ) {
    }
}
