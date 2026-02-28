package cz.bvv.errorhandlingdemo.builder.common.soap;

import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public final class SoapXmlSupport {
    public static final String SOAP_NS = "http://schemas.xmlsoap.org/soap/envelope/";

    private SoapXmlSupport() {
    }

    public static Document newDocument() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder().newDocument();
    }

    public static SoapFaultSkeleton newSoapFaultSkeleton(
      String faultString,
      String detailNamespace,
      String detailRootQualifiedName) throws Exception {
        Document document = newDocument();
        Element envelope = document.createElementNS(SOAP_NS, "soap:Envelope");
        Element body = document.createElementNS(SOAP_NS, "soap:Body");
        Element fault = document.createElementNS(SOAP_NS, "soap:Fault");
        Element faultCode = document.createElement("faultcode");
        Element faultStringNode = document.createElement("faultstring");
        Element detail = document.createElement("detail");
        Element detailRoot = document.createElementNS(detailNamespace, detailRootQualifiedName);

        envelope.appendChild(body);
        body.appendChild(fault);
        fault.appendChild(faultCode);
        fault.appendChild(faultStringNode);
        fault.appendChild(detail);
        detail.appendChild(detailRoot);
        document.appendChild(envelope);

        faultCode.setTextContent("soap:Server");
        faultStringNode.setTextContent(faultString);
        return new SoapFaultSkeleton(document, detailRoot);
    }

    public static String toXmlString(Document document) throws Exception {
        StringWriter writer = new StringWriter();
        var transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        return writer.toString();
    }

    public record SoapFaultSkeleton(Document document, Element detailRoot) {
    }
}
