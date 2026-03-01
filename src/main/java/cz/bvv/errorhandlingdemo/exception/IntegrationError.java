package cz.bvv.errorhandlingdemo.exception;

/**
 * Canonical integration error model used across policy, contract, and route layers.
 *
 * <p>It provides a stable representation for mapping technical failures to integration responses.
 */
public record IntegrationError(String code, String message) {}
