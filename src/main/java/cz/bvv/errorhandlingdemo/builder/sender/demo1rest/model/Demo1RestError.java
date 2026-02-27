package cz.bvv.errorhandlingdemo.builder.sender.demo1rest.model;

import java.util.List;

public record Demo1RestError(
  List<Error> errors
) {
    public record Error (
      String code,
      String message
    ) {}
}
