package cz.bvv.errorhandlingdemo.builder.common.soap;

import cz.bvv.errorhandlingdemo.builder.common.soap.model.SoapErrorEntry;
import java.io.StringWriter;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Component
public class SoapFaultMessageMapper {
    private static final String SOAP_NS = "http://schemas.xmlsoap.org/soap/envelope/";
    private static final String DETAIL_NS = "urn:error-handling-demo:soap:fault";

    public String toSoapFaultXml(String faultString, List<SoapErrorEntry> errors) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        Document document = factory.newDocumentBuilder().newDocument();
        Element envelope = document.createElementNS(SOAP_NS, "soap:Envelope");
        Element body = document.createElementNS(SOAP_NS, "soap:Body");
        Element fault = document.createElementNS(SOAP_NS, "soap:Fault");
        Element faultCode = document.createElement("faultcode");
        Element faultStringNode = document.createElement("faultstring");
        Element detail = document.createElement("detail");
        Element errorsNode = document.createElementNS(DETAIL_NS, "err:errors");

        envelope.appendChild(body);
        body.appendChild(fault);
        fault.appendChild(faultCode);
        fault.appendChild(faultStringNode);
        fault.appendChild(detail);
        detail.appendChild(errorsNode);
        document.appendChild(envelope);

        faultCode.setTextContent("soap:Server");
        faultStringNode.setTextContent(faultString);

        for (SoapErrorEntry error : errors) {
            Element errorNode = document.createElementNS(DETAIL_NS, "err:error");
            Element codeNode = document.createElementNS(DETAIL_NS, "err:code");
            Element messageNode = document.createElementNS(DETAIL_NS, "err:message");
            codeNode.setTextContent(error.code());
            messageNode.setTextContent(error.message());
            errorNode.appendChild(codeNode);
            errorNode.appendChild(messageNode);
            errorsNode.appendChild(errorNode);
        }

        return toXmlString(document);
    }

    private static String toXmlString(Document document) throws Exception {
        StringWriter writer = new StringWriter();
        var transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        return writer.toString();
    }
}
