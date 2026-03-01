package cz.bvv.errorhandlingdemo.builder.sender.demosoap;

import cz.bvv.errorhandlingdemo.contract.failure.IntegrationExceptionMapper;
import cz.bvv.errorhandlingdemo.contract.failure.BaseFailContractRoutePolicy;
import cz.bvv.errorhandlingdemo.builder.sender.demosoap.model.SoapError;
import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * DemoSoapFailureContractRoutePolicy is part of the Camel error-handling architecture demo.
 *
 * <p>The type is documented to serve as an implementation guide for teams that want
 * to apply the same separation of route DSL, policy composition, and failure contracts.
 */
@Component
@Scope("prototype")
public class DemoSoapFailureContractRoutePolicy extends BaseFailContractRoutePolicy {
    private static final Logger LOG = LoggerFactory.getLogger(DemoSoapFailureContractRoutePolicy.class);
    private static final String FALLBACK_MESSAGE = "Internal error while preparing SOAP error response";
    private final DemoSoapMessageMapper demoSoapMessageMapper;

    public DemoSoapFailureContractRoutePolicy(
      DemoSoapMessageMapper demoSoapMessageMapper,
      IntegrationExceptionMapper integrationExceptionMapper) {
        super(integrationExceptionMapper);
        this.demoSoapMessageMapper = demoSoapMessageMapper;
    }

    @Override
    protected void mapContract(IntegrationException exception, Exchange exchange) {
        Message message = exchange.getMessage();
        try {
            SoapError soapError = createSoapError(exception);
            String faultXml = demoSoapMessageMapper.createFailureResponseXml(exception.getMessage(), soapError);
            message.setHeader(Exchange.CONTENT_TYPE, "text/xml; charset=UTF-8");
            message.setHeader(Exchange.HTTP_RESPONSE_CODE, exception.getStatus().value());
            message.setBody(faultXml);
        } catch (Exception e) {
            LOG.error("Failed to prepare SOAP error contract response", e);
            message.setHeader(Exchange.CONTENT_TYPE, "text/xml; charset=UTF-8");
            message.setHeader(Exchange.HTTP_RESPONSE_CODE, 500);
            message.setBody(createFallbackBody());
        }
    }

    protected SoapError createSoapError(IntegrationException exception) {
        return new SoapError(
          exception.getErrors().stream()
            .map(error -> new SoapError.Failure(error.code(), error.message()))
            .toList()
        );
    }

    private String createFallbackBody() {
        try {
            return demoSoapMessageMapper.createFailureResponseXml(
              FALLBACK_MESSAGE,
              new SoapError(List.of(new SoapError.Failure("INTERNAL_ERROR", FALLBACK_MESSAGE)))
            );
        } catch (Exception internalError) {
            LOG.error("Failed to prepare fallback SOAP error response", internalError);
            return """
              <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                <soap:Body>
                  <soap:Fault>
                    <faultcode>soap:Server</faultcode>
                    <faultstring>Internal error</faultstring>
                    <detail>
                      <soapError><failures><failure><id>INTERNAL_ERROR</id><failureText>Internal error</failureText></failure></failures></soapError>
                    </detail>
                  </soap:Fault>
                </soap:Body>
              </soap:Envelope>
              """.trim();
        }
    }
}
