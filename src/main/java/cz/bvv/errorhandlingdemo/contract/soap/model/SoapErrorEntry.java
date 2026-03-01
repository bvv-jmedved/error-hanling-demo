package cz.bvv.errorhandlingdemo.contract.soap.model;

/**
 * SOAP failure-contract component used by sender routes.
 *
 * <p>It illustrates centralized SOAP fault generation in the contract layer.
 */
public record SoapErrorEntry(String code, String message) {
}
