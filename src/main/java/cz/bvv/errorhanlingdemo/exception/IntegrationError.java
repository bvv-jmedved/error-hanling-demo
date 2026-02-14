package cz.bvv.errorhanlingdemo.exception;

public record IntegrationError(
  String code,
  String message
) {
}
