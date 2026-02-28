package cz.bvv.errorhandlingdemo;

import static org.assertj.core.api.Assertions.assertThat;

import cz.bvv.errorhandlingdemo.builder.sender.demosoap.DemoSoapFailureContractRoutePolicy;
import cz.bvv.errorhandlingdemo.builder.sender.demosoap.DemoSoapMessageMapper;
import cz.bvv.errorhandlingdemo.builder.sender.demosoap.model.SoapError;
import cz.bvv.errorhandlingdemo.builder.technicalsender.DemoSoapTechnicalSender;
import cz.bvv.errorhandlingdemo.builder.common.failure.IntegrationExceptionMapper;
import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("poc")
@Import(DemoSoapFailureContractFallbackIntegrationTest.MappingFailureConfig.class)
class DemoSoapFailureContractFallbackIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldFallbackToInternalErrorSoapFaultWhenSoapMappingFails() throws Exception {
        ResponseEntity<String> response = callSoap("""
          <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:dem="urn:error-handling-demo:soap">
            <soap:Body>
              <dem:processDemo>
                  <dem:request>
                    <dem:payload>demo</dem:payload>
                  </dem:request>
                  <dem:failure>
                    <dem:step>technical-call</dem:step>
                    <dem:type>http</dem:type>
                    <dem:status>500</dem:status>
                  </dem:failure>
              </dem:processDemo>
            </soap:Body>
          </soap:Envelope>
          """);

        assertThat(response.getBody()).isNotBlank();
        assertThat(xpath(response.getBody(), "//*[local-name()='Fault']")).isNotBlank();
        assertThat(xpath(response.getBody(), "//*[local-name()='faultstring']/text()")).isNotBlank();
        assertThat(xpath(response.getBody(), "(//*[local-name()='failure'])[1]/*[local-name()='id']/text()"))
          .isEqualTo("INTERNAL_ERROR");
        assertThat(xpath(response.getBody(), "(//*[local-name()='failure'])[1]/*[local-name()='failureText']/text()"))
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
          String.class
        );
    }

    private static String xpath(String xml, String expression) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document document = factory.newDocumentBuilder().parse(
          new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))
        );
        return XPathFactory.newInstance().newXPath().evaluate(expression, document);
    }

    @TestConfiguration
    static class MappingFailureConfig {
        @Bean
        @Primary
        DemoSoapFailureContractRoutePolicy failingSoapFailureContractRoutePolicy(
          DemoSoapMessageMapper demoSoapMessageMapper,
          IntegrationExceptionMapper integrationExceptionMapper) {
            return new DemoSoapFailureContractRoutePolicy(demoSoapMessageMapper, integrationExceptionMapper) {
                @Override
                protected SoapError createSoapError(IntegrationException exception) {
                    throw new IllegalStateException("forced SOAP mapping failure");
                }
            };
        }
    }
}
