package cz.bvv.errorhanlingdemo.exception;

public interface ExceptionNormalizer<T extends Exception> {
    IntegrationException normalize(T exception);
}
