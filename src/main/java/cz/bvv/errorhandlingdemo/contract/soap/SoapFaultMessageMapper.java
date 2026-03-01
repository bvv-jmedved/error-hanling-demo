package cz.bvv.errorhandlingdemo.contract.soap;

import cz.bvv.errorhandlingdemo.contract.soap.model.SoapErrorEntry;
import cz.bvv.errorhandlingdemo.support.soap.SoapXmlSupport;
import java.util.List;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Component
public class SoapFaultMessageMapper {
    private static final String DETAIL_NS = "urn:error-handling-demo:soap:fault";

    public String toSoapFaultXml(String faultString, List<SoapErrorEntry> errors) throws Exception {
        SoapXmlSupport.SoapFaultSkeleton skeleton = SoapXmlSupport.newSoapFaultSkeleton(
          faultString,
          DETAIL_NS,
          "err:errors"
        );
        Document document = skeleton.document();
        Element errorsNode = skeleton.detailRoot();

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

        return SoapXmlSupport.toXmlString(document);
    }
}
