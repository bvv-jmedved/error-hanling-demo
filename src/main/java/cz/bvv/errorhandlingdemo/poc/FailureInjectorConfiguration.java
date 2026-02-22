package cz.bvv.errorhandlingdemo.poc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("poc")
public class FailureInjectorConfiguration {

    @Bean
    public FailureInjector senderValidateFailureInjector() {
        return new FailureInjector("sender-validate");
    }

    @Bean
    public FailureInjector processTransformRequestFailureInjector() {
        return new FailureInjector("process-transform-request");
    }

    @Bean
    public FailureInjector processTransformResponseFailureInjector() {
        return new FailureInjector("process-transform-response");
    }

    @Bean
    public FailureInjector technicalCallFailureInjector() {
        return new FailureInjector("technical-call");
    }
}
