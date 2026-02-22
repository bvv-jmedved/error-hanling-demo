package cz.bvv.errorhandlingdemo.builder.process;

import cz.bvv.errorhandlingdemo.builder.common.BaseProcessRouteBuilder;
import cz.bvv.errorhandlingdemo.poc.FailureInjector;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DemoProcessRouteBuilder extends BaseProcessRouteBuilder {
    private final ObjectProvider<FailureInjector> processTransformRequestFailureInjectorObjectProvider;
    private final ObjectProvider<FailureInjector> processTransformResponseFailureInjectorObjectProvider;

    public DemoProcessRouteBuilder(
      @Qualifier("processTransformRequestFailureInjector")
      ObjectProvider<FailureInjector> processTransformRequestFailureInjectorObjectProvider,
      @Qualifier("processTransformResponseFailureInjector")
      ObjectProvider<FailureInjector> processTransformResponseFailureInjectorObjectProvider) {
        this.processTransformRequestFailureInjectorObjectProvider = processTransformRequestFailureInjectorObjectProvider;
        this.processTransformResponseFailureInjectorObjectProvider = processTransformResponseFailureInjectorObjectProvider;
    }

    @Override
    protected void config() {

        from("direct:demo-process")
          .routeId("demo-process")
          .process(exchange -> processTransformRequestFailureInjectorObjectProvider.ifAvailable(
            failureInjector -> failureInjector.process(exchange)))
          .log("Processor. Processing request: ${body}")
          .to("direct:demo-receiver?exchangePattern=InOut")
          .process(exchange -> processTransformResponseFailureInjectorObjectProvider.ifAvailable(
            failureInjector -> failureInjector.process(exchange)))
          .log("Processor. Processing response: ${body}");

    }

  }

