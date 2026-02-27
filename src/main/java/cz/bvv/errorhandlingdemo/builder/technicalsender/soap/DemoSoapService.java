package cz.bvv.errorhandlingdemo.builder.technicalsender.soap;

import cz.bvv.errorhandlingdemo.builder.sender.demosoap.model.DemoSoapRequest;
import cz.bvv.errorhandlingdemo.builder.sender.demosoap.model.DemoSoapResponse;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;

@WebService(targetNamespace = "urn:error-handling-demo:soap")
public interface DemoSoapService {
    @WebMethod(operationName = "processDemo")
    @WebResult(name = "response")
    DemoSoapResponse processDemo(@WebParam(name = "request") DemoSoapRequest request);
}
