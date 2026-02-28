package cz.bvv.errorhandlingdemo.builder.sender.demosoap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.bvv.errorhandlingdemo.builder.common.soap.SoapXmlSupport;
import cz.bvv.errorhandlingdemo.builder.sender.demosoap.model.DemoSoapResponse;
import cz.bvv.errorhandlingdemo.builder.sender.demosoap.model.SoapError;
import cz.bvv.errorhandlingdemo.builder.sender.demosoap.model.SoapFailureTrigger;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.camel.Exchange;
import javax.xml.parsers.DocumentBuilderFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@Component
public class DemoSoapMessageMapper {
    private static final String HEADER_THROW_IN = "X_THROW_IN";
    private static final String HEADER_THROW_TYPE = "X_THROW_TYPE";
    private static final String HEADER_THROW_STATUS = "X_THROW_STATUS";
    private static final String HEADER_THROW_BODY = "X_THROW_BODY";
    private static final String DEMO_NS = "urn:error-handling-demo:soap";
    private static final String DETAIL_NS = "urn:error-handling-demo:soap:fault";
    private final ObjectMapper objectMapper;

    public DemoSoapMessageMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String readBody(Exchange exchange) throws Exception {
        Object body = exchange.getMessage().getBody();
        if (body instanceof String textBody) {
            return textBody;
        }
        if (body instanceof InputStream stream) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
        return exchange.getMessage().getBody(String.class);
    }

    public void applyFailureHeaders(Exchange exchange, String soapBodyXml) throws Exception {
        if (soapBodyXml == null || soapBodyXml.isBlank()) {
            return;
        }
        SoapFailureTrigger trigger = parseFailureTrigger(soapBodyXml);
        if (trigger.step() != null) {
            exchange.getMessage().setHeader(HEADER_THROW_IN, trigger.step());
        }
        if (trigger.type() != null) {
            exchange.getMessage().setHeader(HEADER_THROW_TYPE, trigger.type());
        }
        if (trigger.status() != null) {
            exchange.getMessage().setHeader(HEADER_THROW_STATUS, trigger.status().toString());
        }
        if (trigger.body() != null) {
            exchange.getMessage().setHeader(HEADER_THROW_BODY, trigger.body());
        }
    }

    public SoapFailureTrigger parseFailureTrigger(String soapBodyXml) throws Exception {
        if (soapBodyXml == null || soapBodyXml.isBlank()) {
            return new SoapFailureTrigger(null, null, null, null);
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document document = factory.newDocumentBuilder().parse(
          new ByteArrayInputStream(soapBodyXml.getBytes(StandardCharsets.UTF_8))
        );

        NodeList failures = document.getElementsByTagNameNS("*", "failure");
        if (failures.getLength() == 0 || !(failures.item(0) instanceof Element failure)) {
            return new SoapFailureTrigger(null, null, null, null);
        }

        String step = defaultIfNull(
          findDirectValue(failure, "step"),
          findDirectValue(failure, "throwIn"));
        String type = defaultIfNull(
          findDirectValue(failure, "type"),
          findDirectValue(failure, "throwType"));
        String statusToken = defaultIfNull(
          findDirectValue(failure, "status"),
          findDirectValue(failure, "throwStatus"));
        String body = defaultIfNull(
          findDirectValue(failure, "body"),
          findDirectValue(failure, "throwBody"));
        Integer status = null;
        if (statusToken != null && !statusToken.isBlank()) {
            try {
                status = Integer.valueOf(statusToken.trim());
            } catch (NumberFormatException ignored) {
            }
        }

        return new SoapFailureTrigger(step, type, status, body);
    }

    public String createSuccessResponseXml(DemoSoapResponse response) throws Exception {
        Document document = SoapXmlSupport.newDocument();

        Element envelope = document.createElementNS(SoapXmlSupport.SOAP_NS, "soap:Envelope");
        Element body = document.createElementNS(SoapXmlSupport.SOAP_NS, "soap:Body");
        Element operationResponse = document.createElementNS(DEMO_NS, "dem:processDemoResponse");
        Element payloadNode = document.createElementNS(DEMO_NS, "dem:response");
        Element statusNode = document.createElementNS(DEMO_NS, "dem:status");

        document.appendChild(envelope);
        envelope.appendChild(body);
        body.appendChild(operationResponse);
        operationResponse.appendChild(payloadNode);
        payloadNode.appendChild(statusNode);
        statusNode.setTextContent(response.getStatus());

        return SoapXmlSupport.toXmlString(document);
    }

    public String createSuccessResponseXml(String downstreamBody) throws Exception {
        String status = "ok";
        if (downstreamBody != null && !downstreamBody.isBlank()) {
            try {
                JsonNode jsonBody = objectMapper.readTree(downstreamBody);
                status = jsonBody.path("status").asText("ok");
            } catch (Exception ignored) {
                status = "ok";
            }
        }

        return createSuccessResponseXml(new DemoSoapResponse(status));
    }

    public String createFailureResponseXml(String faultString, SoapError soapError) throws Exception {
        SoapXmlSupport.SoapFaultSkeleton skeleton = SoapXmlSupport.newSoapFaultSkeleton(
          faultString,
          DETAIL_NS,
          "err:soapError"
        );
        Document document = skeleton.document();
        Element soapErrorNode = skeleton.detailRoot();
        Element failuresNode = document.createElementNS(DETAIL_NS, "err:failures");

        soapErrorNode.appendChild(failuresNode);

        if (soapError.failures() != null) {
            for (SoapError.Failure failure : soapError.failures()) {
                Element failureNode = document.createElementNS(DETAIL_NS, "err:failure");
                Element idNode = document.createElementNS(DETAIL_NS, "err:id");
                Element failureTextNode = document.createElementNS(DETAIL_NS, "err:failureText");
                idNode.setTextContent(failure.id());
                failureTextNode.setTextContent(failure.failureText());
                failureNode.appendChild(idNode);
                failureNode.appendChild(failureTextNode);
                failuresNode.appendChild(failureNode);
            }
        }

        return SoapXmlSupport.toXmlString(document);
    }

    private static String findDirectValue(Element parent, String localName) {
        NodeList nodeList = parent.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (!(nodeList.item(i) instanceof Element child)) {
                continue;
            }
            if (localName.equals(child.getLocalName())) {
                return child.getTextContent();
            }
        }
        return null;
    }

    private static String defaultIfNull(String value, String fallbackValue) {
        return value == null ? fallbackValue : value;
    }

}
