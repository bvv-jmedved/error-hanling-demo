package cz.bvv.errorhandlingdemo.builder.sender.model;

import java.util.List;

public record DemoError(
  List<Error> errors
) {
    public record Error(
      String code,
      String message
    ) {
    }
}
