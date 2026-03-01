package cz.bvv.errorhandlingdemo.contract.soap.model;

import java.util.List;

public record DefaultSoapError(List<SoapErrorEntry> errors) {

}
