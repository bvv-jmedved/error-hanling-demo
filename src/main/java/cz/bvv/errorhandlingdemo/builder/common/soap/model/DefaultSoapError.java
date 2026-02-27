package cz.bvv.errorhandlingdemo.builder.common.soap.model;

import java.util.List;

public class DefaultSoapError {
    private final List<SoapErrorEntry> errors;

    public DefaultSoapError(List<SoapErrorEntry> errors) {
        this.errors = errors;
    }

    public List<SoapErrorEntry> getErrors() {
        return errors;
    }
}
