package cz.bvv.errorhandlingdemo.contract.soap;

import cz.bvv.errorhandlingdemo.contract.failure.BaseFailContractRoutePolicy;
import cz.bvv.errorhandlingdemo.contract.failure.IntegrationExceptionMapper;
import cz.bvv.errorhandlingdemo.contract.soap.model.SoapErrorEntry;
import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract fail-contract policy for SOAP fault responses.
 *
 * <p>It centralizes SOAP fault XML creation and provides a fallback SOAP envelope when contract
 * generation fails.
 *
 * @param <T> DTO type representing SOAP error payload contract
 */
public abstract class SoapFailureContractRoutePolicy<T> extends BaseFailContractRoutePolicy {
  private static final Logger LOG = LoggerFactory.getLogger(SoapFailureContractRoutePolicy.class);
  private static final String FALLBACK_MESSAGE =
      "Internal error while preparing SOAP error response";

  private final SoapFaultMessageMapper soapFaultMessageMapper;

  protected SoapFailureContractRoutePolicy(
      SoapFaultMessageMapper soapFaultMessageMapper,
      IntegrationExceptionMapper integrationExceptionMapper) {
    super(integrationExceptionMapper);
    this.soapFaultMessageMapper = soapFaultMessageMapper;
  }

  @Override
  protected void mapContract(IntegrationException exception, Exchange exchange) {
    Message message = exchange.getMessage();
    try {
      T errorDto = createSoapError(exception);
      String faultXml =
          soapFaultMessageMapper.toSoapFaultXml(exception.getMessage(), getSoapErrors(errorDto));

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

  private String createFallbackBody() {
    try {
      return soapFaultMessageMapper.toSoapFaultXml(
          FALLBACK_MESSAGE, List.of(new SoapErrorEntry("INTERNAL_ERROR", FALLBACK_MESSAGE)));
    } catch (Exception internalError) {
      LOG.error("Failed to prepare fallback SOAP error response", internalError);
      return """
              <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                <soap:Body>
                  <soap:Fault>
                    <faultcode>soap:Server</faultcode>
                    <faultstring>Internal error</faultstring>
                    <detail>
                      <errors><error><code>INTERNAL_ERROR</code><message>Internal error</message></error></errors>
                    </detail>
                  </soap:Fault>
                </soap:Body>
              </soap:Envelope>
              """
          .trim();
    }
  }

  /**
   * Creates the transport error DTO from canonical {@link IntegrationException}.
   *
   * @param exception canonical integration exception
   * @return transport-specific SOAP error DTO
   */
  protected abstract T createSoapError(IntegrationException exception);

  /**
   * Extracts flat SOAP fault entries from a transport error DTO.
   *
   * @param errorDto transport-specific SOAP error DTO
   * @return list of SOAP fault entries used by {@link SoapFaultMessageMapper}
   */
  protected abstract List<SoapErrorEntry> getSoapErrors(T errorDto);
}
