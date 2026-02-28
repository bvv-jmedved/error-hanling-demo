package cz.bvv.errorhandlingdemo.builder.common.soap.model;

import java.util.List;

public record DefaultSoapError(List<SoapErrorEntry> errors) {

}
