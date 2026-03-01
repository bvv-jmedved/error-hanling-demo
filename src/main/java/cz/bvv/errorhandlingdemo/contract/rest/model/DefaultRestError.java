package cz.bvv.errorhandlingdemo.contract.rest.model;

import java.util.List;

public record DefaultRestError(
  List<Error> errors
) {
    public record Error(
      String code,
      String message
    ) {
    }
}
