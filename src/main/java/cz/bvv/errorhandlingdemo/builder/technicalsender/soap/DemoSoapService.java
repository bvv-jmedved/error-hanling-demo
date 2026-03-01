package cz.bvv.errorhandlingdemo.builder.technicalsender.soap;

import cz.bvv.errorhandlingdemo.builder.sender.demosoap.model.DemoSoapRequest;
import cz.bvv.errorhandlingdemo.builder.sender.demosoap.model.DemoSoapResponse;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;

/**
 * DemoSoapService is part of the Camel error-handling architecture demo.
 *
 * <p>The type is documented to serve as an implementation guide for teams that want to apply the
 * same separation of route DSL, policy composition, and failure contracts.
 */
@WebService(targetNamespace = "urn:error-handling-demo:soap")
public interface DemoSoapService {
  @WebMethod(operationName = "processDemo")
  @WebResult(name = "response")
  DemoSoapResponse processDemo(@WebParam(name = "request") DemoSoapRequest request);
}
