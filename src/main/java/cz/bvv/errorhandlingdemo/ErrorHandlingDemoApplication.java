package cz.bvv.errorhandlingdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot entry point for the Camel error-handling architecture demo.
 *
 * <p>The application boots all route builders, policy modules, and PoC helpers used to demonstrate
 * deterministic policy composition.
 */
@SpringBootApplication
public class ErrorHandlingDemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(ErrorHandlingDemoApplication.class, args);
  }
}
