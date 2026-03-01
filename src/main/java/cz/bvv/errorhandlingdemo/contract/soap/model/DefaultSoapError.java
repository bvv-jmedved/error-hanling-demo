package cz.bvv.errorhandlingdemo.contract.soap.model;

import java.util.List;

/**
 * SOAP failure-contract component used by sender routes.
 *
 * <p>It illustrates centralized SOAP fault generation in the contract layer.
 */
public record DefaultSoapError(List<SoapErrorEntry> errors) {}
