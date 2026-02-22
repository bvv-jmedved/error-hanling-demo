package cz.bvv.errorhandlingdemo.exception;

public record IntegrationError(
  String code,
  String message
) {
}
