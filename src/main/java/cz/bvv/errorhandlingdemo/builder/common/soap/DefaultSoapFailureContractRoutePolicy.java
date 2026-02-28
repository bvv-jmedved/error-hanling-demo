package cz.bvv.errorhandlingdemo.builder.common.soap;

import cz.bvv.errorhandlingdemo.builder.common.failure.IntegrationExceptionMapper;
import cz.bvv.errorhandlingdemo.builder.common.soap.model.DefaultSoapError;
import cz.bvv.errorhandlingdemo.builder.common.soap.model.SoapErrorEntry;
import cz.bvv.errorhandlingdemo.exception.IntegrationException;
import java.util.List;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class DefaultSoapFailureContractRoutePolicy
  extends SoapFailureContractRoutePolicy<DefaultSoapError> {

    public DefaultSoapFailureContractRoutePolicy(
      SoapFaultMessageMapper soapFaultMessageMapper,
      IntegrationExceptionMapper integrationExceptionMapper) {
        super(soapFaultMessageMapper, integrationExceptionMapper);
    }

    @Override
    protected DefaultSoapError createSoapError(IntegrationException exception) {
        return new DefaultSoapError(
          exception.getErrors().stream()
            .map(error -> new SoapErrorEntry(error.code(), error.message()))
            .toList()
        );
    }

    @Override
    protected List<SoapErrorEntry> getSoapErrors(DefaultSoapError errorDto) {
        return errorDto.errors();
    }
}
