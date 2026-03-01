package cz.bvv.errorhandlingdemo.builder.sender.demosoap;

import static org.assertj.core.api.Assertions.assertThat;

import cz.bvv.errorhandlingdemo.builder.technicalsender.DemoSoapTechnicalSender;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.w3c.dom.Document;

/**
 * DemoSoapDefaultFailureContractIntegrationTest integration test.
 *
 * <p>Verifies end-to-end route behavior across policy, retry, and failure-contract boundaries and
 * documents expected architecture behavior.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("poc")
class DemoSoapDefaultFailureContractIntegrationTest {

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;

  @Test
  void shouldReturnSoapSuccessOnHappyPath() throws Exception {
    ResponseEntity<String> response = callSoap(buildRequestEnvelope(null, null, null));

    assertThat(response.getBody()).isNotBlank();
    assertThat(response.getHeaders().getContentType()).isNotNull();
    assertThat(response.getHeaders().getContentType().toString()).contains("xml");
    assertThat(xpath(response.getBody(), "//*[local-name()='Fault']")).isEmpty();
    assertThat(xpath(response.getBody(), "//*[local-name()='status']/text()")).isEqualTo("ok");
  }

  @Test
  void shouldReturnSoapFaultWithValidationFailedCodeForSenderValidationFailure() throws Exception {
    ResponseEntity<String> response =
        callSoap(buildRequestEnvelope("sender-validate", "business-validation", null));

    assertThat(response.getBody()).isNotBlank();
    assertThat(xpath(response.getBody(), "//*[local-name()='Fault']")).isNotBlank();
    assertThat(xpath(response.getBody(), "//*[local-name()='faultstring']/text()")).isNotBlank();
    assertThat(
            xpath(
                response.getBody(), "(//*[local-name()='failure'])[1]/*[local-name()='id']/text()"))
        .isEqualTo("VALIDATION_FAILED");
    assertThat(
            xpath(
                response.getBody(),
                "(//*[local-name()='failure'])[1]/*[local-name()='failureText']/text()"))
        .isNotBlank();
  }

  @Test
  void shouldReturnSoapFaultWithDownstreamHttp500CodeForTechnicalFailure() throws Exception {
    ResponseEntity<String> response = callSoap(buildRequestEnvelope("technical-call", "http", 500));

    assertThat(response.getBody()).isNotBlank();
    assertThat(xpath(response.getBody(), "//*[local-name()='Fault']")).isNotBlank();
    assertThat(xpath(response.getBody(), "//*[local-name()='faultstring']/text()")).isNotBlank();
    assertThat(
            xpath(
                response.getBody(), "(//*[local-name()='failure'])[1]/*[local-name()='id']/text()"))
        .isEqualTo("DOWNSTREAM_HTTP_500");
    assertThat(
            xpath(
                response.getBody(),
                "(//*[local-name()='failure'])[1]/*[local-name()='failureText']/text()"))
        .isNotBlank();
  }

  private ResponseEntity<String> callSoap(String soapBody) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.TEXT_XML);
    headers.setAccept(java.util.List.of(MediaType.TEXT_XML));
    headers.set("SOAPAction", "processDemo");

    return restTemplate.exchange(
        "http://localhost:" + port + DemoSoapTechnicalSender.SOAP_ENDPOINT,
        HttpMethod.POST,
        new HttpEntity<>(soapBody, headers),
        String.class);
  }

  private static String buildRequestEnvelope(
      String failureStep, String failureType, Integer failureStatus) {
    String failureNode = "";
    if (failureStep != null || failureType != null || failureStatus != null) {
      String stepNode = failureStep == null ? "" : "<dem:step>" + failureStep + "</dem:step>";
      String typeNode = failureType == null ? "" : "<dem:type>" + failureType + "</dem:type>";
      String statusNode =
          failureStatus == null ? "" : "<dem:status>" + failureStatus + "</dem:status>";
      String bodyNode = "";
      failureNode =
          """
              <dem:failure>
                %s
                %s
                %s
                %s
              </dem:failure>
              """
              .formatted(stepNode, typeNode, statusNode, bodyNode);
    }

    return """
          <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:dem="urn:error-handling-demo:soap">
            <soap:Body>
              <dem:processDemo>
                <dem:request>
                  <dem:payload>demo</dem:payload>
                </dem:request>
                %s
              </dem:processDemo>
            </soap:Body>
          </soap:Envelope>
          """
        .formatted(failureNode);
  }

  private static String xpath(String xml, String expression) throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    Document document =
        factory
            .newDocumentBuilder()
            .parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    return XPathFactory.newInstance().newXPath().evaluate(expression, document);
  }
}
